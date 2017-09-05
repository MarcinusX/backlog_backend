package com.swimHelper.service.calories;

import com.swimHelper.ExerciseSeriesRepository;
import com.swimHelper.TestUtil;
import com.swimHelper.TrainingTestUtil;
import com.swimHelper.exception.TooManyParametersException;
import com.swimHelper.exception.TrainingNotFoundException;
import com.swimHelper.exception.UserNotFoundException;
import com.swimHelper.model.Style;
import com.swimHelper.model.Training;
import com.swimHelper.model.User;
import com.swimHelper.repository.ExerciseRepository;
import com.swimHelper.repository.TrainingRepository;
import com.swimHelper.repository.UserRepository;
import com.swimHelper.service.CaloriesService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

/**
 * Created by mstobieniecka on 2017-09-05.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class CaloriesServiceIntegrationTest {

    @Autowired
    private CaloriesService sut;
    @Autowired
    private TrainingRepository trainingRepository;
    @Autowired
    private ExerciseSeriesRepository exerciseSeriesRepository;
    @Autowired
    private ExerciseRepository exerciseRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestUtil testUtil;
    @Autowired
    private TrainingTestUtil trainingTestUtil;

    @After
    public void cleanUp() {
        trainingRepository.deleteAll();
        exerciseSeriesRepository.deleteAll();
        userRepository.deleteAll();
        exerciseRepository.deleteAll();
    }

    @Before
    public void prepare() {
        trainingRepository.deleteAll();
        exerciseSeriesRepository.deleteAll();
        userRepository.deleteAll();
        exerciseRepository.deleteAll();
        trainingTestUtil.addExercisesInSpecifiedStyle(Style.BACKSTROKE);
        trainingTestUtil.addExercisesInSpecifiedStyle(Style.FREESTYLE);
        trainingTestUtil.addExercisesInSpecifiedStyle(Style.BREASTSTROKE);
        trainingTestUtil.addExercisesInSpecifiedStyle(Style.BUTTERFLY);
        trainingTestUtil.addWarmUpRelaxExercises();
    }

    @Test
    public void calculateCalories_whenUser_shouldReturnBurnedCaloriesOfAllTrainingsForUser() throws UserNotFoundException, TooManyParametersException, TrainingNotFoundException {
        //given
        User user = testUtil.createValidUser();
        user.setWeight(80);
        User savedUser = userRepository.saveAndFlush(user);
        addTrainings(savedUser);
        //when
        int distance = sut.calculateCalories(savedUser.getId(), null, null, null);
        //given
        assertThat(distance).isGreaterThan(0);
    }

    @Test
    public void calculateCalories_whenUserAndDates_shouldReturnBurnedCaloriesOfAllTrainingsBetweenDatesForUser() throws UserNotFoundException, TooManyParametersException, TrainingNotFoundException {
        //given
        User user = testUtil.createValidUser();
        user.setWeight(80);
        User savedUser = userRepository.saveAndFlush(user);
        addTrainings(savedUser);
        LocalDateTime startDate = LocalDateTime.of(2017, 1, 30, 6, 40, 45);
        LocalDateTime endDate = LocalDateTime.of(2100, 11, 30, 6, 40, 45);
        //when
        int distance = sut.calculateCalories(savedUser.getId(), null, startDate, endDate);
        //given
        assertThat(distance).isGreaterThan(0);
    }

    @Test
    public void calculateCalories_whenUserAndDates_shouldReturn0() throws UserNotFoundException, TooManyParametersException, TrainingNotFoundException {
        //given
        User user = testUtil.createValidUser();
        user.setWeight(80);
        User savedUser = userRepository.saveAndFlush(user);
        addTrainings(savedUser);
        LocalDateTime startDate = LocalDateTime.of(2100, 1, 30, 6, 40, 45);
        LocalDateTime endDate = LocalDateTime.of(2100, 11, 30, 6, 40, 45);
        //when
        int distance = sut.calculateCalories(savedUser.getId(), null, startDate, endDate);
        //given
        assertThat(distance).isEqualTo(0);
    }

    @Test
    public void calculateCalories_whenUserAndDatesAndTrainingId_shouldThrowException() throws UserNotFoundException, TooManyParametersException, TrainingNotFoundException {
        //given
        User user = testUtil.createValidUser();
        user.setWeight(80);
        User savedUser = userRepository.saveAndFlush(user);
        addTrainings(savedUser);
        LocalDateTime startDate = LocalDateTime.of(2017, 1, 30, 6, 40, 45);
        LocalDateTime endDate = LocalDateTime.of(2100, 11, 30, 6, 40, 45);
        //when
        Throwable thrown = catchThrowable(() -> sut.calculateCalories(savedUser.getId(), 1L, startDate, endDate));
        //given
        assertThat(thrown).isInstanceOf(TooManyParametersException.class);
    }

    private void addTrainings(User user) {
        for (int i = 1; i < 6; i++) {
            Training training = testUtil.createValidTraining();
            training.getExerciseSeries().forEach(es -> es.setExercise(exerciseRepository.findByStyle(Style.BACKSTROKE).get(0)));
            training.setTrainingDateTime(LocalDateTime.of(2017, i, 10, 6, 40, 45));
            training.getExerciseSeries().forEach(es -> {
                es.setCompletedRepeats(3);
                es.setAverageDurationOfOneRepeatInSeconds(200);
            });
            training.setUser(user);
            trainingRepository.saveAndFlush(training);
        }
    }
}
