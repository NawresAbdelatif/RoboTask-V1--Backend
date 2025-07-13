package com.example.acwa.security;
import com.example.acwa.entities.LogEntry;
import com.example.acwa.repositories.LogEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AuthenticationEventListener {

    @Autowired
    private LogEntryRepository logEntryRepository;

    @EventListener
    public void handleAuthenticationSuccess(AuthenticationSuccessEvent event) {
        LogEntry log = new LogEntry();
        LocalDateTime now = LocalDateTime.now();
        log.setTimestamp(now);
        log.setLoginDate(now.toLocalDate());
        log.setLevel("INFO");
        log.setMessage("Connexion réussie");
        log.setUsername(event.getAuthentication().getName());

        String ip = "UNKNOWN";

        if (event.getAuthentication().getDetails() instanceof WebAuthenticationDetails details) {
            ip = details.getRemoteAddress();
        }

        log.setIp(ip);
        logEntryRepository.save(log);
    }

    @EventListener
    public void handleAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
        LogEntry log = new LogEntry();
        LocalDateTime now = LocalDateTime.now();
        log.setTimestamp(now);
        log.setLoginDate(now.toLocalDate());
        log.setLevel("WARN");
        log.setMessage("Échec de connexion");
        log.setUsername(event.getAuthentication().getName());

        String ip = "UNKNOWN";

        if (event.getAuthentication().getDetails() instanceof WebAuthenticationDetails details) {
            ip = details.getRemoteAddress();
        }

        log.setIp(ip);
        logEntryRepository.save(log);
    }
}

