package com.swimHelper.service;

import com.swimHelper.component.notification.NotificationSender;
import com.swimHelper.component.notification.NotificationSenderFactory;
import com.swimHelper.exception.BusinessException;
import com.swimHelper.exception.UnsupportedNotificationMediumException;
import com.swimHelper.model.NotificationMedium;
import com.swimHelper.model.Training;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    public NotificationService(TrainingService trainingService, NotificationSenderFactory notificationSenderFactory) {
        this.trainingService = trainingService;
        this.notificationSenderFactory = notificationSenderFactory;
    }

    @Scheduled(fixedRateString = "${notifications.scheduler.rate}")
    public void sendNotifications() {
        trainingService.getTrainingsToBeNotified().forEach((training) -> {
            try {
                sendNotification(training);
            } catch (BusinessException e) {
                logger.error("Failed to find training by id: " + training.getId(), e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void sendNotification(Training training) throws BusinessException {
        try {
            NotificationSender senderService = notificationSenderFactory.getSenderService(NotificationMedium.EMAIL);
            boolean notificationSentProperly = senderService.remindTraining(training.getUser(), training);
            if (notificationSentProperly) {
                trainingService.setUserNotified(training.getId());
            }
        } catch (UnsupportedNotificationMediumException e) {
            e.printStackTrace();
        }
    }
}
