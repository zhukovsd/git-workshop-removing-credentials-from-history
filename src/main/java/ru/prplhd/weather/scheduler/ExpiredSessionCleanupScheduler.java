package ru.prplhd.weather.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.prplhd.weather.service.SessionService;

@Component
public class ExpiredSessionCleanupScheduler {

    private final SessionService sessionService;

    public ExpiredSessionCleanupScheduler(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Scheduled(fixedDelay = 3_600_000)
    public void cleanupExpiredSessions() {
        sessionService.deleteExpiredSessions();
    }
}
