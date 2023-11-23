package io.novovivo.filetransfer.runner;


import io.novovivo.filetransfer.model.LocalSocketServerPorts;
import io.novovivo.filetransfer.repository.LocalSocketServerPortsRepository;
import io.novovivo.filetransfer.service.interfaces.FileExchangeService;
import java.util.List;
import javax.net.ssl.SSLServerSocketFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class SecureSocketServerConfigRunner implements ApplicationRunner {
    private final FileExchangeService fileExchangeService;
    private final LocalSocketServerPortsRepository serverPortsRepository;

    @Autowired public SecureSocketServerConfigRunner(FileExchangeService fileExchangeService, LocalSocketServerPortsRepository serverPortsRepository) {
        this.fileExchangeService = fileExchangeService;
        this.serverPortsRepository = serverPortsRepository;
    }

    @Override
    public void run(ApplicationArguments var1) {
        /*
        List<LocalSocketServerPorts> serverPortsList = serverPortsRepository.findAll();
        SSLServerSocketFactory ssf = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
        serverPortsList.forEach(localSocketServerPorts -> {
           fileExchangeService.initSocketServers(ssf,localSocketServerPorts.getPort());
        });
        */
    }

}

