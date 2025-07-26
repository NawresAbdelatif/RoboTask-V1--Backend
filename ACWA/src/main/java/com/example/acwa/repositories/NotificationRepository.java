package com.example.acwa.repositories;

import com.example.acwa.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientEmailAndReadFalse(String email);
    long countByRecipientEmailAndReadFalse(String email);
    List<Notification> findByRecipientEmail(String email);
    List<Notification> findByRecipientEmailOrderByCreatedAtDesc(String email);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Notification n SET n.read = true WHERE n.recipient.email = :email")
    int markAllAsReadByRecipientEmail(@Param("email") String email);

}


