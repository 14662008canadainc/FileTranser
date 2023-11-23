package io.novovivo.filetransfer.service.implementations;

import io.novovivo.filetransfer.service.interfaces.FilesStorageService;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author：Zuochao（Edward）Dou
 */

@Service
public class FilesStorageServiceImpl implements FilesStorageService {

    private String path = "files/uploads";

    @Override
    public void save(MultipartFile file) {
        try {
            Files.copy(file.getInputStream(), Paths.get(path).resolve(file.getOriginalFilename()));
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

    @Override
    public Resource load(String filename) {
        try {
            Path file;
            file = Paths.get(path).resolve(filename);
            //System.out.println(filename);
            //System.out.println(file.toUri());
            Resource resource = new UrlResource(file.toUri());
            //System.out.println(resource.exists());
            //System.out.println(resource.isReadable());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(Paths.get(path).toFile());
    }

}
