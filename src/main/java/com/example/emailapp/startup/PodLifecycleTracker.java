package com.example.emailapp.startup;

import com.example.emailapp.model.PodTracking;
import com.example.emailapp.repository.PodTrackingRepository;
import com.example.emailapp.service.ULIDGenerator;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

@Component
public class PodLifecycleTracker {

    private final PodTrackingRepository podTrackingRepository;
    private final String podUlid;
    private final String podName;

    public PodLifecycleTracker(PodTrackingRepository podTrackingRepository) {
        this.podTrackingRepository = podTrackingRepository;
        this.podUlid = ULIDGenerator.generateULID();
        this.podName = System.getenv("POD_NAME") != null ? System.getenv("POD_NAME") : "local-pod";
    }

    @PostConstruct
    public void registerPod() {
        PodTracking podTracking = new PodTracking();
        podTracking.setUlid(podUlid);
        podTracking.setPodName(podName);
        podTrackingRepository.save(podTracking);
    }

    @PreDestroy
    public void unregisterPod() {
        podTrackingRepository.deleteById(podUlid);
    }
}
