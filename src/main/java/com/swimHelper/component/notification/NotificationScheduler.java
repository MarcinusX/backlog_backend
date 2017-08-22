package com.swimHelper.component.notification;

import com.swimHelper.exception.UnsupportedNotificationMediumException;
import com.swimHelper.model.NotificationMedium;
import com.swimHelper.model.Training;
import com.swimHelper.repository.TrainingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Marcin Szalek on 21.08.17.
 */
@Component
public class NotificationScheduler {

    private final TrainingRepository trainingRepository;
    private final NotificationSenderFactory notificationSenderFactory;

    @Autowired
    public NotificationScheduler(TrainingRepository trainingRepository, NotificationSenderFactory notificationSenderFactory) {
        this.trainingRepository = trainingRepository;
        this.notificationSenderFactory = notificationSenderFactory;
    }

    @Scheduled(fixedRateString = "${notifications.scheduler.rate}")
    public void sendNotifications() {
        List<Training> trainigs = trainingRepository.findTrainingsToBeNotified(LocalDateTime.now());
        trainigs.forEach(
                training -> {
                    try {
                        NotificationSender senderService = notificationSenderFactory.getSenderService(NotificationMedium.EMAIL);
                        boolean notificationSentProperly = senderService.remindTraining(training.getUser(), training);
                        if (notificationSentProperly) {
                            training.setHasUserBeenNotified(true);
                            trainingRepository.saveAndFlush(training);
                        }
                    } catch (UnsupportedNotificationMediumException e) {
                        e.printStackTrace();
                    }
                }
        );
    }
}
