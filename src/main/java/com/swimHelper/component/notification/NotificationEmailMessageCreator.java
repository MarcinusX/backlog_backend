package com.swimHelper.component.notification;

import com.swimHelper.model.EmailMessage;
import com.swimHelper.model.Training;
import com.swimHelper.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

/**
 * Created by Marcin Szalek on 22.08.17.
 */
@Component
public class NotificationEmailMessageCreator {

    private final String subject;
    private final String textTemplate;
    private final String authorName;
    private final String authorAddress;

    public NotificationEmailMessageCreator(@Value("${notifications.email.subject}") String subject,
                                           @Value("${notifications.email.textTemplate}") String textTemplate,
                                           @Value("${notifications.email.author.name}") String authorName,
                                           @Value("${notifications.email.author.address}") String authorAddress) {
        this.subject = subject;
        this.textTemplate = textTemplate;
        this.authorName = authorName;
        this.authorAddress = authorAddress;
    }

    public EmailMessage createNotificationEmailMessage(User user, Training training) {
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setSubject(subject);
        emailMessage.setTextContent(
                MessageFormat.format(textTemplate, user.getFirstname(), training.getDateTime()));
        emailMessage.setTo(
                new EmailMessage.Person(user.getFirstname() + " " + user.getLastname(), user.getEmail()));
        emailMessage.setFrom(
                new EmailMessage.Person(authorName, authorAddress));
        return emailMessage;
    }
}
