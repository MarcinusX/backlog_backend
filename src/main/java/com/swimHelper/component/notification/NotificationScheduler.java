package com.swimHelper.component.notification;

import com.swimHelper.exception.UnsupportedNotificationMediumException;
import com.swimHelper.model.NotificationMedium;
import com.swimHelper.repository.TrainingRepository;
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
    private final NotificationSenderFactory notificationService;

    @Autowired
    public NotificationScheduler(TrainingRepository trainingRepository, NotificationSenderFactory notificationService) {
        this.trainingRepository = trainingRepository;
        this.notificationService = notificationService;
    }

    @Scheduled(fixedRateString = "${notifications.scheduler.rate}")
    public void sendNotifications() {
        trainingRepository.findTrainingsToBeNotified(LocalDateTime.now()).forEach(
                training -> {
                    try {
                        NotificationSender senderService = notificationService.getSenderService(NotificationMedium.EMAIL);
                        senderService.remindTraining(training.getUser(), training);
                    } catch (UnsupportedNotificationMediumException e) {
                        e.printStackTrace();
                    }
                }
        );
    }
}
