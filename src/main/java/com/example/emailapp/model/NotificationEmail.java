package com.example.emailapp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification_email")
public class NotificationEmail {
    @Id
    private String ulid; // ULID as primary key

    private String email;
    private String subject;

    @Lob
    private String message;

    @Lob
    private byte[] attachment;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    private int retryCount = 0;

    @ManyToOne
    @JoinColumn(name = "pod_ulid")
    private PodTracking processingPod;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    public void generateUlid() {
        this.ulid = ULIDGenerator.generateULID();
    }

    public enum Status {
        PENDING, PROCESSING, SENT, FAILED
    }
}
