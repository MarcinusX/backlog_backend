package com.swimHelper.generator;

import com.swimHelper.TestUtil;
import com.swimHelper.exception.MissingTrainingRequirementsException;
import com.swimHelper.model.*;
import com.swimHelper.repository.ExerciseRepository;
import com.swimHelper.util.RandomGenerator;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Created by mstobieniecka on 2017-07-19.
 */
public class TrainingGeneratorTest {

    private TestUtil testUtil = new TestUtil();
    private final ExerciseRepository exerciseRepository = mock(ExerciseRepository.class);
    private final RandomGenerator random = mock(RandomGenerator.class);
    private final TrainingCalculator trainingCalculator = spy(new TrainingCalculator(random));
    private final TrainingGenerator sut = new TrainingGenerator(exerciseRepository, trainingCalculator, random);

    @Test
    public void generateTraining_whenValidParameters_doesntThrowException() throws Exception {
        //given
        User user = testUtil.createValidUser();
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        when(exerciseRepository.findByStyle(Style.FREESTYLE)).thenReturn(Collections.singletonList(new Exercise(Style.FREESTYLE)));
        when(exerciseRepository.findByStyle(Style.BACKSTROKE)).thenReturn(Collections.singletonList(new Exercise(Style.BACKSTROKE)));
        when(random.generateRandomInt(2)).thenReturn(1);
        when(random.generateRandomInt(1)).thenReturn(0);
        doReturn(1).when(trainingCalculator).getNumberOfRepeatsInOneSeries(anyInt(), anyInt());
        Exercise exercise = new Exercise(Style.BACKSTROKE);
        exercise.setWarmUpRelax(true);
        when(exerciseRepository.findByWarmUpRelax(true)).thenReturn(Collections.singletonList(exercise));
        //when
        sut.generateTraining(user, trainingRequirements);
        //then
        //pass
    }

    //when user chooses training style he has to have style statistics
    @Test
    public void generateTraining_whenMissingUserData_shouldThrowException() {
        //given
        User user = testUtil.createValidUser();
        user.setStyleStatistics(new ArrayList<>());
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        //when
        Throwable throwable = catchThrowable(() -> sut.generateTraining(user, trainingRequirements));
        //then
        assertThat(throwable).isInstanceOf(MissingTrainingRequirementsException.class);
    }

    //when user wants to generate training he has to choose at least one style
    @Test
    public void generateTraining_whenMissingStyles_shouldThrowException() {
        //given
        User user = testUtil.createValidUser();
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        trainingRequirements.setStyles(new ArrayList<>());
        //when
        Throwable throwable = catchThrowable(() -> sut.generateTraining(user, trainingRequirements));
        //then
        assertThat(throwable).isInstanceOf(MissingTrainingRequirementsException.class);
    }

    //when user wants to generate training he has to choose intensity level
    @Test
    public void generateTraining_whenMissingIntensityLevel_shouldThrowException() {
        //given
        User user = testUtil.createValidUser();
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        trainingRequirements.setIntensityLevel(null);
        //when
        Throwable throwable = catchThrowable(() -> sut.generateTraining(user, trainingRequirements));
        //then
        assertThat(throwable).isInstanceOf(MissingTrainingRequirementsException.class);
    }

    //when user wants to generate training he has to write max duration of the training or max distance
    @Test
    public void generateTraining_whenMissingMaxDurationAndMaxDistance_shouldThrowException() {
        //given
        User user = testUtil.createValidUser();
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        trainingRequirements.setMaxDistance(0);
        trainingRequirements.setMaxDurationInSeconds(0);
        //when
        Throwable throwable = catchThrowable(() -> sut.generateTraining(user, trainingRequirements));
        //then
        assertThat(throwable).isInstanceOf(MissingTrainingRequirementsException.class);
    }

    //when user wants to generate training he has to write max duration of the training or max distance
    @Test
    public void generateTraining_whenMissingMaxDistance_shouldNotThrowException() throws Exception {
        //given
        User user = testUtil.createValidUser();
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        trainingRequirements.setMaxDistance(0);
        when(exerciseRepository.findByStyle(Style.FREESTYLE)).thenReturn(Collections.singletonList(new Exercise(Style.FREESTYLE)));
        when(exerciseRepository.findByStyle(Style.BACKSTROKE)).thenReturn(Collections.singletonList(new Exercise(Style.BACKSTROKE)));
        doReturn(1).when(trainingCalculator).getNumberOfRepeatsInOneSeries(anyInt(), anyInt());
        when(random.generateRandomInt(2)).thenReturn(1);
        when(random.generateRandomInt(1)).thenReturn(0);
        Exercise exercise = new Exercise(Style.BACKSTROKE);
        exercise.setWarmUpRelax(true);
        when(exerciseRepository.findByWarmUpRelax(true)).thenReturn(Collections.singletonList(exercise));
        //when
        sut.generateTraining(user, trainingRequirements);
        //then
        //pass
    }

    //when user wants to generate training he has to write max duration of the training or max distance
    @Test
    public void generateTraining_whenMissingMaxDuration_shouldNotThrowException() throws Exception {
        //given
        User user = testUtil.createValidUser();
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        trainingRequirements.setMaxDurationInSeconds(900);
        when(exerciseRepository.findByStyle(Style.FREESTYLE)).thenReturn(Collections.singletonList(new Exercise(Style.FREESTYLE)));
        when(exerciseRepository.findByStyle(Style.BACKSTROKE)).thenReturn(Collections.singletonList(new Exercise(Style.BACKSTROKE)));
        Exercise exercise = new Exercise(Style.BACKSTROKE);
        exercise.setWarmUpRelax(true);
        when(exerciseRepository.findByWarmUpRelax(true)).thenReturn(Collections.singletonList(exercise));
        doReturn(1).when(trainingCalculator).getNumberOfRepeatsInOneSeries(anyInt(), anyInt());
        //when
        sut.generateTraining(user, trainingRequirements);
        //then
        //pass
    }

    //check if training is not null
    @Test
    public void generateTraining_whenValidParameters_doesntReturnNull() throws Exception {
        //given
        User user = testUtil.createValidUser();
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        when(exerciseRepository.findByStyle(Style.FREESTYLE)).thenReturn(Collections.singletonList(new Exercise(Style.FREESTYLE)));
        when(exerciseRepository.findByStyle(Style.BACKSTROKE)).thenReturn(Collections.singletonList(new Exercise(Style.BACKSTROKE)));
        doReturn(1).when(trainingCalculator).getNumberOfRepeatsInOneSeries(anyInt(), anyInt());
        Exercise exercise = new Exercise(Style.BACKSTROKE);
        exercise.setWarmUpRelax(true);
        when(exerciseRepository.findByWarmUpRelax(true)).thenReturn(Collections.singletonList(exercise));
        //when
        Training training = sut.generateTraining(user, trainingRequirements);
        //then
        assertThat(training).isNotNull();
    }

    //when user generates training the most important are given styles
    @Test
    public void generateTraining_whenStyles_shouldReturnTrainingInGivenStyles() throws Exception {
        //given
        User user = testUtil.createValidUser();
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        when(exerciseRepository.findByStyle(Style.FREESTYLE)).thenReturn(Collections.singletonList(new Exercise(Style.FREESTYLE)));
        when(exerciseRepository.findByStyle(Style.BACKSTROKE)).thenReturn(Collections.singletonList(new Exercise(Style.BACKSTROKE)));
        doReturn(1).when(trainingCalculator).getNumberOfRepeatsInOneSeries(anyInt(), anyInt());
        Exercise exercise = new Exercise(Style.BACKSTROKE);
        exercise.setWarmUpRelax(true);
        when(exerciseRepository.findByWarmUpRelax(true)).thenReturn(Collections.singletonList(exercise));
        //when
        Training training = sut.generateTraining(user, trainingRequirements);
        //then
        List<Style> stylesUsed = training.getExerciseSeries()
                .stream()
                .map(exerciseSeries1 -> exerciseSeries1.getExercise().getStyle())
                .distinct().collect(Collectors.toList());
        boolean areStylesCorrect = stylesUsed.stream().allMatch(style -> trainingRequirements.getStyles().contains(style));
        assertThat(areStylesCorrect).isTrue();
    }

    //when user generates training exercise series is created on the basis of intensity level
    @Test
    public void generateTraining_whenIntensityLevel_shouldCreateExerciseSeriesDueToIntensityLevel() throws Exception {
        //given
        User user = testUtil.createValidUser();
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        when(exerciseRepository.findByStyle(Style.FREESTYLE)).thenReturn(Collections.singletonList(new Exercise(Style.FREESTYLE)));
        when(exerciseRepository.findByStyle(Style.BACKSTROKE)).thenReturn(Collections.singletonList(new Exercise(Style.BACKSTROKE)));
        doReturn(1).when(trainingCalculator).getNumberOfRepeatsInOneSeries(anyInt(), anyInt());
        Exercise exercise = new Exercise(Style.BACKSTROKE);
        exercise.setWarmUpRelax(true);
        when(exerciseRepository.findByWarmUpRelax(true)).thenReturn(Collections.singletonList(exercise));
        //then
        Training training = sut.generateTraining(user, trainingRequirements);
        //then
        List<Integer> exerciseSeriesDistances = training.getExerciseSeries()
                .stream().filter(s -> !s.getExercise().isWarmUpRelax())
                .map(ExerciseSeries::getDistance)
                .collect(Collectors.toList());
        List<Integer> generatedDistances = IntensityLevel.LOW.getDistances();
        boolean areDistancesCorrect = exerciseSeriesDistances.stream().allMatch(distance -> generatedDistances.contains(distance));
        assertThat(areDistancesCorrect).isTrue();
    }

    @Test
    public void generateTraining_whenMaxDuration_shouldReturnTrainingWithGivenMaxDuration() throws Exception {
        //given
        User user = testUtil.createValidUser();
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        when(exerciseRepository.findByStyle(Style.FREESTYLE)).thenReturn(Collections.singletonList(new Exercise(Style.FREESTYLE)));
        when(exerciseRepository.findByStyle(Style.BACKSTROKE)).thenReturn(Collections.singletonList(new Exercise(Style.BACKSTROKE)));        //when
        doReturn(1).when(trainingCalculator).getNumberOfRepeatsInOneSeries(anyInt(), anyInt());
        Exercise exercise = new Exercise(Style.BACKSTROKE);
        exercise.setWarmUpRelax(true);
        when(exerciseRepository.findByWarmUpRelax(true)).thenReturn(Collections.singletonList(exercise));
        //when
        Training training = sut.generateTraining(user, trainingRequirements);
        //then
        assertThat(training.getDurationInSeconds()).isLessThanOrEqualTo(trainingRequirements.getMaxDurationInSeconds());
    }

    @Test
    public void adaptTrainingToMaxDistance_whenMaxDistanceIsShorterThanTrainingDistance_shouldReturnTrainingThatMaxDistanceIsLongerOrEqualToTrainingDistance() throws Exception {
        //given
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        trainingRequirements.setMaxDistance(500);
        Training training = testUtil.createValidTraining();
        //when
        Training trainingAfterAdaptation = sut.getAdaptedTrainingToMaxDistance(training, trainingRequirements.getMaxDistance());
        //then
        int distance = 0;
        for (ExerciseSeries series : trainingAfterAdaptation.getExerciseSeries()) {
            distance += series.getDistance() * series.getRepeats();
        }
        assertThat(distance).isLessThanOrEqualTo(trainingRequirements.getMaxDistance());
    }

    @Test
    public void adaptTrainingToMaxDistance_whenMaxDistanceIsLongerThanTrainingDistance_shouldReturnTheSameTrainingAsGivenAsParameter() throws Exception {
        //given
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        trainingRequirements.setMaxDistance(8000);
        Training training = testUtil.createValidTraining();
        //when
        Training trainingAfterAdaptation = sut.getAdaptedTrainingToMaxDistance(training, trainingRequirements.getMaxDistance());
        //then
        assertThat(trainingAfterAdaptation).isEqualTo(training);
    }

    @Test
    public void adaptTrainingToMaxDistance_whenMaxDistanceIsTheSameAsTrainingDistance_shouldReturnTheSameTrainingAsGivenAsParameter() throws Exception {
        //given
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        trainingRequirements.setMaxDistance(7500);
        Training training = testUtil.createValidTraining();
        //when
        Training trainingAfterAdaptation = sut.getAdaptedTrainingToMaxDistance(training, trainingRequirements.getMaxDistance());
        //then
        assertThat(trainingAfterAdaptation).isEqualTo(training);
    }

    @Test
    public void generateTraining_whenNoExercisesInGivenStyles_shouldReturn2ExerciseSeries() throws Exception {
        //given
        User user = testUtil.createValidUser();
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        when(exerciseRepository.findByStyle(Style.FREESTYLE)).thenReturn(Collections.emptyList());
        when(exerciseRepository.findByStyle(Style.BACKSTROKE)).thenReturn(Collections.emptyList());
        Exercise exercise = new Exercise(Style.BACKSTROKE);
        exercise.setWarmUpRelax(true);
        when(exerciseRepository.findByWarmUpRelax(true)).thenReturn(Collections.singletonList(exercise));
        //when
        Training training = sut.generateTraining(user, trainingRequirements);
        //then
        assertThat(training.getExerciseSeries().size()).isEqualTo(2);
    }
}