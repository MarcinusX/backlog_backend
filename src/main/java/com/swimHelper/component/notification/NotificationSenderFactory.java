package com.swimHelper.component.notification;

import com.swimHelper.exception.UnsupportedNotificationMediumException;
import com.swimHelper.model.NotificationMedium;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marcin Szalek on 22.08.17.
 */
@Component
public class NotificationSenderFactory {

    private final Map<NotificationMedium, NotificationSender> notificationSenders;

    @Autowired
    public NotificationSenderFactory(EmailNotificationSender emailNotificationSender) {
        notificationSenders = new HashMap<>();
        notificationSenders.put(NotificationMedium.EMAIL, emailNotificationSender);
    }

    public NotificationSender getSenderService(NotificationMedium medium)
            throws UnsupportedNotificationMediumException {
        NotificationSender senderService = notificationSenders.get(medium);
        if (senderService == null) {
            throw new UnsupportedNotificationMediumException(medium);
        } else {
            return senderService;
        }
    }
}
