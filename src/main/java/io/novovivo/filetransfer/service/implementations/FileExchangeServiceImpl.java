package io.novovivo.filetransfer.service.implementations;

import com.sun.imageio.plugins.common.InputStreamAdapter;
import io.novovivo.filetransfer.dto.DowloadFileRequestDTO;
import io.novovivo.filetransfer.model.LocalSocketServerPorts;
import io.novovivo.filetransfer.repository.LocalSocketServerPortsRepository;
import io.novovivo.filetransfer.repository.RemoteSocketServerPortsRepository;
import io.novovivo.filetransfer.service.interfaces.FileExchangeService;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.UUID;
import javax.net.ssl.SSLServerSocketFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @Author: Zuochao(Edward) Dou
 */
@Slf4j
@Service
public class FileExchangeServiceImpl implements FileExchangeService {
    private final LocalSocketServerPortsRepository localSocketServerPortsRepository;

    public FileExchangeServiceImpl(LocalSocketServerPortsRepository localSocketServerPortsRepository) {
        this.localSocketServerPortsRepository = localSocketServerPortsRepository;
    }

    @Override
    @Async
    public void sendFile(DowloadFileRequestDTO requestDTO) {

    }

    @Override
    @Async
    public void initSocketServers(SSLServerSocketFactory ssf, int port) {
        try {
            ServerSocket ss = ssf.createServerSocket(port);
            while(true){
                try{
                    log.info("Socket server listen on <{}> ... ...", port);
                    Socket s = ss.accept();
                    OutputStream out = s.getOutputStream();
                    InputStream in = s.getInputStream();
                    String tempChunkId = UUID.randomUUID().toString();
                    OutputStream os = new FileOutputStream(new File("./files/downloads/" + tempChunkId));
                    byte[] buf = new byte[8192];
                    int len;
                    while((len = in.read(buf))>0){
                        os.write(buf,0,len);
                    }
                    in.close();
                    os.close();
                    LocalSocketServerPorts serverPorts = localSocketServerPortsRepository.findByPort(port);
                    serverPorts.setSessionId(tempChunkId);
                    localSocketServerPortsRepository.save(serverPorts);
                    StringBuffer buffer = new StringBuffer();
                    buffer.append(tempChunkId);
                    String string = buffer.toString();
                    byte[] data = string.getBytes();
                    out.write("HTTP/1.0 200 OK\n".getBytes());
                    out.write(new String("Content-Length: " + data.length + "\n").getBytes());
                    out.write("Content-Type: text/plain\n\n".getBytes());
                    out.write(data);
                    out.flush();
                    out.close();
                    s.close();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.info("Create socket server with port <{}> failed!",port);
        }

    }

    @Override public int getOneFreePort() {
        return 0;
    }

}
