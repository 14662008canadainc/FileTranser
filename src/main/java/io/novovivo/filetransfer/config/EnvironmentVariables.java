package io.novovivo.filetransfer.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author：Zuochao（Edward）Dou
 */

@Component
public class EnvironmentVariables {

    @Value("${COMPUTATION_SERVICE_NAME}")
    @Getter
    private String computationServiceName;

    @Value("${COMPUTATION_SERVICE_PORT}")
    @Getter
    private int computationServicePort;

    @Value("${REMOTE_IP}")
    @Getter
    private String remoteIp;

    @Value("${REMOTE_PORT}")
    @Getter
    private int remotePort;



    public String notifyRemoteToDownloadFileUrl(){
        return "https://" + this.remoteIp + ":" + this.remotePort + "/remote/ready";
    }

    public String downloadFileUrl(){
        return "https://" + this.remoteIp + ":" + this.remotePort + "/file/download";
    }

    public String notifyComputationToReadAndProcessUrl(){
        return "https://" + this.computationServiceName + ":" + this.computationServicePort + "/file/ready";
    }


}
