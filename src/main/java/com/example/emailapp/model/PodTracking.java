package com.example.emailapp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pod_tracking")
public class PodTracking {
    @Id
    private String ulid; // ULID as primary key

    private String podName;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime terminatedAt;
    private String status = "RUNNING";

    @PrePersist
    public void generateUlid() {
        this.ulid = ULIDGenerator.generateULID();
    }
}
