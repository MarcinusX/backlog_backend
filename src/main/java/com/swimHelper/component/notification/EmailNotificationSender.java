package com.swimHelper.component.notification;

import com.swimHelper.model.EmailMessage;
import com.swimHelper.model.Training;
import com.swimHelper.model.User;
import com.swimHelper.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Marcin Szalek on 22.08.17.
 */
@Component
public class EmailNotificationSender implements NotificationSender {

    private final NotificationEmailMessageCreator emailMessageCreator;
    private final EmailService emailService;

    @Autowired
    public EmailNotificationSender(NotificationEmailMessageCreator emailMessageCreator, EmailService emailService) {
        this.emailMessageCreator = emailMessageCreator;
        this.emailService = emailService;
    }

    @Override
    public void remindTraining(User user, Training training) {
        EmailMessage emailMessage = emailMessageCreator.createNotificationEmailMessage(user, training);
        emailService.sendEmail(emailMessage);
    }
}
