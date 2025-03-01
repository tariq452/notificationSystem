package com.example.emailapp.repository;

import com.example.emailapp.model.PodTracking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PodTrackingRepository extends JpaRepository<PodTracking, String> {
}
