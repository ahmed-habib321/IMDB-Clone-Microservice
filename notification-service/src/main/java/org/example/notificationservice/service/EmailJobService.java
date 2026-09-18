package org.example.notificationservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.notificationservice.enums.NotificationTemplate;
import org.example.notificationservice.mapper.NotificationMapper;
import org.example.sharedmodule.notification_service.exception.EmailJobSerializationException;
import org.example.notificationservice.repository.EmailJobRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailJobService {

    private final EmailJobRepository emailJobRepository;
    private final ObjectMapper objectMapper;
    private final NotificationMapper notificationMapper;

    @Transactional
    public void createEmailJob(
            UUID messageId,
            String recipient,
            NotificationTemplate template,
            Map<String, Object> variables
    ) {
        try {
            String variablesJson = objectMapper.writeValueAsString(variables);

            emailJobRepository.save(
                    notificationMapper.emailJob(messageId, recipient, template, variablesJson));
        } catch (JsonProcessingException e) {
            throw new EmailJobSerializationException(e);
        }
    }
}
