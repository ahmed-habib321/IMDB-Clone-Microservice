package org.example.searchservice.event;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sharedmodule.Constants.TOPIC_NAMES;
import org.example.sharedmodule.people_service.event.PersonIndexEvent;
import org.example.sharedmodule.title_service.event.TitleIndexEvent;
import org.example.sharedmodule.utils.LogUtils;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static org.example.sharedmodule.Constants.SERVICE_NAMES.SEARCH_SERVICE;

@Component
@RequiredArgsConstructor
@Slf4j
public class SearchEventConsumer {

    private final ElasticsearchClient es;

    @Async
    @KafkaListener(topics = TOPIC_NAMES.PERSON_INDEXED)
    public void onPersonIndexed(PersonIndexEvent event) {
        try {
            LogUtils.logEventConsumed(SEARCH_SERVICE, TOPIC_NAMES.PERSON_INDEXED);
            es.index(i -> i.index("people").id(event.personId().toString()).document(event));
        } catch (Exception e) {
            log.error("Failed to process {} event for personId={}", TOPIC_NAMES.PERSON_INDEXED, event.personId(), e);
        }
    }

    @Async
    @KafkaListener(topics = TOPIC_NAMES.TITLE_INDEXED)
    public void onTitleIndexed(TitleIndexEvent event) {
        try {
            LogUtils.logEventConsumed(SEARCH_SERVICE, TOPIC_NAMES.TITLE_INDEXED);
            es.index(i -> i.index("titles").id(event.titleId().toString()).document(event));
        } catch (Exception e) {
            log.error("Failed to process {} event for titleId={}", TOPIC_NAMES.TITLE_INDEXED, event.titleId(), e);
        }
    }
}