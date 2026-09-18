package org.example.titleservice.event;

import lombok.RequiredArgsConstructor;
import org.example.sharedmodule.rating_reviews_service.event.RatingAggregatedEvent;
import org.example.sharedmodule.utils.LogUtils;
import org.example.sharedmodule.utils.UUIDUtils;
import org.example.titleservice.repository.TitleRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static org.example.sharedmodule.Constants.SERVICE_NAMES.TITLE_SERVICE;
import static org.example.sharedmodule.Constants.TOPIC_NAMES.RATING_AGGREGATED;


@Component
@RequiredArgsConstructor
public class RatingEventConsumer {

    private final TitleRepository titleRepository;

    @KafkaListener(topics = RATING_AGGREGATED)
    @Transactional
    public void onRatingAggregated(RatingAggregatedEvent event) {
        LogUtils.logEventConsumed(TITLE_SERVICE, RATING_AGGREGATED);
        titleRepository.updateRatingStats(UUIDUtils.parse(event.titleId()), event.newRating(), event.newVoteCount());
    }
}
