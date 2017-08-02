package com.swimHelper.generator;

import com.swimHelper.TestUtil;
import com.swimHelper.exception.BusinessException;
import com.swimHelper.exception.MissingTrainingRequirementsException;
import com.swimHelper.model.*;
import com.swimHelper.repository.ExerciseRepository;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by mstobieniecka on 2017-07-19.
 */
public class TrainingGeneratorTest {

    private final ExerciseRepository exerciseRepository = mock(ExerciseRepository.class);
    private final TrainingCalculator trainingCalculator = mock(TrainingCalculator.class);
    private final Random random = new Random();
    private final TrainingGenerator sut = new TrainingGenerator(exerciseRepository, trainingCalculator, random);

    @Test
    public void generateTraining_whenValidParameters_doesntThrowException() throws Exception {
        //given
        User user = TestUtil.createValidUser();
        TrainingRequirements trainingRequirements = TestUtil.createValidTrainingRequirements();
        when(exerciseRepository.findByStyle(Style.FREESTYLE)).thenReturn(Collections.singletonList(new Exercise(Style.FREESTYLE)));
        when(exerciseRepository.findByStyle(Style.BACKSTROKE)).thenReturn(Collections.singletonList(new Exercise(Style.BACKSTROKE)));
        //when
        sut.generateTraining(user, trainingRequirements);
        //then
        //pass
    }

    //when user chooses training style he has to have style statistics
    @Test
    public void generateTraining_whenMissingUserData_shouldThrowException() {
        //given
        User user = TestUtil.createValidUser();
        user.setStyleStatistics(new ArrayList<>());
        TrainingRequirements trainingRequirements = TestUtil.createValidTrainingRequirements();
        //when
        Throwable throwable = catchThrowable(() -> sut.generateTraining(user, trainingRequirements));
        //then
        assertThat(throwable).isInstanceOf(MissingTrainingRequirementsException.class);
    }

    //when user wants to generate training he has to choose at least one style
    @Test
    public void generateTraining_whenMissingStyles_shouldThrowException() {
        //given
        User user = TestUtil.createValidUser();
        TrainingRequirements trainingRequirements = TestUtil.createValidTrainingRequirements();
        trainingRequirements.setStyles(new ArrayList<>());
        //when
        Throwable throwable = catchThrowable(() -> sut.generateTraining(user, trainingRequirements));
        //then
        assertThat(throwable).isInstanceOf(MissingTrainingRequirementsException.class);
    }

    //when user wants to generate training he has to choose difficulty level
    @Test
    public void generateTraining_whenMissingDifficultyLevel_shouldThrowException() {
        //given
        User user = TestUtil.createValidUser();
        TrainingRequirements trainingRequirements = TestUtil.createValidTrainingRequirements();
        trainingRequirements.setDifficultyLevel(null);
        //when
        Throwable throwable = catchThrowable(() -> sut.generateTraining(user, trainingRequirements));
        //then
        assertThat(throwable).isInstanceOf(MissingTrainingRequirementsException.class);
    }

    //when user wants to generate training he has to choose intensity level
    @Test
    public void generateTraining_whenMissingIntensityLevel_shouldThrowException() {
        //given
        User user = TestUtil.createValidUser();
        TrainingRequirements trainingRequirements = TestUtil.createValidTrainingRequirements();
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
        User user = TestUtil.createValidUser();
        TrainingRequirements trainingRequirements = TestUtil.createValidTrainingRequirements();
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
        User user = TestUtil.createValidUser();
        TrainingRequirements trainingRequirements = TestUtil.createValidTrainingRequirements();
        trainingRequirements.setMaxDistance(0);
        when(exerciseRepository.findByStyle(Style.FREESTYLE)).thenReturn(Collections.singletonList(new Exercise(Style.FREESTYLE)));
        when(exerciseRepository.findByStyle(Style.BACKSTROKE)).thenReturn(Collections.singletonList(new Exercise(Style.BACKSTROKE)));
        //when
        sut.generateTraining(user, trainingRequirements);
        //then
        //pass
    }

    //when user wants to generate training he has to write max duration of the training or max distance
    @Test
    public void generateTraining_whenMissingMaxDuration_shouldNotThrowException() throws Exception {
        //given
        User user = TestUtil.createValidUser();
        TrainingRequirements trainingRequirements = TestUtil.createValidTrainingRequirements();
        trainingRequirements.setMaxDurationInSeconds(0);
        when(exerciseRepository.findByStyle(Style.FREESTYLE)).thenReturn(Collections.singletonList(new Exercise(Style.FREESTYLE)));
        when(exerciseRepository.findByStyle(Style.BACKSTROKE)).thenReturn(Collections.singletonList(new Exercise(Style.BACKSTROKE)));
        //when
        sut.generateTraining(user, trainingRequirements);
        //then
        //pass
    }

    //check if training is not null
    @Test
    public void generateTraining_whenValidParameters_doesntReturnNull() throws Exception {
        //given
        User user = TestUtil.createValidUser();
        TrainingRequirements trainingRequirements = TestUtil.createValidTrainingRequirements();
        when(exerciseRepository.findByStyle(Style.FREESTYLE)).thenReturn(Collections.singletonList(new Exercise(Style.FREESTYLE)));
        when(exerciseRepository.findByStyle(Style.BACKSTROKE)).thenReturn(Collections.singletonList(new Exercise(Style.BACKSTROKE)));
        //when
        Training training = sut.generateTraining(user, trainingRequirements);
        //then
        assertThat(training).isNotNull();
    }

    //when user generates training the most important are given styles
    @Test
    public void generateTraining_whenStylesGiven_shouldReturnTrainingInGivenStyles() throws BusinessException {
        //given
        User user = TestUtil.createValidUser();
        TrainingRequirements trainingRequirements = TestUtil.createValidTrainingRequirements();
        when(exerciseRepository.findByStyle(Style.FREESTYLE)).thenReturn(Collections.singletonList(new Exercise(Style.FREESTYLE)));
        when(exerciseRepository.findByStyle(Style.BACKSTROKE)).thenReturn(Collections.singletonList(new Exercise(Style.BACKSTROKE)));
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
    public void generateTraining_whenIntensityLevelGiven_shouldCreateExerciseSeriesDueToIntensityLevel() throws Exception {
        //given
        User user = TestUtil.createValidUser();
        TrainingRequirements trainingRequirements = TestUtil.createValidTrainingRequirements();
        when(exerciseRepository.findByStyle(Style.FREESTYLE)).thenReturn(Collections.singletonList(new Exercise(Style.FREESTYLE)));
        when(exerciseRepository.findByStyle(Style.BACKSTROKE)).thenReturn(Collections.singletonList(new Exercise(Style.BACKSTROKE)));
        //then
        Training training = sut.generateTraining(user, trainingRequirements);
        //then
        List<Integer> exerciseSeriesDistances = training.getExerciseSeries()
                .stream()
                .map(ExerciseSeries::getDistance)
                .collect(Collectors.toList());
        boolean areDistancesCorrect = exerciseSeriesDistances.stream().allMatch(distance -> IntensityLevel.LOW.getDistances().contains(distance));
        assertThat(areDistancesCorrect).isTrue();
    }

    @Test
    public void generateTraining_whenMaxDurationGiven_shouldReturnTrainingWithGivenMaxDuration() throws BusinessException {
        //given
        User user = TestUtil.createValidUser();
        TrainingRequirements trainingRequirements = TestUtil.createValidTrainingRequirements();
        when(exerciseRepository.findByStyle(Style.FREESTYLE)).thenReturn(Collections.singletonList(new Exercise(Style.FREESTYLE)));
        when(exerciseRepository.findByStyle(Style.BACKSTROKE)).thenReturn(Collections.singletonList(new Exercise(Style.BACKSTROKE)));        //when
        //when
        Training training = sut.generateTraining(user, trainingRequirements);
        //then
        assertThat(training.getDurationInSeconds()).isLessThanOrEqualTo(trainingRequirements.getMaxDurationInSeconds());
    }




}