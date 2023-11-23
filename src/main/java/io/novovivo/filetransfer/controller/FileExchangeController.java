package io.novovivo.filetransfer.controller;

import io.novovivo.filetransfer.dto.DowloadFileRequestDTO;
import io.novovivo.filetransfer.service.interfaces.FileExchangeService;
import io.novovivo.filetransfer.service.interfaces.FilesStorageService;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import javax.activation.MimetypesFileTypeMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
public class FileExchangeController {
    private final FilesStorageService filesStorageService;
    private final FileExchangeService fileExchangeService;

    @Autowired public FileExchangeController(FilesStorageService filesStorageService, FileExchangeService fileExchangeService) {
        this.filesStorageService = filesStorageService;
        this.fileExchangeService = fileExchangeService;
    }

    @PostMapping("file/download")
    public ResponseEntity<Resource> downloadFile(@RequestBody DowloadFileRequestDTO requestDTO){
        log.info("API service: send file to remote server... ...");
        Resource resource = filesStorageService.load(requestDTO.getFilename());
        String fileType = new MimetypesFileTypeMap().getContentType(resource.getFilename());
        //log.info("File Type is: " + fileType);
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(fileType + ";charset=UTF-8"))
            .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"" + new String(resource.getFilename().getBytes(
                StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1) + "\"")
            .body(resource);
    }



}
