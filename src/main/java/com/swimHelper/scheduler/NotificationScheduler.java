package com.swimHelper.scheduler;

import com.swimHelper.repository.TrainingRepository;
import com.swimHelper.service.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Created by Marcin Szalek on 21.08.17.
 */
@Component
public class NotificationScheduler {

    private final TrainingRepository trainingRepository;
    private final NotificationService notificationService;

    @Autowired
    public NotificationScheduler(TrainingRepository trainingRepository, NotificationService notificationService) {
        this.trainingRepository = trainingRepository;
        this.notificationService = notificationService;
    }

    @Scheduled(fixedRateString = "${scheduler.notifications.rate}")
    public void sendNotifications() {
        trainingRepository.findTrainingsToBeNotified(LocalDateTime.now()).forEach(
                training -> {

                }
        );
    }
}
