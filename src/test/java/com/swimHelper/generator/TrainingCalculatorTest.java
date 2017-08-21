package com.swimHelper.generator;

import com.swimHelper.TestUtil;
import com.swimHelper.exception.BusinessException;
import com.swimHelper.exception.UnsatisfiedTimeRequirementsException;
import com.swimHelper.model.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

/**
 * Created by mstobieniecka on 2017-08-01.
 */
public class TrainingCalculatorTest {

    private TestUtil testUtil = new TestUtil();
    private final Random random = new Random();
    private final TrainingCalculator sut = new TrainingCalculator(random);

    @Test
    public void getNumberOfExerciseSeries_whenLittleMaxDurationAndLowIntensityLevelGiven_shouldReturnNumberOfExerciseSeries() {
        //given
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        //when
        Integer numberOfExercisesSeries = sut.getNumberOfExerciseSeries(trainingRequirements.getIntensityLevel(), trainingRequirements.getMaxDurationInSeconds());
        //then
        assertThat(numberOfExercisesSeries).isEqualTo(1);
    }

    @Test
    public void getNumberOfExerciseSeries_whenAverageMaxDurationAndLowIntensityLevelGiven_shouldReturnNumberOfExerciseSeries() {
        //given
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        trainingRequirements.setMaxDurationInSeconds(2400);
        //when
        Integer numberOfExercisesSeries = sut.getNumberOfExerciseSeries(trainingRequirements.getIntensityLevel(), trainingRequirements.getMaxDurationInSeconds());
        List<Integer> availableValues = new ArrayList<>();
        availableValues.add(2);
        availableValues.add(3);
        availableValues.add(4);
        //then
        assertThat(numberOfExercisesSeries).isIn(availableValues);
    }

    @Test
    public void getNumberOfExerciseSeries_whenLongMaxDurationAndLowIntensityLevelGiven_shouldReturnNumberOfExerciseSeries() {
        //given
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        trainingRequirements.setMaxDurationInSeconds(3600);
        List<Integer> possibleNumberOfSeries = new ArrayList<>();
        possibleNumberOfSeries.add(3);
        possibleNumberOfSeries.add(4);
        //when
        Integer numberOfExercisesSeries = sut.getNumberOfExerciseSeries(trainingRequirements.getIntensityLevel(), trainingRequirements.getMaxDurationInSeconds());
        //then
        assertThat(numberOfExercisesSeries).isIn(possibleNumberOfSeries);
    }

    @Test
    public void getNumberOfExerciseSeries_whenLongMaxDurationAndHighIntensityLevelGiven_shouldReturnNumberOfExerciseSeries() {
        //given
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        trainingRequirements.setIntensityLevel(IntensityLevel.HIGH);
        trainingRequirements.setMaxDurationInSeconds(3000);
        List<Integer> numberOfExerciseSeriesForHighLevel = new ArrayList<>();
        numberOfExerciseSeriesForHighLevel.add(5);
        numberOfExerciseSeriesForHighLevel.add(6);
        numberOfExerciseSeriesForHighLevel.add(7);
        //when
        Integer numberOfExercisesSeries = sut.getNumberOfExerciseSeries(trainingRequirements.getIntensityLevel(), trainingRequirements.getMaxDurationInSeconds());
        //then
        assertThat(numberOfExercisesSeries).isIn(numberOfExerciseSeriesForHighLevel);
    }

    @Test
    public void getNumberOfExerciseSeries_whenAverageMaxDurationAndHighIntensityLevelGiven_shouldReturnNumberOfExerciseSeries() {
        //given
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        trainingRequirements.setIntensityLevel(IntensityLevel.HIGH);
        trainingRequirements.setMaxDurationInSeconds(2400);
        List<Integer> numberOfExerciseSeriesForHighLevel = new ArrayList<>();
        numberOfExerciseSeriesForHighLevel.add(3);
        numberOfExerciseSeriesForHighLevel.add(4);
        //when
        Integer numberOfExercisesSeries = sut.getNumberOfExerciseSeries(trainingRequirements.getIntensityLevel(), trainingRequirements.getMaxDurationInSeconds());
        //then
        assertThat(numberOfExercisesSeries).isIn(numberOfExerciseSeriesForHighLevel);
    }

    @Test
    public void getNumberOfExerciseSeries_whenShortMaxDurationAndHighIntensityLevelGiven_shouldReturnNumberOfExerciseSeries() {
        //given
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        trainingRequirements.setIntensityLevel(IntensityLevel.HIGH);
        trainingRequirements.setMaxDurationInSeconds(1500);
        //when
        Integer numberOfExercisesSeries = sut.getNumberOfExerciseSeries(trainingRequirements.getIntensityLevel(), trainingRequirements.getMaxDurationInSeconds());
        //then
        assertThat(numberOfExercisesSeries).isEqualTo(3);
    }

    @Test
    public void getNumberOfExerciseSeries_whenLittleMaxDurationGiven_shouldReturnNumberOfExerciseSeries() {
        //given
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        trainingRequirements.setIntensityLevel(IntensityLevel.MEDIUM);
        trainingRequirements.setMaxDurationInSeconds(600);
        //when
        Integer numberOfExercisesSeries = sut.getNumberOfExerciseSeries(trainingRequirements.getIntensityLevel(), trainingRequirements.getMaxDurationInSeconds());
        //then
        assertThat(numberOfExercisesSeries).isEqualTo(1);
    }

    @Test
    public void getDurationOfOneExerciseSeries_whenNumberOfExerciseSeriesAndMaxDurationGiven_shouldReturnDurationOfOneExerciseSeries() {
        //given
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        //when
        Integer durationOfOneExerciseSeries = sut.getDurationOfOneExerciseSeries(8, trainingRequirements.getMaxDurationInSeconds());
        //then
        assertThat(durationOfOneExerciseSeries).isEqualTo(75);
    }

    @Test
    public void getDurationOfOneExerciseRepeat_whenShortDistanceAndStyleStatisticsGiven_shouldReturnDurationOfOneExerciseRepeat() {
        //given
        User user = testUtil.createValidUser();
        ExerciseSeries exerciseSeries = new ExerciseSeries();
        exerciseSeries.setExercise(new Exercise(Style.FREESTYLE));
        exerciseSeries.setDistance(100);
        //when
        Integer durationOfOneExerciseRepeat = sut.getDurationOfOneExerciseRepeatInSeconds(exerciseSeries.getExercise(), exerciseSeries.getDistance(), user);
        StyleStatistics styleStatistics = user
                .getStyleStatistics().stream()
                .filter(styleStatistics1 -> styleStatistics1.getStyle().equals(exerciseSeries.getExercise().getStyle())).findFirst().get();
        //then
        assertThat(durationOfOneExerciseRepeat).isEqualTo(styleStatistics.getTimeInSeconds());
    }

    @Test
    public void getDurationOfOneExerciseRepeat_whenVeryShortDistanceAndStyleStatisticsGiven_shouldReturnDurationOfOneExerciseRepeat() {
        //given
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
        User user = testUtil.createValidUser();
        ExerciseSeries exerciseSeries = new ExerciseSeries();
        exerciseSeries.setExercise(new Exercise(Style.FREESTYLE));
        exerciseSeries.setDistance(50);
        //when
        Integer durationOfOneExerciseRepeat = sut.getDurationOfOneExerciseRepeatInSeconds(exerciseSeries.getExercise(), exerciseSeries.getDistance(), user);
        StyleStatistics styleStatistics = user
                .getStyleStatistics().stream()
                .filter(styleStatistics1 -> styleStatistics1.getStyle().equals(exerciseSeries.getExercise().getStyle())).findFirst().get();
        //then
        assertThat(durationOfOneExerciseRepeat).isEqualTo(styleStatistics.getTimeInSeconds() / 2);
    }

    @Test
    public void getDurationOfOneExerciseRepeat_whenMediumDistanceAndStyleStatisticsGiven_shouldReturnDurationOfOneExerciseRepeat() {
        //given
        TrainingRequirements trainingRequirements = testUtil.createValidTrainingRequirements();
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
    public void getDurationOfOneExerciseRepeat_whenQuiteLongDistanceAndStyleStatisticsGiven_shouldReturnDurationOfOneExerciseRepeat() {
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
    public void getDurationOfOneExerciseRepeat_whenLongDistanceAndStyleStatisticsGiven_shouldReturnDurationOfOneExerciseRepeat() {
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
    public void getBreakOfOneExerciseRepeatInSeconds_whenLowIntensityLevelAndDurationOfOneExerciseSeriesGiven_shouldReturnBreakForOneExerciseRepeat() {
        //given
        int durationOfOneExerciseRepeat = 300;
        IntensityLevel intensityLevel = IntensityLevel.LOW;
        //when
        int breakDurationOfOneExerciseRepeat = sut.getBreakOfOneExerciseRepeatInSeconds(intensityLevel, durationOfOneExerciseRepeat);
        //then
        assertThat(breakDurationOfOneExerciseRepeat).isEqualTo(50);
    }

    @Test
    public void getBreakOfOneExerciseRepeatInSeconds_whenMediumIntensityLevelAndDurationOfOneExerciseSeriesGiven_shouldReturnBreakForOneExerciseRepeat() {
        //given
        int durationOfOneExerciseRepeat = 300;
        IntensityLevel intensityLevel = IntensityLevel.MEDIUM;
        //when
        int breakDurationOfOneExerciseRepeat = sut.getBreakOfOneExerciseRepeatInSeconds(intensityLevel, durationOfOneExerciseRepeat);
        //then
        assertThat(breakDurationOfOneExerciseRepeat).isEqualTo(40);
    }

    @Test
    public void getBreakOfOneExerciseRepeatInSeconds_whenHighIntensityLevelAndDurationOfOneExerciseSeriesGiven_shouldReturnBreakForOneExerciseRepeat() {
        //given
        int durationOfOneExerciseRepeat = 300;
        IntensityLevel intensityLevel = IntensityLevel.HIGH;
        //when
        int breakDurationOfOneExerciseRepeat = sut.getBreakOfOneExerciseRepeatInSeconds(intensityLevel, durationOfOneExerciseRepeat);
        //then
        assertThat(breakDurationOfOneExerciseRepeat).isEqualTo(30);
    }

    @Test
    public void getNumberOfRepeatsInOneSeries_whenSeriesDurationAndOneRepeatDurationAndBreakGiven_shouldReturnNumberOfRepeats() throws BusinessException {
        //given
        int durationOfOneExerciseRepeat = 200;
        int durationOfSeries = 800;
        int durationOfBreak = 30;
        //when
        int numberOfRepeatsInSeries = sut.getNumberOfRepeatsInOneSeries(durationOfSeries, durationOfOneExerciseRepeat, durationOfBreak);
        //then
        assertThat(numberOfRepeatsInSeries).isEqualTo(3);
    }

    @Test
    public void getNumberOfRepeatsInOneSeries_whenSeriesDurationAndOneRepeatDurationAndBreakGiven_shouldThrowException() throws BusinessException {
        //given
        int durationOfOneExerciseRepeat = 200;
        int durationOfSeries = 200;
        int durationOfBreak = 30;
        //when
        Throwable throwable = catchThrowable(() -> sut.getNumberOfRepeatsInOneSeries(durationOfSeries, durationOfOneExerciseRepeat, durationOfBreak));
        //then
        assertThat(throwable).isInstanceOf(UnsatisfiedTimeRequirementsException.class);
    }

    @Test
    public void calculateDistanceOfTraining_whenExerciseSeriesGiven_shouldReturnDistanceOfTrainingInMeters() {
        //given
        Training training = testUtil.createValidTraining();
        //when
        int distanceOfTraining = sut.calculateDistanceOfTraining(new ArrayList<>(training.getExerciseSeries()));
        //then
        assertThat(distanceOfTraining).isEqualTo(7500);
    }
}
