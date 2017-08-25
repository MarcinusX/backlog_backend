package com.swimHelper.generator;

import com.swimHelper.exception.UnsatisfiedTimeRequirementsException;
import com.swimHelper.model.*;
import com.swimHelper.util.RandomGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * Created by mstobieniecka on 2017-08-01.
 */
@Component
public class TrainingCalculator {

    private final RandomGenerator randomGenerator;
    private static final int MAX_DURATION_OF_SHORT_TRAINING_IN_SECONDS = 900;
    private static final int MAX_DURATION_OF_AVERAGE_TRAINING_IN_SECONDS = 1800;
    private static final int MIN_DURATION_OF_LONG_TRAINING_IN_SECONDS = 2700;
    private static final int MIN_EXERCISE_SERIES_DISTANCE_IN_METERS = 25;
    private static final int STYLE_STATISTICS_DISTANCE_IN_METERS = 100;
    public static final int WARMUP_AND_RELAX_DURATION = 900;

    @Autowired
    public TrainingCalculator(RandomGenerator randomGenerator) {
        this.randomGenerator = randomGenerator;
    }

    int getNumberOfExerciseSeries(IntensityLevel intensityLevel, int maxDurationInSeconds) throws UnsatisfiedTimeRequirementsException {
        int timeForMainPartOfTraining = maxDurationInSeconds - WARMUP_AND_RELAX_DURATION;
        if (timeForMainPartOfTraining < 0) {
            throw new UnsatisfiedTimeRequirementsException();
        } else if (timeForMainPartOfTraining == 0) {
            return 0;
        } else if (timeForMainPartOfTraining <= MAX_DURATION_OF_SHORT_TRAINING_IN_SECONDS) {
            return 1;
        } else if (intensityLevel.equals(IntensityLevel.LOW)) {
            if (timeForMainPartOfTraining <= (MAX_DURATION_OF_AVERAGE_TRAINING_IN_SECONDS)) {
                return 3;
            } else if (timeForMainPartOfTraining > MAX_DURATION_OF_AVERAGE_TRAINING_IN_SECONDS && timeForMainPartOfTraining <= MIN_DURATION_OF_LONG_TRAINING_IN_SECONDS) {
                return 4;
            } else {
                return randomGenerator.generateRandomIntFromRange(5, 6);
            }
        } else {
            if (timeForMainPartOfTraining <= MAX_DURATION_OF_AVERAGE_TRAINING_IN_SECONDS) {
                return 4;
            } else if (timeForMainPartOfTraining > MAX_DURATION_OF_AVERAGE_TRAINING_IN_SECONDS && timeForMainPartOfTraining <= MIN_DURATION_OF_LONG_TRAINING_IN_SECONDS) {
                return randomGenerator.generateRandomIntFromRange(5, 6);
            } else {
                return randomGenerator.generateRandomIntFromRange(7, 9);
            }
        }
    }

    int getDurationOfOneExerciseSeries(int numberOfExerciseSeries, int maxDurationInMinutes) {
        return maxDurationInMinutes / numberOfExerciseSeries;
    }

    //before invoking this method there is checking if user has style statistics according to
    //training requirements
    int getDurationOfOneExerciseRepeatInSeconds(Exercise exercise, int distance, User user) {
        StyleStatistics styleStatistics = user.getStyleStatistics()
                .stream()
                .filter(styleStatistics1 -> exercise.getStyle().equals(styleStatistics1.getStyle()))
                .findFirst().get();
        int userTimeInStyle = styleStatistics.getTimeInSeconds();
        if (distance == MIN_EXERCISE_SERIES_DISTANCE_IN_METERS) {
            return userTimeInStyle / 4;
        } else if (distance == 50) {
            return userTimeInStyle / 2;
        } else if (distance == STYLE_STATISTICS_DISTANCE_IN_METERS) {
            return userTimeInStyle;
        } else {
            double multipleTimeFactor = distance / STYLE_STATISTICS_DISTANCE_IN_METERS;
            double increasingDurationFactor = 0.4 * multipleTimeFactor * multipleTimeFactor * 1 / 7 * userTimeInStyle;
            Double durationOfOneExerciseSeriesInSeconds = multipleTimeFactor * userTimeInStyle + increasingDurationFactor;
            return durationOfOneExerciseSeriesInSeconds.intValue();
        }
    }

    int getBreakOfOneExerciseRepeatInSeconds(IntensityLevel intensityLevel, int durationOfOneExerciseRepeatInSeconds) {
        int breakOfOneExerciseRepeatInSeconds;
        if (intensityLevel.equals(IntensityLevel.LOW)) {
            breakOfOneExerciseRepeatInSeconds = durationOfOneExerciseRepeatInSeconds / 6;
        } else if (intensityLevel.equals(IntensityLevel.MEDIUM)) {
            breakOfOneExerciseRepeatInSeconds = durationOfOneExerciseRepeatInSeconds / 7;
        } else {
            breakOfOneExerciseRepeatInSeconds = durationOfOneExerciseRepeatInSeconds / 8;
        }
        return (breakOfOneExerciseRepeatInSeconds - breakOfOneExerciseRepeatInSeconds % 10);
    }

    int getNumberOfRepeatsInOneSeries(int durationOfSeriesInSeconds, int durationOfRepeatAndBreak) throws UnsatisfiedTimeRequirementsException {
        int numberOfRepeatsInOneSeries = durationOfSeriesInSeconds / durationOfRepeatAndBreak;
//        if (numberOfRepeatsInOneSeries < 1) {
//            throw new UnsatisfiedTimeRequirementsException();
//        }
        return numberOfRepeatsInOneSeries;
    }

    int getRandomDistanceForIntensityLevel(List<Integer> availableDistances) {
        int randomDistanceIndex = randomGenerator.generateRandomInt(availableDistances.size());
        return availableDistances.get(randomDistanceIndex);
    }

    int calculateDistanceOfTraining(Collection<ExerciseSeries> exerciseSeries) {
        int distance = 0;
        for (ExerciseSeries series : exerciseSeries) {
            distance += series.getDistance() * series.getRepeats();
        }
        return distance;
    }

}
