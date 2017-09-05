package com.swimHelper.component.training;

import com.swimHelper.TestUtil;
import com.swimHelper.TrainingTestUtil;
import com.swimHelper.exception.TooManyParametersException;
import com.swimHelper.exception.TrainingNotFoundException;
import com.swimHelper.exception.UserNotFoundException;
import com.swimHelper.model.Training;
import com.swimHelper.model.User;
import com.swimHelper.repository.TrainingRepository;
import com.swimHelper.repository.UserRepository;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by mstobieniecka on 2017-09-05.
 */
public class DistanceTrackerUnitTest {
    private final TrainingRepository trainingRepositoryMock = mock(TrainingRepository.class);
    private final UserRepository userRepositoryMock = mock(UserRepository.class);
    final DistanceTracker sut = new DistanceTracker(userRepositoryMock, trainingRepositoryMock);

    @Test
    public void countDistance_whenUserId_shouldReturnDistanceOfAllTrainings() throws UserNotFoundException, TooManyParametersException, TrainingNotFoundException {
        //given
        User user = new TestUtil().createValidUser();
        List<Training> trainings = createTrainingsForUser(user);
        when(userRepositoryMock.findOne(anyLong())).thenReturn(user);
        when(trainingRepositoryMock.findTrainingsByUser(anyLong())).thenReturn(trainings);
        //when
        int distance = sut.countDistance(1L, null, null, null);
        //then
        assertThat(distance).isEqualTo(25200);
    }

    @Test
    public void countDistance_whenUserIdAndTrainingId_shouldReturnDistanceOfChosenTraining() throws UserNotFoundException, TooManyParametersException, TrainingNotFoundException {
        //given
        User user = new TestUtil().createValidUser();
        Training training = new TrainingTestUtil().createValidTraining();
        training.getExerciseSeries().forEach(es -> es.setCompletedRepeats(4));
        when(userRepositoryMock.findOne(anyLong())).thenReturn(user);
        when(trainingRepositoryMock.findTrainingByUserAndId(anyLong(), anyLong())).thenReturn(training);
        when(trainingRepositoryMock.findOne(anyLong())).thenReturn(training);
        //when
        int distance = sut.countDistance(1L, 1L, null, null);
        //then
        assertThat(distance).isGreaterThan(8000).isLessThan(9000);
    }

    @Test
    public void countDistance_whenUserIdAndDates_shouldReturnDistanceOfTrainingsBetweenDates() throws UserNotFoundException, TooManyParametersException, TrainingNotFoundException {
        //given
        User user = new TestUtil().createValidUser();
        List<Training> trainings = createTrainingsForUser(user);
        trainings.forEach(t -> t.setTrainingDateTime(LocalDateTime.now()));
        LocalDateTime endDate = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        LocalDateTime startDate = LocalDateTime.of(2014, 9, 10, 6, 40, 45);
        when(userRepositoryMock.findOne(anyLong())).thenReturn(user);
        when(trainingRepositoryMock.findTrainingsByUserAndDates(anyLong(), eq(startDate), eq(endDate))).thenReturn(trainings);
        //when
        int distance = sut.countDistance(1L, null, startDate, endDate);
        //then
        assertThat(distance).isGreaterThan(25000);
    }

    @Test
    public void countDistance_whenUserIdAndDatesAndTrainingId_shouldThrowException() throws UserNotFoundException, TooManyParametersException, TrainingNotFoundException {
        //given
        User user = new TestUtil().createValidUser();
        LocalDateTime endDate = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        LocalDateTime startDate = LocalDateTime.of(2014, 9, 10, 6, 40, 45);
        when(userRepositoryMock.findOne(anyLong())).thenReturn(user);
        //when
        Throwable thrown = catchThrowable(() -> sut.countDistance(1L, 1L, startDate, endDate));
        //then
        assertThat(thrown).isInstanceOf(TooManyParametersException.class);
    }


    private List<Training> createTrainingsForUser(User user) {
        Training training1 = new TrainingTestUtil().createValidTraining();
        training1.setUser(user);
        training1.getExerciseSeries().forEach(es -> es.setCompletedRepeats(3));
        Training training2 = new TrainingTestUtil().createValidTraining();
        training2.setUser(user);
        training2.getExerciseSeries().forEach(es -> es.setCompletedRepeats(4));
        Training training3 = new TrainingTestUtil().createValidTraining();
        training3.setUser(user);
        training3.getExerciseSeries().forEach(es -> es.setCompletedRepeats(5));
        return Arrays.asList(training1, training2, training3);
    }
}
