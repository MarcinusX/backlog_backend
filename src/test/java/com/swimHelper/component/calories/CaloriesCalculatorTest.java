package com.swimHelper.component.calories;

import com.swimHelper.model.Exercise;
import com.swimHelper.model.ExerciseSeries;
import com.swimHelper.model.Style;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Marcin Szalek on 04.09.17.
 * https://www.hss.edu/conditions_burning-calories-with-exercise-calculating-estimated-energy-expenditure.asp
 */
public class CaloriesCalculatorTest {

    private final CaloryBurnFactorFactory caloryBurnFactorFactory = new CaloryBurnFactorFactory();//To simple to mock
    private final CaloriesCalculator sut = new CaloriesCalculator(caloryBurnFactorFactory);

    @Test
    public void calculateCalories_forSeries() throws Exception {
        //given
        double weight = 70.0;
        Exercise exercise = new Exercise();
        exercise.setStyle(Style.BACKSTROKE);
        ExerciseSeries series = new ExerciseSeries();
        series.setDistance(25);
        series.setCompletedRepeats(8);
        series.setAverageDurationOfOneRepeatInSeconds(30);
        series.setExercise(exercise);
        //when
        int caloriesBurned = sut.calculateCalories(series, weight);
        //then
        assertThat(caloriesBurned).isGreaterThan(40);
        assertThat(caloriesBurned).isLessThan(60);
    }

    @Test
    public void calculateCalories_forSeriesButterfly() throws Exception {
        //given
        double weight = 55.0;
        Exercise exercise = new Exercise();
        exercise.setStyle(Style.BUTTERFLY);
        ExerciseSeries series = new ExerciseSeries();
        series.setDistance(50);
        series.setCompletedRepeats(4);
        series.setAverageDurationOfOneRepeatInSeconds(50);
        series.setExercise(exercise);
        //when
        int caloriesBurned = sut.calculateCalories(series, weight);
        //then
        assertThat(caloriesBurned).isEqualTo(41);
    }

    @Test
    public void calculateCalories_moreSpeedIsMoreCalories() throws Exception {
        //given
        double weight = 70.0;
        Exercise exercise = new Exercise();
        exercise.setStyle(Style.BACKSTROKE);
        ExerciseSeries series = new ExerciseSeries();
        series.setDistance(25);
        series.setCompletedRepeats(8);
        series.setAverageDurationOfOneRepeatInSeconds(30);
        series.setExercise(exercise);
        //when
        int caloriesBurned200m = sut.calculateCalories(series, weight);
        series.setDistance(50);
        int caloriesBurned400m = sut.calculateCalories(series, weight);
        //then
        assertThat(caloriesBurned400m).isGreaterThan(caloriesBurned200m);
    }

    @Test
    public void calculateCalories_moreTimeMoreCalories() throws Exception {
        //given
        double weight = 70.0;
        Exercise exercise = new Exercise();
        exercise.setStyle(Style.BACKSTROKE);
        ExerciseSeries series = new ExerciseSeries();
        series.setDistance(25);
        series.setCompletedRepeats(8);
        series.setAverageDurationOfOneRepeatInSeconds(30);
        series.setExercise(exercise);
        //when
        int caloriesBurnedMedium = sut.calculateCalories(series, weight);
        series.setAverageDurationOfOneRepeatInSeconds(60);
        series.setDistance(50);
        int caloriesBurnedFast = sut.calculateCalories(series, weight);
        //then
        assertThat(caloriesBurnedFast).isGreaterThan(caloriesBurnedMedium);
    }

}