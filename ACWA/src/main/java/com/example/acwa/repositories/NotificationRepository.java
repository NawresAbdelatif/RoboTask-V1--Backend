package com.example.acwa.repositories;

import com.example.acwa.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientEmailAndReadFalse(String email);
    long countByRecipientEmailAndReadFalse(String email);
    List<Notification> findByRecipientEmail(String email);
    List<Notification> findByRecipientEmailOrderByCreatedAtDesc(String email);
}


