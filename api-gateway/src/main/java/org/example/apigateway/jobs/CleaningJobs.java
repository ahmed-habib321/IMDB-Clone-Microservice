package org.example.apigateway.jobs;

import lombok.RequiredArgsConstructor;
import org.example.apigateway.repository.AccessTokenRepository;
import org.example.apigateway.repository.AuthCredentialRepository;
import org.example.apigateway.repository.RefreshTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class CleaningJobs {

    private final RefreshTokenRepository refreshTokenRepository;
    private final AccessTokenRepository accessTokenRepository;
    private final AuthCredentialRepository authCredentialRepository;

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void unlockUsers() {
        authCredentialRepository.unlockExpiredAccounts(Instant.now());
    }

    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void cleanExpiredRefreshTokens() {
        refreshTokenRepository.deleteByRevokedTrueOrExpiryDateBefore(Instant.now());
    }
}