package org.example.peopleservice.client;

import org.example.sharedmodule.title_service.dto.TitleMiniResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@FeignClient(name = "title-service-client", path = "/api/v1/title", fallbackFactory = TitleServiceClientFallbackFactory.class)
public interface TitleServiceClient {

    @PostMapping("/search")
    List<TitleMiniResponse> fetchTitleSummaries(@RequestBody List<String> ids);

    default List<TitleMiniResponse> fetchTitles(Collection<UUID> ids) {
        if (ids.isEmpty()) return List.of();
        return fetchTitleSummaries(ids.stream().map(UUID::toString).toList());
    }
}