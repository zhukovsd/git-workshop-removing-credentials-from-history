package ru.prplhd.weather.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.prplhd.weather.service.SessionService;

@Component
@RequiredArgsConstructor
public class ExpiredSessionCleanupScheduler {

    private final SessionService sessionService;

    @Scheduled(fixedDelay = 3_600_000)
    public void cleanupExpiredSessions() {
        sessionService.deleteExpiredSessions();
    }
}
