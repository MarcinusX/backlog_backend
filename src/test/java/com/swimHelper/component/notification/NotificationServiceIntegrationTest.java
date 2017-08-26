package com.swimHelper.component.notification;

import com.swimHelper.model.Training;
import com.swimHelper.model.User;
import com.swimHelper.repository.TrainingRepository;
import com.swimHelper.service.NotificationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.mail.internet.MimeMessage;
import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by Marcin Szalek on 22.08.17.
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ComponentScan
public class NotificationServiceIntegrationTest {

    @Autowired
    private NotificationService sut;

    @MockBean
    private TrainingRepository trainingRepository;
    @MockBean
    private JavaMailSender javaMailSender;

    @Test
    public void notificationScheduler_callsMailSender() {
        //given
        Training training = new Training();
        User user = new User();
        user.setEmail("some@email.com");
        user.setFirstname("Zbyszek");
        training.setUser(user);
        when(trainingRepository.findTrainingsToBeNotified(any())).thenReturn(Collections.singletonList(training));
        when(javaMailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));
        //when
        sut.sendNotifications();
        //then
        verify(javaMailSender).send(any(MimeMessage.class));
    }
}