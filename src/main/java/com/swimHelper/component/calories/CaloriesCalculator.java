package com.swimHelper.component.calories;

import com.swimHelper.model.ExerciseSeries;
import com.swimHelper.model.Style;
import org.springframework.stereotype.Component;

/**
 * Created by Marcin Szalek on 04.09.17.
 */
@Component
public class CaloriesCalculator {

    private static final double CONST_MULTIPLIER = 0.0175;
    private static final double CONST_SPEED_FACTOR = 0.5;

    private final CaloryBurnFactorFactory factorFactory;

    public CaloriesCalculator(CaloryBurnFactorFactory caloryBurnFactorFactory) {
        this.factorFactory = caloryBurnFactorFactory;
    }

    public int calculateCalories(ExerciseSeries series, double weight) {
        Style style = series.getExercise().getStyle();
        double caloriesPerMinute = CONST_MULTIPLIER * weight * factorFactory.getFactor(style);
        double caloriesPerRepeat = caloriesPerMinute * series.getAverageDurationOfOneRepeatInSeconds() / 60.0;
        double caloriesForSerie = caloriesPerRepeat * series.getCompletedRepeats();
        double baseSpeed = factorFactory.getBaseSpeed(style);
        double actualSpeed = series.getDistance() / (series.getAverageDurationOfOneRepeatInSeconds() * 1.0);
        double speedFactor = 1 + CONST_SPEED_FACTOR * (actualSpeed - baseSpeed);
        int totalCalories = (int) (caloriesForSerie * speedFactor);
        return totalCalories;
    }
}
