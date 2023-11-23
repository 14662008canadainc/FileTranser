package io.novovivo.filetransfer.service.interfaces;

import io.novovivo.filetransfer.dto.DowloadFileRequestDTO;
import javax.net.ssl.SSLServerSocketFactory;

/**
 * @Author: Zuochao(Edward) Dou
 */

public interface FileExchangeService {
    void sendFile(DowloadFileRequestDTO requestDTO);
    void initSocketServers(SSLServerSocketFactory ssf,int port);
    int getOneFreePort();
}
