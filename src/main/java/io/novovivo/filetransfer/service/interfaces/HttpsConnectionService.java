package io.novovivo.filetransfer.service.interfaces;

import io.novovivo.filetransfer.dto.DowloadFileRequestDTO;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import javax.net.ssl.HttpsURLConnection;
import org.apache.http.HttpEntity;

/**
 * @Author：Zuochao（Edward）Dou
 */

public interface HttpsConnectionService {
    HttpsURLConnection getHttpsConnection(String destinationUrl) throws IOException, KeyManagementException, NoSuchAlgorithmException;
    void download(DowloadFileRequestDTO requestDTO) throws Exception;
    String notifyToDownload(DowloadFileRequestDTO requestDTO) throws NoSuchAlgorithmException, IOException, KeyManagementException;
    int sendAsync(String url, Object object);
    String sendHttpSynced(String url, Object object) throws IOException;
    String sendHttpSyncedWithHeader(String url, Object object, HashMap<String, String> headerMap) throws IOException;
    String sendHttpsSyncedWithHeader(String url, Object object, HashMap<String, String> headerMap) throws IOException;
    String sendHttpSyncedMultiPartFiles(String url, InputStream inputStream, String contentType, String studyId) throws IOException;
    String sendMultiPartFile(HttpEntity multipartEntity, HttpsURLConnection connection) throws IOException;
    String sendHttpSynced(String url, String dataStr) throws IOException;
}
