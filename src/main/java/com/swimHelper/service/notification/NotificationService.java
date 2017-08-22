package com.swimHelper.service.notification;

import com.swimHelper.exception.UnsupportedNotificationMediumException;
import com.swimHelper.model.NotificationMedium;
import com.swimHelper.model.Training;
import com.swimHelper.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marcin Szalek on 22.08.17.
 */
@Service
public class NotificationService {

    private final Map<NotificationMedium, NotificationSenderService> senderServices;

    @Autowired
    public NotificationService(EmailSenderService emailSenderService) {
        senderServices = new HashMap<>();
        senderServices.put(NotificationMedium.EMAIL, emailSenderService);
    }

    public void sendTrainingReminderNotification(NotificationMedium medium, User user, Training training)
            throws UnsupportedNotificationMediumException {
        NotificationSenderService senderService = senderServices.get(medium);
        if (senderService == null) {
            throw new UnsupportedNotificationMediumException(medium);
        }
    }
}
