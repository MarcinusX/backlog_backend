package com.swimHelper.service.training;

import com.swimHelper.TrainingTestUtil;
import com.swimHelper.component.training.DistanceTracker;
import com.swimHelper.exception.InvalidTrainingException;
import com.swimHelper.exception.TrainingNotFoundException;
import com.swimHelper.generator.TrainingGenerator;
import com.swimHelper.model.ExerciseSeries;
import com.swimHelper.model.Training;
import com.swimHelper.repository.TrainingRepository;
import com.swimHelper.repository.UserRepository;
import com.swimHelper.service.TrainingService;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Created by mstobieniecka on 2017-09-02.
 */
public class TrainingServiceUnitTest {
    private final TrainingRepository trainingRepositoryMock = mock(TrainingRepository.class);
    private final TrainingGenerator trainingGeneratorMock = mock(TrainingGenerator.class);
    private final UserRepository userRepositoryMock = mock(UserRepository.class);
    private final DistanceTracker distanceTrackerMock = mock(DistanceTracker.class);
    final TrainingService sut = new TrainingService(trainingGeneratorMock, userRepositoryMock, trainingRepositoryMock, distanceTrackerMock);

    @Test
    public void putTraining_ifTrainingNotExists_throwsException() throws Exception {
        //given
        when(trainingRepositoryMock.getOne(1L)).thenReturn(null);
        Training training = new TrainingTestUtil().createValidTraining();
        //when
        Throwable thrown = catchThrowable(() -> sut.setTrainingCompletion(training));
        //then
        assertThat(thrown).isInstanceOf(TrainingNotFoundException.class);
    }

    @Test
    public void putTraining_ifTrainingIsNull_throwsException() throws Exception {
        //given

        //when
        Throwable thrown = catchThrowable(() -> sut.setTrainingCompletion(null));
        //then
        assertThat(thrown).isInstanceOf(InvalidTrainingException.class);
    }

    @Test
    public void putTraining_callsRepositoryToSave() throws Exception {
        //given
        Training training = new TrainingTestUtil().createValidTraining();
        when(trainingRepositoryMock.findOne(1L)).thenReturn(new Training());
        //when
        sut.setTrainingCompletion(training);
        //then
        verify(trainingRepositoryMock).saveAndFlush(any());
    }

    @Test
    public void putTraining_returnsUpdatedEntity() throws Exception {
        //given
        Training training = new TrainingTestUtil().createValidTraining();
        when(trainingRepositoryMock.findOne(anyLong())).thenReturn(training);
        when(trainingRepositoryMock.saveAndFlush(training)).thenReturn(training);
        ExerciseSeries exerciseSeriesToUpdate = training.getExerciseSeries().stream().filter(es -> es.getId().equals(1L)).findFirst().get();
        exerciseSeriesToUpdate.setCompletedRepeats(5);
        exerciseSeriesToUpdate.setAverageDurationOfOneRepeatInSeconds(300);
        //when
        Training returnedTraining = sut.setTrainingCompletion(training);
        ExerciseSeries updatedExerciseSeries = returnedTraining.getExerciseSeries().stream().filter(es -> es.getId().equals(1L)).findFirst().get();
        //then
        assertThat(updatedExerciseSeries.getAverageDurationOfOneRepeatInSeconds()).isEqualTo(exerciseSeriesToUpdate.getAverageDurationOfOneRepeatInSeconds());
        assertThat(updatedExerciseSeries.getCompletedRepeats()).isEqualTo(exerciseSeriesToUpdate.getCompletedRepeats());
    }
}
