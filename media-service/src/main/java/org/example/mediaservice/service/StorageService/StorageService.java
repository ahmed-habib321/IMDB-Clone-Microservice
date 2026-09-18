package org.example.mediaservice.service.StorageService;


import java.io.IOException;
import java.io.InputStream;

public interface StorageService {

    void Delete(String key) throws IOException;

    void Upload(String key, InputStream stream, String contentType, long size);

    String getUrl(String key);
}
