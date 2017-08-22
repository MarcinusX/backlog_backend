package com.swimHelper.component.notification;

import com.swimHelper.model.Training;
import com.swimHelper.model.User;
import com.swimHelper.repository.TrainingRepository;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by Marcin Szalek on 22.08.17.
 */
public class NotificationSchedulerTest {

    private final TrainingRepository trainingRepositoryMock = mock(TrainingRepository.class);
    private final NotificationSenderFactory factoryMock = mock(NotificationSenderFactory.class);
    private final NotificationScheduler sut = new NotificationScheduler(trainingRepositoryMock, factoryMock);

    @Test
    public void sendNotifications_callsSenderForEveryTraining() throws Exception {
        //given
        NotificationSender someSenderService = mock(NotificationSender.class);
        List<Training> trainingsNeedingNotification = Arrays.asList(training(1), training(2), training(3));
        when(trainingRepositoryMock.findTrainingsToBeNotified(any())).thenReturn(trainingsNeedingNotification);
        when(factoryMock.getSenderService(any())).thenReturn(someSenderService);
        //when
        sut.sendNotifications();
        //then
        for (Training training : trainingsNeedingNotification) {
            verify(someSenderService).remindTraining(training.getUser(), training);
        }
    }

    private Training training(long id) {
        Training training = new Training();
        training.setId(id);
        training.setUser(new User());
        return training;
    }

}