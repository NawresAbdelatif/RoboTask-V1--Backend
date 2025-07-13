package com.example.acwa.repositories;

import com.example.acwa.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

        List<Notification> findByRecipientUsernameAndReadFalse(String username);
        long countByRecipientUsernameAndReadFalse(String username);
        List<Notification> findByRecipientUsername(String username);
        List<Notification> findByRecipientUsernameOrderByCreatedAtDesc(String username);
    }


