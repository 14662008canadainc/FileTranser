package io.novovivo.filetransfer.controller;

import io.novovivo.filetransfer.dto.DowloadFileRequestDTO;
import io.novovivo.filetransfer.dto.GetFileListRequestDTO;
import io.novovivo.filetransfer.dto.GetFileListResponseDTO;
import io.novovivo.filetransfer.model.ChunkedFile;
import io.novovivo.filetransfer.repository.ChunkedFileRepository;
import io.novovivo.filetransfer.service.interfaces.FileExchangeService;
import io.novovivo.filetransfer.service.interfaces.HttpsConnectionService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Zuochao(Edward) Dou
 */
@Slf4j
@CrossOrigin
@RestController
public class NotificationController {
    private final HttpsConnectionService httpsConnectionService;
    private final FileExchangeService fileExchangeService;
    private final ChunkedFileRepository chunkedFileRepository;

    @Autowired public NotificationController(HttpsConnectionService httpsConnectionService, FileExchangeService fileExchangeService,
        ChunkedFileRepository chunkedFileRepository) {
        this.httpsConnectionService = httpsConnectionService;
        this.fileExchangeService = fileExchangeService;
        this.chunkedFileRepository = chunkedFileRepository;
    }

    @PostMapping("local/ready")
    public ResponseEntity<String> localFileReady(@RequestBody DowloadFileRequestDTO requestDTO) throws Exception {
        log.info("Notifying the other party to download file <" + requestDTO.getFilename() + ">... ...");
        /*
        long start = System.currentTimeMillis();
        FileOutputStream os = new FileOutputStream("./files/uploads/" + requestDTO.getFilename() + ".zip");
        ZipOutputStream zipOut = new ZipOutputStream(os);
        File fileToZip = new File("./files/uploads/" + requestDTO.getFilename());
        FileInputStream in = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
        zipOut.putNextEntry(zipEntry);
        byte[] bytes =  new byte[8192];
        int len;
        while((len = in.read(bytes))>0){
            zipOut.write(bytes,0,len);
        }
        zipOut.close();
        in.close();
        os.close();
        long end = System.currentTimeMillis();
        log.info("Extra zip time is <{}> ms.", end-start);
        */
        return ResponseEntity.ok(httpsConnectionService.notifyToDownload(requestDTO));
    }

    @PostMapping("remote/ready")
    public String downloadFile(@RequestBody DowloadFileRequestDTO requestDTO) throws Exception {
        log.info("Starting downloading file <" + requestDTO.getFilename() + ">... ...");
        httpsConnectionService.download(requestDTO);
        return "OK! Starting transfer file <" + requestDTO.getFilename() + ">... ...";
    }

    @PostMapping("file/list")
    public GetFileListResponseDTO getFileList(@RequestBody GetFileListRequestDTO requestDTO) throws Exception {
        log.info("Fetch <{}> from database ... ...", requestDTO.getType());
        GetFileListResponseDTO responseDTO = new GetFileListResponseDTO();
        List<ChunkedFile> chunkedFiles = chunkedFileRepository.findByType(requestDTO.getType());
        responseDTO.setFileList(chunkedFiles);
        return responseDTO;
    }


    /**
     * simulate C++ server
     * @param requestDTO
     * @return
     * @throws Exception
     */
    @PostMapping("file/ready")
    public String fileReadyToReadAndProcess(@RequestBody DowloadFileRequestDTO requestDTO) throws Exception {
        log.info("Starting read and process file <" + requestDTO.getFilename() + ">... ...");
        return "OK! Starting read and process file <" + requestDTO.getFilename() + ">... ...";
    }
}
