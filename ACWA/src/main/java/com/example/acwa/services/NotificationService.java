package com.example.acwa.services;

import com.example.acwa.entities.Notification;
import com.example.acwa.entities.User;
import com.example.acwa.repositories.NotificationRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NotificationService {
    @PersistenceContext
    private EntityManager em;

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

    public List<Notification> getUserNotifications(String email) {
        return notificationRepository.findByRecipientEmailOrderByCreatedAtDesc(email);
    }
    public long countUnread(String email) {
        return notificationRepository.countByRecipientEmailAndReadFalse(email);
    }
    @Transactional
    public void markAllAsRead(String email) {
        int updated = notificationRepository.markAllAsReadByRecipientEmail(email);
        em.flush();
        System.out.println("Notifications updated: " + updated);
    }

    public List<Notification> getUnreadNotifications(String email) {
        return notificationRepository.findByRecipientEmailAndReadFalse(email);
    }

    public void deleteAllForUser(String email) {
        List<Notification> notifs = notificationRepository.findByRecipientEmail(email);
        notificationRepository.deleteAll(notifs);
    }






}

