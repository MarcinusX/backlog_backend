package com.swimHelper.generator;

import com.swimHelper.exception.UnsatisfiedTimeRequirementsException;
import com.swimHelper.model.Exercise;
import com.swimHelper.model.IntensityLevel;
import com.swimHelper.model.StyleStatistics;
import com.swimHelper.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by mstobieniecka on 2017-08-01.
 */
@Component
public class TrainingCalculator {

    private final Random randomGenerator;

    @Autowired
    public TrainingCalculator(Random randomGenerator) {
        this.randomGenerator = randomGenerator;
    }

    public int getNumberOfExerciseSeries(IntensityLevel intensityLevel, int maxDurationInSeconds) {
        if (maxDurationInSeconds <= 900) {
            return 1;
        }
        if (intensityLevel.equals(IntensityLevel.LOW)) {
            if (maxDurationInSeconds <= 1800) {
                return 2;
            } else if (maxDurationInSeconds > 1800 && maxDurationInSeconds <= 2700) {
                return 3;
            }
            return ThreadLocalRandom.current().nextInt(3, 5);
        } else {
            if (maxDurationInSeconds <= 1800) {
                return 3;
            } else if (maxDurationInSeconds > 1800 && maxDurationInSeconds <= 2700) {
                return ThreadLocalRandom.current().nextInt(3, 5);
            }
            return ThreadLocalRandom.current().nextInt(5, 8);
        }
    }

    public int getDurationOfOneExerciseSeries(int numberOfExerciseSeries, int maxDurationInMinutes) {
        return maxDurationInMinutes / numberOfExerciseSeries;
    }

    public int getDurationOfOneExerciseRepeatInSeconds(Exercise exercise, int distance, User user) {
        StyleStatistics styleStatistics = user.getStyleStatistics()
                .stream()
                .filter(styleStatistics1 -> exercise.getStyle().equals(styleStatistics1.getStyle()))
                .findFirst().get();
        int userTimeInStyle = styleStatistics.getTimeInSeconds();
        if (distance == 50) {
            return userTimeInStyle / 2;
        } else if (distance == 100) {
            return userTimeInStyle;
        } else {
            double multipleTimeFactor = distance / 100;
            double increasingDurationFactor = 0.4 * multipleTimeFactor * multipleTimeFactor * 1 / 7 * userTimeInStyle;
            Double durationOfOneExerciseSeriesInSeconds = multipleTimeFactor * userTimeInStyle + increasingDurationFactor;
            return durationOfOneExerciseSeriesInSeconds.intValue();
        }
    }

    public int getBreakOfOneExerciseRepeatInSeconds(IntensityLevel intensityLevel, int durationOfOneExerciseRepeatInSeconds) {
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

    public int getNumberOfRepeatsInOneSeries(int durationOfSeriesInSeconds, int durationOfOneRepeat, int durationOfOneBreak) throws UnsatisfiedTimeRequirementsException {
        int numberOfRepeatsInOneSeries = durationOfSeriesInSeconds / (durationOfOneBreak + durationOfOneRepeat);
        if (numberOfRepeatsInOneSeries < 1) {
            throw new UnsatisfiedTimeRequirementsException();
        }

        return numberOfRepeatsInOneSeries;
    }

    public int getRandomDistanceForIntensityLevel(List<Integer> availableDistances) {
        int randomDistanceIndex = randomGenerator.nextInt(availableDistances.size());
        return availableDistances.get(randomDistanceIndex);
    }
}
