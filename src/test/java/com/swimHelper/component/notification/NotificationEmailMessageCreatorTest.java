package com.swimHelper.component.notification;

import com.swimHelper.model.EmailMessage;
import com.swimHelper.model.Training;
import com.swimHelper.model.User;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Marcin Szalek on 22.08.17.
 */
public class NotificationEmailMessageCreatorTest {

    private static final String SUBJECT = "subject";
    private static final String TEXT_TEMPLATE = "template %s";
    private static final String AUTHOR_NAME = "author";
    private static final String AUTHOR_ADDRESS = "email@author.com";

    private final NotificationEmailMessageCreator sut =
            new NotificationEmailMessageCreator(SUBJECT, TEXT_TEMPLATE, AUTHOR_NAME, AUTHOR_ADDRESS);

    @Test
    public void createNotificationEmailMessage() throws Exception {
        //given
        User user = new User();
        Training training = new Training();
        //when
        EmailMessage emailMessage = sut.createNotificationEmailMessage(user, training);
        //then
        assertThat(emailMessage.getSubject()).isEqualTo(SUBJECT);
        assertThat(emailMessage.getTo().getName()).isEqualTo(user.getFirstname() + " " + user.getLastname());
        assertThat(emailMessage.getTo().getEmailAddress()).isEqualTo(user.getEmail());
        assertThat(emailMessage.getFrom().getName()).isEqualTo(AUTHOR_NAME);
        assertThat(emailMessage.getFrom().getEmailAddress()).isEqualTo(AUTHOR_ADDRESS);
        //TODO: test injecting message?
    }

}