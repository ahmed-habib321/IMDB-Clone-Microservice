package org.example.peopleservice.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class TitleServiceClientFallbackFactory implements FallbackFactory<TitleServiceClient> {

    @Override
    public TitleServiceClient create(Throwable cause) {
        log.error("title-service call failed, returning empty filmography: {}", cause.getMessage());
        return ids -> List.of();
    }
}