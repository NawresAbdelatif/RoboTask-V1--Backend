package com.example.acwa.services;

import com.example.acwa.entities.Notification;
import com.example.acwa.entities.User;
import com.example.acwa.repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    public void createNotification(String message, User recipient) {
        Notification notif = new Notification();
        notif.setMessage(message);
        notif.setRecipient(recipient);
        notif.setRead(false);
        notif.setCreatedAt(java.time.LocalDateTime.now());
        notificationRepository.save(notif);
    }

    public List<Notification> getUserNotifications(String username) {
        return notificationRepository.findByRecipientUsernameOrderByCreatedAtDesc(username);
    }

    public long countUnread(String username) {
        return notificationRepository.countByRecipientUsernameAndReadFalse(username);
    }
    public void markAllAsRead(String username) {
        List<Notification> notifs = notificationRepository.findByRecipientUsername(username);
        for (Notification n : notifs) {
            n.setRead(true);
        }
        notificationRepository.saveAll(notifs);
    }
    public List<Notification> getUnreadNotifications(String username) {
        return notificationRepository.findByRecipientUsernameAndReadFalse(username);
    }





}

