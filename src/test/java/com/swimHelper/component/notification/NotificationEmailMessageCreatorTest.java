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
    private static final String TEXT_TEMPLATE = "template {0}";
    private static final String AUTHOR_NAME = "author";
    private static final String AUTHOR_ADDRESS = "email@author.com";

    private final NotificationEmailMessageCreator sut =
            new NotificationEmailMessageCreator(SUBJECT, TEXT_TEMPLATE, AUTHOR_NAME, AUTHOR_ADDRESS);

    @Test
    public void createNotificationEmailMessage() throws Exception {
        //given
        User user = new User();
        user.setFirstname("Bill");
        user.setLastname("Gates");
        Training training = new Training();
        //when
        EmailMessage emailMessage = sut.createNotificationEmailMessage(user, training);
        //then
        assertThat(emailMessage.getSubject()).isEqualTo(SUBJECT);
        assertThat(emailMessage.getTo().getName()).isEqualTo("Bill Gates");
        assertThat(emailMessage.getTo().getEmailAddress()).isEqualTo(user.getEmail());
        assertThat(emailMessage.getFrom().getName()).isEqualTo(AUTHOR_NAME);
        assertThat(emailMessage.getFrom().getEmailAddress()).isEqualTo(AUTHOR_ADDRESS);
    }

}