package org.example.notificationservice.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.example.notificationservice.service.EmailService;
import org.example.notificationservice.service.impl.EmailServiceImpl;
import org.example.notificationservice.service.impl.MockemailSender;

@Component
public class EmailBean {

    @Bean("emailService")
    @ConditionalOnProperty(name = "email.provider", havingValue = "MOCK", matchIfMissing = true)
    public EmailService mockEmailService() {
        return new MockemailSender();
    }

    @Bean("emailService")
    @ConditionalOnProperty(name = "email.provider", havingValue = "REAL")
    public EmailService realEmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        return new EmailServiceImpl(mailSender, templateEngine);
    }
}
