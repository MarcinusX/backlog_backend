package com.swimHelper.component.notification;

import com.swimHelper.exception.UnsupportedNotificationMediumException;
import com.swimHelper.model.NotificationMedium;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mock;

/**
 * Created by Marcin Szalek on 22.08.17.
 */
public class NotificationFactoryTest {

    private final EmailNotificationSender emailSenderServiceMock = mock(EmailNotificationSender.class);
    private final NotificationSenderFactory sut = new NotificationSenderFactory(emailSenderServiceMock);

    @Test
    public void getSenderService_whenUnsupportedMedium_throwsException() throws Exception {
        //given
        NotificationMedium medium = NotificationMedium.SMS;//not supported yet
        //when
        Throwable thrown = catchThrowable(() -> sut.getSenderService(medium));
        //then
        assertThat(thrown).isInstanceOf(UnsupportedNotificationMediumException.class);
    }

    @Test
    public void getSenderService_whenNullMedium_throwsException() throws Exception {
        //given
        NotificationMedium medium = null;
        //when
        Throwable thrown = catchThrowable(() -> sut.getSenderService(medium));
        //then
        assertThat(thrown).isInstanceOf(UnsupportedNotificationMediumException.class);
    }

    @Test
    public void getSenderService_whenEmail_returnsEmailService() throws Exception {
        //given
        NotificationMedium medium = NotificationMedium.EMAIL;
        //when
        NotificationSender senderService = sut.getSenderService(medium);
        //then
        assertThat(senderService).isEqualTo(emailSenderServiceMock);
    }
}