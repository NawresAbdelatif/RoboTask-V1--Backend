package com.example.acwa.controllers;

import com.example.acwa.entities.Notification;
import com.example.acwa.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public List<Notification> getMyNotifications(Authentication auth) {
        String username = auth.getName();
        return notificationService.getUserNotifications(username);
    }

    @GetMapping("/unread-count")
    public Map<String, Long> getUnreadCount(Authentication auth) {
        String username = auth.getName();
        long count = notificationService.countUnread(username);
        return Map.of("unreadCount", count);
    }

    @GetMapping("/unread")
    public List<Notification> getMyUnreadNotifications(Authentication auth) {
        String username = auth.getName();
        return notificationService.getUnreadNotifications(username);
    }

    @PostMapping("/clear")
    public void clearAll(Authentication auth) {
        String username = auth.getName();
        notificationService.markAllAsRead(username);
    }

    @PostMapping("/mark-all-read")
    public void markAllRead(Authentication auth) {
        String username = auth.getName();
        notificationService.markAllAsRead(username);
    }

}
