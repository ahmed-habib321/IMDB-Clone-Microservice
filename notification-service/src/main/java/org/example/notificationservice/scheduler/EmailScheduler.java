package org.example.notificationservice.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.example.notificationservice.entity.EmailJob;
import org.example.notificationservice.enums.EmailJobStatus;
import org.example.notificationservice.repository.EmailJobRepository;
import org.example.notificationservice.service.EmailJobProcessor;

@Component
@RequiredArgsConstructor
public class EmailScheduler {

    private final EmailJobRepository emailJobRepository;
    private final EmailJobProcessor emailJobProcessor;

    @Scheduled(fixedDelay = 30000)
    public void processPendingEmails() {
        emailJobRepository.findTop100ByStatusOrderByCreatedAtAsc(EmailJobStatus.PENDING)
            .forEach(
                emailJob -> emailJobProcessor.process(emailJob.getId())
            );
    }
}
