package com.swimHelper.generator;

import com.swimHelper.TestUtil;
import com.swimHelper.exception.BusinessException;
import com.swimHelper.exception.UnsatisfiedTimeRequirementsException;
import com.swimHelper.model.*;
import com.swimHelper.util.RandomGenerator;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

/**
 * Created by mstobieniecka on 2017-08-01.
 */
public class TrainingCalculatorTest {

    private TestUtil testUtil = new TestUtil();
    private final RandomGenerator random = new RandomGenerator();
    private final TrainingCalculator sut = new TrainingCalculator(random);

    @Test
    public void getNumberOfExerciseSeries_whenLittleMaxDurationAndLowIntensityLevel_shouldThrowException() throws BusinessException {
        //given
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        trainingRequirements.setMaxDurationInSeconds(600);
        //when
        Throwable throwable = catchThrowable(() -> sut.getNumberOfExerciseSeries(trainingRequirements.getIntensityLevel(), trainingRequirements.getMaxDurationInSeconds()));
        //then
        assertThat(throwable).isInstanceOf(UnsatisfiedTimeRequirementsException.class);
    }

    @Test
    public void getNumberOfExerciseSeries_whenAverageMaxDurationAndLowIntensityLevel_shouldReturnBetween4and6ExerciseSeries() throws BusinessException {
        //given
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        trainingRequirements.setMaxDurationInSeconds(3300);
        //when
        Integer numberOfExercisesSeries = sut.getNumberOfExerciseSeries(trainingRequirements.getIntensityLevel(), trainingRequirements.getMaxDurationInSeconds());
        //then
        List<Integer> expectedAvailableValues = Arrays.asList(4, 5, 6);
        assertThat(numberOfExercisesSeries).isIn(expectedAvailableValues);
    }

    @Test
    public void getNumberOfExerciseSeries_whenLongMaxDurationAndLowIntensityLevel_shouldReturn5or6ExerciseSeries() throws BusinessException {
        //given
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        trainingRequirements.setMaxDurationInSeconds(3700);
        //when
        Integer numberOfExercisesSeries = sut.getNumberOfExerciseSeries(trainingRequirements.getIntensityLevel(), trainingRequirements.getMaxDurationInSeconds());
        //then
        List<Integer> expectedPossibleNumberOfSeries = Arrays.asList(5, 6);
        assertThat(numberOfExercisesSeries).isIn(expectedPossibleNumberOfSeries);
    }

    @Test
    public void getNumberOfExerciseSeries_whenLongMaxDurationAndHighIntensityLevel_shouldReturnBetween7and9ExerciseSeries() throws BusinessException {
        //given
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        trainingRequirements.setIntensityLevel(IntensityLevel.HIGH);
        trainingRequirements.setMaxDurationInSeconds(4000);
        //when
        Integer numberOfExercisesSeries = sut.getNumberOfExerciseSeries(trainingRequirements.getIntensityLevel(), trainingRequirements.getMaxDurationInSeconds());
        //then
        List<Integer> expectedNumberOfExerciseSeriesForHighLevel = Arrays.asList(7, 8, 9);
        assertThat(numberOfExercisesSeries).isIn(expectedNumberOfExerciseSeriesForHighLevel);
    }

    @Test
    public void getNumberOfExerciseSeries_whenAverageMaxDurationAndHighIntensityLevel_shouldReturnBetween7And9ExerciseSeries() throws BusinessException {
        //given
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        trainingRequirements.setIntensityLevel(IntensityLevel.HIGH);
        trainingRequirements.setMaxDurationInSeconds(3700);
        //when
        Integer numberOfExercisesSeries = sut.getNumberOfExerciseSeries(trainingRequirements.getIntensityLevel(), trainingRequirements.getMaxDurationInSeconds());
        //then
        List<Integer> expectedNumberOfExerciseSeriesForHighLevel = Arrays.asList(7, 8, 9);
        assertThat(numberOfExercisesSeries).isIn(expectedNumberOfExerciseSeriesForHighLevel);
    }

    @Test
    public void getNumberOfExerciseSeries_whenShortMaxDurationAndHighIntensityLevel_shouldReturn1ExerciseSeries() throws BusinessException {
        //given
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        trainingRequirements.setIntensityLevel(IntensityLevel.HIGH);
        trainingRequirements.setMaxDurationInSeconds(1500);
        //when
        Integer numberOfExercisesSeries = sut.getNumberOfExerciseSeries(trainingRequirements.getIntensityLevel(), trainingRequirements.getMaxDurationInSeconds());
        //then
        assertThat(numberOfExercisesSeries).isEqualTo(1);
    }

    @Test
    public void getNumberOfExerciseSeries_whenLittleMaxDuration_shouldReturn1ExerciseSeries() throws BusinessException {
        //given
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        trainingRequirements.setIntensityLevel(IntensityLevel.MEDIUM);
        trainingRequirements.setMaxDurationInSeconds(1800);
        //when
        Integer numberOfExercisesSeries = sut.getNumberOfExerciseSeries(trainingRequirements.getIntensityLevel(), trainingRequirements.getMaxDurationInSeconds());
        //then
        assertThat(numberOfExercisesSeries).isEqualTo(1);
    }

    @Test
    public void getDurationOfOneExerciseSeries_whenNumberOfExerciseSeriesAndMaxDuration_shouldReturn375Seconds() {
        //given
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        //when
        Integer durationOfOneExerciseSeries = sut.getDurationOfOneExerciseSeries(8, trainingRequirements.getMaxDurationInSeconds());
        //then
        assertThat(durationOfOneExerciseSeries).isEqualTo(375);
    }

    @Test
    public void getDurationOfOneExerciseRepeat_whenShortDistanceAndStyleStatistics_shouldReturnCalculatedDurationOfOneExerciseRepeat() {
        //given
        User user = testUtil.createValidUser();
        ExerciseSeries exerciseSeries = new ExerciseSeries();
        exerciseSeries.setExercise(new Exercise(Style.FREESTYLE));
        exerciseSeries.setDistance(100);
        //when
        Integer durationOfOneExerciseRepeat = sut.getDurationOfOneExerciseRepeatInSeconds(exerciseSeries.getExercise(), exerciseSeries.getDistance(), user);
        //then
        StyleStatistics styleStatistics = user
                .getStyleStatistics().stream()
                .filter(styleStatistics1 -> styleStatistics1.getStyle().equals(exerciseSeries.getExercise().getStyle())).findFirst().get();
        assertThat(durationOfOneExerciseRepeat).isEqualTo(styleStatistics.getTimeInSeconds());
    }

    @Test
    public void getDurationOfOneExerciseRepeat_whenVeryShortDistanceAndStyleStatistics_shouldReturnCalculatedDurationOfOneExerciseRepeat() {
        //given
        User user = testUtil.createValidUser();
        ExerciseSeries exerciseSeries = new ExerciseSeries();
        exerciseSeries.setExercise(new Exercise(Style.FREESTYLE));
        exerciseSeries.setDistance(50);
        //when
        Integer durationOfOneExerciseRepeat = sut.getDurationOfOneExerciseRepeatInSeconds(exerciseSeries.getExercise(), exerciseSeries.getDistance(), user);
        //then
        StyleStatistics styleStatistics = user
                .getStyleStatistics().stream()
                .filter(styleStatistics1 -> styleStatistics1.getStyle().equals(exerciseSeries.getExercise().getStyle())).findFirst().get();
        assertThat(durationOfOneExerciseRepeat).isEqualTo(styleStatistics.getTimeInSeconds() / 2);
    }

    @Test
    public void getDurationOfOneExerciseRepeat_whenMediumDistanceAndStyleStatistics_shouldReturn351Seconds() {
        //given
        User user = testUtil.createValidUser();
        ExerciseSeries exerciseSeries = new ExerciseSeries();
        exerciseSeries.setExercise(new Exercise(Style.FREESTYLE));
        exerciseSeries.setDistance(300);
        //when
        Integer durationOfOneExerciseRepeat = sut.getDurationOfOneExerciseRepeatInSeconds(exerciseSeries.getExercise(), exerciseSeries.getDistance(), user);
        //then
        assertThat(durationOfOneExerciseRepeat).isEqualTo(351);
    }

    @Test
    public void getDurationOfOneExerciseRepeat_whenQuiteLongDistanceAndStyleStatistics_shouldReturn642Seconds() {
        //given
        User user = testUtil.createValidUser();
        ExerciseSeries exerciseSeries = new ExerciseSeries();
        exerciseSeries.setExercise(new Exercise(Style.FREESTYLE));
        exerciseSeries.setDistance(500);
        //when
        Integer durationOfOneExerciseRepeat = sut.getDurationOfOneExerciseRepeatInSeconds(exerciseSeries.getExercise(), exerciseSeries.getDistance(), user);
        //then
        assertThat(durationOfOneExerciseRepeat).isEqualTo(642);
    }

    @Test
    public void getDurationOfOneExerciseRepeat_whenLongDistanceAndStyleStatistics_shouldReturn1165Seconds() {
        //given
        User user = testUtil.createValidUser();
        ExerciseSeries exerciseSeries = new ExerciseSeries();
        exerciseSeries.setExercise(new Exercise(Style.FREESTYLE));
        exerciseSeries.setDistance(800);
        //when
        Integer durationOfOneExerciseRepeat = sut.getDurationOfOneExerciseRepeatInSeconds(exerciseSeries.getExercise(), exerciseSeries.getDistance(), user);
        //then
        assertThat(durationOfOneExerciseRepeat).isEqualTo(1165);
    }

    @Test
    public void getBreakOfOneExerciseRepeatInSeconds_whenLowIntensityLevelAndDurationOfOneExerciseSeries_shouldReturn50Seconds() {
        //given
        int durationOfOneExerciseRepeat = 300;
        IntensityLevel intensityLevel = IntensityLevel.LOW;
        //when
        int breakDurationOfOneExerciseRepeat = sut.getBreakOfOneExerciseRepeatInSeconds(intensityLevel, durationOfOneExerciseRepeat);
        //then
        assertThat(breakDurationOfOneExerciseRepeat).isEqualTo(50);
    }

    @Test
    public void getBreakOfOneExerciseRepeatInSeconds_whenMediumIntensityLevelAndDurationOfOneExerciseSeries_shouldReturn40Seconds() {
        //given
        int durationOfOneExerciseRepeat = 300;
        IntensityLevel intensityLevel = IntensityLevel.MEDIUM;
        //when
        int breakDurationOfOneExerciseRepeat = sut.getBreakOfOneExerciseRepeatInSeconds(intensityLevel, durationOfOneExerciseRepeat);
        //then
        assertThat(breakDurationOfOneExerciseRepeat).isEqualTo(40);
    }

    @Test
    public void getBreakOfOneExerciseRepeatInSeconds_whenHighIntensityLevelAndDurationOfOneExerciseSeries_shouldReturn30Seconds() {
        //given
        int durationOfOneExerciseRepeat = 300;
        IntensityLevel intensityLevel = IntensityLevel.HIGH;
        //when
        int breakDurationOfOneExerciseRepeat = sut.getBreakOfOneExerciseRepeatInSeconds(intensityLevel, durationOfOneExerciseRepeat);
        //then
        assertThat(breakDurationOfOneExerciseRepeat).isEqualTo(30);
    }

    @Test
    public void getNumberOfRepeatsInOneSeries_whenSeriesDurationAndOneRepeatDurationAndBreak_shouldReturn3Repeats() throws BusinessException {
        //given
        int durationOfOneExerciseRepeat = 200;
        int durationOfSeries = 800;
        int durationOfBreak = 30;
        //when
        int numberOfRepeatsInSeries = sut.getNumberOfRepeatsInOneSeries(durationOfSeries, durationOfOneExerciseRepeat + durationOfBreak);
        //then
        assertThat(numberOfRepeatsInSeries).isEqualTo(3);
    }

    @Test
    public void getNumberOfRepeatsInOneSeries_whenSeriesDurationAndOneRepeatDurationAndBreak_shouldReturn0Repeats() throws BusinessException {
        //given
        int durationOfOneExerciseRepeat = 200;
        int durationOfSeries = 200;
        int durationOfBreak = 30;
        //when
        int numberOfRepeats = sut.getNumberOfRepeatsInOneSeries(durationOfSeries, durationOfOneExerciseRepeat + durationOfBreak);
        //then
        assertThat(numberOfRepeats).isEqualTo(0);
    }

    @Test
    public void calculateDistanceOfTraining_whenExerciseSeries_shouldReturn7500Meters() {
        //given
        Training training = testUtil.createValidTraining();
        //when
        int distanceOfTraining = sut.calculateDistanceOfTraining(new ArrayList<>(training.getExerciseSeries()));
        //then
        assertThat(distanceOfTraining).isEqualTo(7500);
    }
}
