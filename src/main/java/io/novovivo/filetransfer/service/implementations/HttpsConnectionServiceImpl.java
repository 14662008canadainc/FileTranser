package io.novovivo.filetransfer.service.implementations;

import io.novovivo.filetransfer.config.EnvironmentVariables;
import io.novovivo.filetransfer.dto.DowloadFileRequestDTO;
import io.novovivo.filetransfer.model.ChunkedFile;
import io.novovivo.filetransfer.repository.ChunkedFileRepository;
import io.novovivo.filetransfer.service.interfaces.HttpsConnectionService;
import io.novovivo.filetransfer.service.ultilities.RequestBodyUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.http.HttpEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @Author：Zuochao（Edward）Dou
 */
@Slf4j
@Service
public class HttpsConnectionServiceImpl implements HttpsConnectionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpsConnectionServiceImpl.class);
    @Autowired ChunkedFileRepository chunkedFileRepository;
    @Autowired EnvironmentVariables environmentVariables;
    @Override public HttpsURLConnection getHttpsConnection(String destinationUrl)
        throws IOException, KeyManagementException, NoSuchAlgorithmException {
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                @Override public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override public void checkClientTrusted(
                    X509Certificate[] certs, String authType) {
                }

                @Override public void checkServerTrusted(
                    X509Certificate[] certs, String authType) {
                }
            }
        };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HostnameVerifier allHostsValid = (hostname, session) -> true;
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        LOGGER.info("Send message to: {}", destinationUrl);
        URL url = new URL(destinationUrl);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setConnectTimeout(10*1000);
        connection.setSSLSocketFactory(sc.getSocketFactory());
        connection.setHostnameVerifier(allHostsValid);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        return connection;
    }

    @Override
    @Async
    public void download(DowloadFileRequestDTO requestDTO){
        HttpsURLConnection connection = null;
        try{
            connection = this.getHttpsConnection(environmentVariables.downloadFileUrl());
            OutputStreamWriter writer=new OutputStreamWriter(connection.getOutputStream());
            writer.write(requestDTO.toString());
            writer.flush();
            writer.close();
            int responseCode = connection.getResponseCode();
            //log.info("响应code：{}",responseCode);
            if(responseCode== HttpsURLConnection.HTTP_OK){
                long start = System.currentTimeMillis();
                InputStream is = connection.getInputStream();
                OutputStream os = new FileOutputStream(new File("./files/downloads/" + requestDTO.getFilename()));
                long mid = System.currentTimeMillis();
                byte[] buf = new byte[8192];
                int len;
                while((len = is.read(buf))>0){
                    os.write(buf,0,len);
                }
                is.close();
                os.close();
                /*
                String zipFile ="./files/downloads/" + requestDTO.getFilename() + ".zip";
                File destDir = new File("./files/downloads/");
                byte[] buffer = new byte[8192];
                ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
                ZipEntry zipEntry = zis.getNextEntry();
                while(zipEntry!=null){
                    File destFile = new File(destDir,zipEntry.getName());
                    FileOutputStream fos = new FileOutputStream(destFile);
                    int length;
                    while((length = zis.read(buffer))>0){
                        fos.write(buffer,0,length);
                    }
                    fos.close();
                    zipEntry = zis.getNextEntry();
                }*/
                long end = System.currentTimeMillis();
                //database set flag true
                //log.info( "Loading file <{}>, time used <{}> ms", requestDTO.getFilename(), (mid-start));
                log.info( "Finish downloading file <{}>, time used <{}> ms", requestDTO.getFilename(), (end-start));
                int failCount = 0;
                String ret = notifyFileReady(requestDTO);
                while("Connection failed".equals(ret) && failCount<=3){
                    Thread.sleep(1000*(failCount+1));
                    ret = notifyFileReady(requestDTO);
                    failCount++ ;
                }
                //log.info(notifyFileReady(requestDTO));
                ChunkedFile file = new ChunkedFile();
                file.setFilename(requestDTO.getFilename());
                if("Connection failed".equals(ret)){
                    file.setType("FailToNotify");
                }else{
                    file.setType("ExchangedFile");
                }
                chunkedFileRepository.save(file);
            } else {
                // database set flag false
                // log.info( "Failed!");
                log.info( "Download file <{}> failed: connection failed!", requestDTO.getFilename());
                ChunkedFile file = new ChunkedFile();
                file.setFilename(requestDTO.getFilename());
                file.setType("FailedToDownload");
                chunkedFileRepository.save(file);
            }
        }catch (Exception e){
            e.printStackTrace();
            log.info(chunkedFileRepository.findByFilename(requestDTO.getFilename()).get(0).getType());
        }finally {
            if(connection!=null){
                connection.disconnect();
            }
        }
    }

    @Override public String notifyToDownload(DowloadFileRequestDTO requestDTO) throws NoSuchAlgorithmException, IOException, KeyManagementException {
        List<ChunkedFile> chunkedFileList = chunkedFileRepository.findByFilename(requestDTO.getFilename());
        if(chunkedFileList != null && chunkedFileList.size()!=0){
            return "filename conflict!";
        }
        ChunkedFile file = new ChunkedFile();
        file.setFilename(requestDTO.getFilename());
        file.setType("LocalFile");
        chunkedFileRepository.save(file);
        int failCount = 0;
        String ret = send(requestDTO.toString(),this.getHttpsConnection(environmentVariables.notifyRemoteToDownloadFileUrl()));
        if("Connection failed".equals(ret) &&failCount<=3){
            try {
                Thread.sleep(1000*(failCount+1));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            send(requestDTO.toString(),this.getHttpsConnection(environmentVariables.notifyRemoteToDownloadFileUrl()));
            failCount++;
        }
        return ret;
    }
    private String notifyFileReady(DowloadFileRequestDTO requestDTO) throws NoSuchAlgorithmException, IOException, KeyManagementException {
        return send(requestDTO.toString(),this.getHttpsConnection(environmentVariables.notifyComputationToReadAndProcessUrl()));
    }
    private String send(String msg, HttpsURLConnection connection){
        String response;
        try{
            OutputStreamWriter writer=new OutputStreamWriter(connection.getOutputStream());
            writer.write(msg);
            writer.flush();
            writer.close();
            int responseCode = connection.getResponseCode();
            //log.info("响应code：{}",responseCode);
            if(responseCode== HttpsURLConnection.HTTP_OK){
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                br.close();
                response= sb.toString();
            } else {
                return "Connection failed";
            }
        }catch (Exception e){
            e.printStackTrace();
            return "Connection failed";
        }finally {
            connection.disconnect();
        }
        return response;
    }

    @Override
    public int sendAsync(String url, Object object) {
        OkHttpClient client = this.getAsyncOkHttpClient();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());
        Request request = new Request.Builder().url(url).post(body).build();
        LOGGER.info("Content sent to server: {}", object.toString());
        client.newCall(request)
            .enqueue(
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        call.cancel();
                        LOGGER.error("Something went wrong, connection failed!");
                        try {
                            // do something failure processing
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    @Override
                    public void onResponse(Call call, Response response){
                        if (response.isSuccessful()) {
                            String responseMsg= response.body() != null ? response.body().toString() : null;
                            LOGGER.info("Response code = {}, Response Body: {}", response.code(),responseMsg);
                        }else{
                            // do something failure processing
                        }
                        response.close();
                    }
                });
        return HttpURLConnection.HTTP_OK;
    }

    @Override public String sendHttpSynced(String url, Object object) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();
        RequestBody body=RequestBody.create(MediaType.parse("application/json"), object.toString());
        Request request=new Request.Builder().url(url).post(body).build();
        Call call=client.newCall(request);
        Response response=call.execute();
        String msg;
        if(response.code()== HttpsURLConnection.HTTP_OK){
            ResponseBody content=response.body();
            msg = content==null?null:content.string();
        } else {
            msg = "Connection Failed!";
        }
        response.close();
        return msg;
    }

    /**
     * 窦哥我这边为了不影响你的代码我这边重载一下sendHttpSynced方法
     * 今天测试区块上链原来你的object.toString()返回不一定是一个标准的json串格式
     * 导致post传参服务端解析对象异常所以重载以alibaba.fastjson.JSON将对象转成以上标准的json串
     * dataStr=alibaba.fastjson.JSON.toJSONString(object)
     */
     @Override
     public String sendHttpSynced(String url, String dataStr) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        RequestBody body=RequestBody.create(MediaType.parse("application/json"), dataStr);
        Request request=new Request.Builder().url(url).post(body).build();
        Call call=client.newCall(request);
        Response response=call.execute();
        String msg;
        if(response.code()== HttpsURLConnection.HTTP_OK){
            ResponseBody content=response.body();
            msg = content==null?null:content.string();
        } else {
            msg = "Connection Failed!";
        }
        response.close();
        return msg;
    }

    @Override public String sendHttpSyncedWithHeader(String url, Object object, HashMap<String, String> headerMap)
        throws IOException {
        OkHttpClient client=new OkHttpClient.Builder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();
        RequestBody body=RequestBody.create(MediaType.parse("application/json"), object.toString());
        Request request;
        if(headerMap.keySet().contains("Authorization")){
            request=new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", headerMap.get("Authorization"))
                .build();
        }else {
            request=new Request.Builder()
                .url(url)
                .post(body)
                .header("ts", headerMap.get("ts"))
                .header("token",headerMap.get("token"))
                .build();
        }

        Call call=client.newCall(request);
        Response response=call.execute();
        String msg;
        if(response.code()== HttpsURLConnection.HTTP_OK){
            ResponseBody content=response.body();
            msg = content==null?null:content.string();
        } else {
            System.out.println("Response: " + response.code() + ":" + response.message());
            msg = "Connection Failed!";
        }
        response.close();
        return msg;
    }

    @Override public String sendHttpsSyncedWithHeader(String url, Object object, HashMap<String, String> headerMap)
        throws IOException {
        OkHttpClient client= getAsyncOkHttpClient();
        RequestBody body=RequestBody.create(MediaType.parse("application/json"), object.toString());
        Request request;
        if(headerMap.keySet().contains("Authorization")){
            request=new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", headerMap.get("Authorization"))
                .build();
        }else {
            request=new Request.Builder()
                .url(url)
                .post(body)
                .header("ts", headerMap.get("ts"))
                .header("token",headerMap.get("token"))
                .build();
        }

        Call call=client.newCall(request);
        Response response=call.execute();
        String msg;
        if(response.code()== HttpsURLConnection.HTTP_OK){
            ResponseBody content=response.body();
            msg = content==null?null:content.string();
        } else {
            System.out.println("Response: " + response.code() + ":" + response.message());
            msg = "Connection Failed!";
        }
        response.close();
        return msg;
    }

    @Override
    public String sendHttpSyncedMultiPartFiles(String url, InputStream inputStream, String contentType, String studyId)
        throws IOException {
        OkHttpClient client=new OkHttpClient();

        RequestBody requestBody = RequestBodyUtil.create(MediaType.parse(contentType), inputStream);

        Request request=new Request.Builder()
            .url(url)
            .post(requestBody)
            .build();

        Call call=client.newCall(request);
        Response response=call.execute();
        String msg;
        if(response.code()== HttpsURLConnection.HTTP_OK){
            ResponseBody content=response.body();
            msg = content==null?null:content.string();
        } else {
            msg = "Connection Failed!";
        }
        response.close();
        return msg;
    }

    private OkHttpClient getAsyncOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType){ }
                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) { }
                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() { return new java.security.cert.X509Certificate[]{}; }
                }
            };
            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            OkHttpClient.Builder builder = new OkHttpClient.Builder().callTimeout(10, TimeUnit.SECONDS).connectTimeout(10, TimeUnit.SECONDS);
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);
            return builder.build();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Connection Failed!");
            //throw new RuntimeException(e);
        }
    }

    @Override
    public String sendMultiPartFile(HttpEntity multipartEntity, HttpsURLConnection connection) throws IOException {
        String response;
        OutputStream out = connection.getOutputStream();
        try{
            multipartEntity.writeTo(out);
            int code=connection.getResponseCode();
            if(code== HttpsURLConnection.HTTP_OK){
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                br.close();
                response = sb.toString();
            } else {
                LOGGER.info("Response Code : " + code);
                response = "Connection Failed!";
            }
        }catch (Exception e){
            e.printStackTrace();
            response =  "Connection Failed!";
        }finally {
            out.close();
            connection.disconnect();
        }
        return response;
    }
}
