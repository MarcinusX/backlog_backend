package com.swimHelper.repository;

import com.swimHelper.model.Training;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Marcin Szalek on 21.08.17.
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class TrainingRepositoryTest {

    @Autowired
    private TrainingRepository trainingRepository;

    @Before
    public void clearDatabase() {
        trainingRepository.deleteAll();
    }

    @Test
    public void trainingRepositoryQueryTest() throws Exception {
        //given
        Training training = new Training();
        Training training1 = new Training();
        training1.setNotificationDateTime(LocalDateTime.now().minusMinutes(1));
        Training training2 = new Training();
        training2.setNotificationDateTime(LocalDateTime.now().plusMinutes(1));
        Training training3 = new Training();
        training3.setHasUserBeenNotified(true);
        Training training4 = new Training();
        training4.setHasUserBeenNotified(true);
        training4.setNotificationDateTime(LocalDateTime.now().minusMinutes(1));
        Training training5 = new Training();
        training5.setHasUserBeenNotified(true);
        training5.setNotificationDateTime(LocalDateTime.now().plusMinutes(1));
        trainingRepository.save(Arrays.asList(training, training1, training2, training3, training4, training5));
        trainingRepository.flush();
        //when
        List<Training> trainings = trainingRepository.findTrainingsToBeNotified(LocalDateTime.now());
        //then
        assertThat(trainings).containsExactly(training1);
    }

}