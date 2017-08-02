package com.swimHelper.generator;

import com.swimHelper.exception.MissingTrainingRequirementsException;
import com.swimHelper.exception.UnsatisfiedTimeRequirementsException;
import com.swimHelper.model.*;
import com.swimHelper.repository.ExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by mstobieniecka on 2017-07-19.
 */
@Component
public class TrainingGenerator {

    private final ExerciseRepository exerciseRepository;
    private final TrainingCalculator trainingCalculator;
    private final Random randomGenerator;

    @Autowired
    public TrainingGenerator(ExerciseRepository exerciseRepository, TrainingCalculator trainingCalculator, Random randomGenerator) {
        this.exerciseRepository = exerciseRepository;
        this.trainingCalculator = trainingCalculator;
        this.randomGenerator = randomGenerator;
    }

    public Training generateTraining(User user, TrainingRequirements trainingRequirements) throws MissingTrainingRequirementsException, UnsatisfiedTimeRequirementsException {
        if (!areTrainingRequirementsGiven(user, trainingRequirements)) {
            throw new MissingTrainingRequirementsException();
        }
        List<Exercise> exercisesByStyles = new ArrayList<>();
        trainingRequirements.getStyles().forEach(style -> exercisesByStyles.addAll(exerciseRepository.findByStyle(style)));
        Training training = new Training();
        int numberOfExerciseSeries = trainingCalculator.getNumberOfExerciseSeries(trainingRequirements.getIntensityLevel(), trainingRequirements.getMaxDurationInSeconds());
        int durationOfOneExerciseSeriesInSeconds = trainingCalculator.getDurationOfOneExerciseSeries(numberOfExerciseSeries, trainingRequirements.getMaxDurationInSeconds());
        for (int i = 0; i < numberOfExerciseSeries; i++) {
            Exercise exercise = exercisesByStyles.get(randomGenerator.nextInt(exercisesByStyles.size()));
            addExerciseSeries(training, exercise, trainingRequirements, user, durationOfOneExerciseSeriesInSeconds);
            exercisesByStyles.remove(exercise);
        }

        return training;
    }

    private boolean areTrainingRequirementsGiven(User user, TrainingRequirements trainingRequirements) {
        Collection<Style> userStylesFromStatistics =
                user.getStyleStatistics().stream().map(StyleStatistics::getStyle).collect(Collectors.toList());
        boolean doesUserHaveStatisticsForChosenStyles = trainingRequirements.getStyles().stream().allMatch(userStylesFromStatistics::contains);
        boolean doesUserChoseStyles = !trainingRequirements.getStyles().isEmpty();
        boolean isDifficultyLevelSet = trainingRequirements.getDifficultyLevel() != null;
        boolean isMaxDurationOrMaxDistanceSet = (trainingRequirements.getMaxDistance() > 0 || trainingRequirements.getMaxDurationInSeconds() > 0);
        boolean isIntensityLevelSet = trainingRequirements.getIntensityLevel() != null;
        return (doesUserHaveStatisticsForChosenStyles && doesUserChoseStyles && isDifficultyLevelSet && isMaxDurationOrMaxDistanceSet && isIntensityLevelSet);
    }

    private ExerciseSeries createExerciseSeries(Exercise exercise, TrainingRequirements trainingRequirements, User user, int durationOfOneSeries) throws UnsatisfiedTimeRequirementsException {
        ExerciseSeries exerciseSeries = new ExerciseSeries();
        int distance = trainingCalculator.getRandomDistanceForIntensityLevel(trainingRequirements.getIntensityLevel().getDistances());
        exerciseSeries.setDistance(distance);
        int durationOfOneRepeatInSeconds = trainingCalculator.getDurationOfOneExerciseRepeatInSeconds(exercise, distance, user);
        exerciseSeries.setDurationOfOneExerciseInSeconds(durationOfOneRepeatInSeconds);
        exerciseSeries.setExercise(exercise);
        int breakInSeconds = trainingCalculator.getBreakOfOneExerciseRepeatInSeconds(trainingRequirements.getIntensityLevel(), durationOfOneRepeatInSeconds);
        exerciseSeries.setBreakInSeconds(breakInSeconds);
        exerciseSeries.setRepeats(trainingCalculator.getNumberOfRepeatsInOneSeries(durationOfOneSeries, durationOfOneRepeatInSeconds, breakInSeconds));
        return exerciseSeries;
    }

    private void addExerciseSeries(Training training, Exercise exercise, TrainingRequirements trainingRequirements, User user, int durationOfOneSeries) throws UnsatisfiedTimeRequirementsException {
        training.getExerciseSeries().add(createExerciseSeries(exercise, trainingRequirements, user, durationOfOneSeries));
    }
}
