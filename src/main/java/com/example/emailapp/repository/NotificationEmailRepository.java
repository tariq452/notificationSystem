package com.example.emailapp.repository;

import com.example.emailapp.model.NotificationEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationEmailRepository extends JpaRepository<NotificationEmail, String> {
    List<NotificationEmail> findByStatus(NotificationEmail.Status status);
}
