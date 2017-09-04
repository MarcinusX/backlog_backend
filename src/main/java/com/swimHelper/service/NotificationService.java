package com.swimHelper.service;

import com.swimHelper.component.notification.NotificationSender;
import com.swimHelper.component.notification.NotificationSenderFactory;
import com.swimHelper.exception.UnsupportedNotificationMediumException;
import com.swimHelper.model.NotificationMedium;
import com.swimHelper.model.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by Marcin Szalek on 21.08.17.
 */
@Component
public class NotificationService {

    private final TrainingService trainingService;
    private final NotificationSenderFactory notificationSenderFactory;

    @Autowired
    public NotificationService(TrainingService trainingService, NotificationSenderFactory notificationSenderFactory) {
        this.trainingService = trainingService;
        this.notificationSenderFactory = notificationSenderFactory;
    }

    @Scheduled(fixedRateString = "${notifications.scheduler.rate}")
    public void sendNotifications() {
        trainingService.getTrainingsToBeNotified().forEach(this::sendNotification);
    }

    public void sendNotification(Training training) {
        try {
            NotificationSender senderService = notificationSenderFactory.getSenderService(NotificationMedium.EMAIL);
            boolean notificationSentProperly = senderService.remindTraining(training.getUser(), training);
            if (notificationSentProperly) {
                setTrainingNotified(training);
            }
        } catch (UnsupportedNotificationMediumException e) {
            e.printStackTrace();
        }
    }

    private void setTrainingNotified(Training training) {
        training.setHasUserBeenNotified(true);
        trainingService.updateTraining(training);
    }
}
