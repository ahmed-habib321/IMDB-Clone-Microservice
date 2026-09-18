package org.example.outbox.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaSender {

    private static final long SEND_TIMEOUT_SECONDS = 10;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public boolean send(String topic, String key, Object payload) {
        try {
            kafkaTemplate.send(topic, key, payload).get(SEND_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return kafkaUnavailable(topic, key, new OutboxKafkaException("Kafka send interrupted", e));
        } catch (ExecutionException e) {
            return kafkaUnavailable(topic, key, new OutboxKafkaException("Kafka send failed", e.getCause()));
        } catch (TimeoutException e) {
            return kafkaUnavailable(topic, key, new OutboxKafkaException("Kafka send timed out", e));
        }
    }

    private boolean kafkaUnavailable(String topic, String key, OutboxKafkaException ex) {
        log.warn("Kafka unavailable for topic={}, key={}: {}", topic, key, ex.getMessage());
        return false;
    }
}
