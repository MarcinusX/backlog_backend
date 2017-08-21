package com.swimHelper.scheduler;

import com.swimHelper.repository.TrainingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by Marcin Szalek on 21.08.17.
 */
@Component
public class NotificationScheduler {

    private final TrainingRepository trainingRepository;

    @Autowired
    public NotificationScheduler(TrainingRepository trainingRepository) {
        this.trainingRepository = trainingRepository;
    }

    @Scheduled(fixedRateString = "${scheduler.notifications.rate}")
    public void sendNotifications() {

    }
}
