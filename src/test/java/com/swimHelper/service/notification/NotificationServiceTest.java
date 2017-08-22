package com.swimHelper.service.notification;

import com.swimHelper.exception.UnsupportedNotificationMediumException;
import com.swimHelper.model.NotificationMedium;
import com.swimHelper.model.Training;
import com.swimHelper.model.User;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mock;

/**
 * Created by Marcin Szalek on 22.08.17.
 */
public class NotificationServiceTest {

    private final EmailSenderService emailSenderServiceMock = mock(EmailSenderService.class);
    private final NotificationService sut = new NotificationService(emailSenderServiceMock);

    @Test
    public void sendTrainingReminderNotification_whenUnsupportedMedium_throwsException() throws Exception {
        //given
        Training training = new Training();
        User user = new User();
        NotificationMedium medium = NotificationMedium.SMS;//not supported yet
        //when
        Throwable thrown = catchThrowable(() -> sut.sendTrainingReminderNotification(medium, user, training));
        //then
        assertThat(thrown).isInstanceOf(UnsupportedNotificationMediumException.class);
    }

    @Test
    public void sendTrainingReminderNotification_whenNullMedium_throwsException() throws Exception {
        //given
        Training training = new Training();
        User user = new User();
        //when
        Throwable thrown = catchThrowable(() -> sut.sendTrainingReminderNotification(null, user, training));
        //then
        assertThat(thrown).isInstanceOf(UnsupportedNotificationMediumException.class);
    }
}