package org.example.mediaservice.service.StorageService;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

@Service
@Slf4j
@ConditionalOnProperty(name = "storage.type", havingValue = "local", matchIfMissing = true)
public class LocalStorageService implements StorageService {

    @Value("${storage.path:uploads}")
    private String storagePath;

    @Value("${storage.public-url:http://localhost:8080/media}")
    private String publicUrl;

    private Path rootPath;

    @PostConstruct
    public void init() {
        try {
            rootPath = Paths.get(storagePath).toAbsolutePath().normalize();
            Files.createDirectories(rootPath);

            log.info("Local storage initialized at {}", rootPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize local storage", e);
        }
    }

    @Override
    public void Upload(String key,
                       InputStream stream,
                       String contentType,
                       long size) {
        try {
            Path filePath = rootPath.resolve(key).normalize();

            // Create parent directories if needed
            Files.createDirectories(filePath.getParent());

            Files.copy(
                    stream,
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file: " + key, e);
        }
    }

    @Override
    public void Delete(String key) throws IOException {
        Path filePath = rootPath.resolve(key).normalize();
        Files.deleteIfExists(filePath);
    }

    @Override
    public String getUrl(String key) {
        return publicUrl + "/" + key;
    }
}