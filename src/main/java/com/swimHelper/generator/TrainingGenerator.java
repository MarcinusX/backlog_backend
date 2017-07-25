package com.swimHelper.generator;

import com.swimHelper.exception.MissingTrainingRequirementsException;
import com.swimHelper.model.*;
import com.swimHelper.repository.ExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Created by mstobieniecka on 2017-07-19.
 */
@Component
public class TrainingGenerator {

    private final ExerciseRepository exerciseRepository;
    private final Random randomGenerator;

    @Autowired
    public TrainingGenerator(ExerciseRepository exerciseRepository, Random randomGenerator) {
        this.exerciseRepository = exerciseRepository;
        this.randomGenerator = randomGenerator;
    }

    public Training generateTraining(User user, TrainingRequirements trainingRequirements) throws MissingTrainingRequirementsException {
        if (!areTrainingRequirementsGiven(user, trainingRequirements)) {
            throw new MissingTrainingRequirementsException();
        }
        List<Exercise> exercisesByStyles = new ArrayList<>();
        trainingRequirements.getStyles().forEach(style -> exercisesByStyles.addAll(exerciseRepository.findByStyle(style)));
        Training training = new Training();

        exercisesByStyles.forEach(exercise -> addExerciseSeries(training, exercise, trainingRequirements));

        return training;
    }

    private boolean areTrainingRequirementsGiven(User user, TrainingRequirements trainingRequirements) {
        Collection<Style> userStylesFromStatistics =
                user.getStyleStatistics().stream().map(StyleStatistics::getStyle).collect(Collectors.toList());
        boolean doesUserHaveStatisticsForChosenStyles = trainingRequirements.getStyles().stream().allMatch(userStylesFromStatistics::contains);
        boolean doesUserChoseStyles = !trainingRequirements.getStyles().isEmpty();
        boolean isDifficultyLevelSet = trainingRequirements.getDifficultyLevel() != null;
        boolean isMaxDurationOrMaxDistanceSet = (trainingRequirements.getMaxDistance() > 0 || trainingRequirements.getMaxDurationInMinutes() > 0);
        boolean isIntensityLevelSet = trainingRequirements.getIntensityLevel() != null;
        return (doesUserHaveStatisticsForChosenStyles && doesUserChoseStyles && isDifficultyLevelSet && isMaxDurationOrMaxDistanceSet && isIntensityLevelSet);
    }

    private int getRandomDistanceForIntensityLevel(List<Integer> availableDistances) {
        int randomDistanceIndex = randomGenerator.nextInt(availableDistances.size());
        return availableDistances.get(randomDistanceIndex);
    }

    private ExerciseSeries createExerciseSeries(Exercise exercise, List<Integer> availableDistances) {
        ExerciseSeries exerciseSeries = new ExerciseSeries();
        exerciseSeries.setDistance(getRandomDistanceForIntensityLevel(availableDistances));
        exerciseSeries.setExercise(exercise);
        return exerciseSeries;
    }

    private void addExerciseSeries(Training training, Exercise exercise, TrainingRequirements trainingRequirements) {
        training.getExerciseSeries().add(createExerciseSeries(exercise, trainingRequirements.getIntensityLevel().getDistances()));
    }

    private void createTrainingWithGivenTime(User user, TrainingRequirements trainingRequirements) {

    }

    public int getNumberOfExerciseSeries(IntensityLevel intensityLevel, int maxDurationInMinutes) {
        if (maxDurationInMinutes <= 15) {
            return 1;
        }
        if (intensityLevel.equals(IntensityLevel.LOW)) {
            if (maxDurationInMinutes <= 30) {
                return 2;
            } else if (maxDurationInMinutes > 30 && maxDurationInMinutes <= 45) {
                return 3;
            }
            return ThreadLocalRandom.current().nextInt(3, 5);
        } else {
            if (maxDurationInMinutes <= 30) {
                return 3;
            } else if (maxDurationInMinutes > 30 && maxDurationInMinutes <= 45) {
                return ThreadLocalRandom.current().nextInt(3, 5);
            }
            return ThreadLocalRandom.current().nextInt(5, 8);
        }
    }

    public int getDurationOfOneExerciseSeries(int numberOfExerciseSeries, int maxDurationInMinutes) {
        return maxDurationInMinutes / numberOfExerciseSeries;
    }
}
