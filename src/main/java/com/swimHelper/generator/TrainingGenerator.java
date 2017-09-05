package com.swimHelper.generator;

import com.swimHelper.exception.MissingTrainingRequirementsException;
import com.swimHelper.exception.UnsatisfiedTimeRequirementsException;
import com.swimHelper.model.*;
import com.swimHelper.repository.ExerciseRepository;
import com.swimHelper.util.RandomGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mstobieniecka on 2017-07-19.
 */
@Component
public class TrainingGenerator {

    private final ExerciseRepository exerciseRepository;
    private final TrainingCalculator trainingCalculator;
    private final RandomGenerator randomGenerator;

    @Autowired
    public TrainingGenerator(ExerciseRepository exerciseRepository, TrainingCalculator trainingCalculator, RandomGenerator randomGenerator) {
        this.exerciseRepository = exerciseRepository;
        this.trainingCalculator = trainingCalculator;
        this.randomGenerator = randomGenerator;
    }

    public Training generateTraining(User user, TrainingRequirements trainingRequirements) throws MissingTrainingRequirementsException, UnsatisfiedTimeRequirementsException {
        if (!areTrainingRequirementsGiven(user, trainingRequirements)) {
            throw new MissingTrainingRequirementsException();
        }
        List<Exercise> matchingExercises = getMatchingExercises(trainingRequirements);
        //create training
        Training training = new Training();
        training.setTrainingDateTime(trainingRequirements.getTrainingDateTime());
        //add warm up
        addWarmUpExerciseSeries(training);
        //add exercises
        int numberOfExerciseSeries = trainingCalculator
                .getNumberOfExerciseSeries(trainingRequirements.getIntensityLevel(), trainingRequirements.getMaxDurationInSeconds());
        numberOfExerciseSeries = Math.min(matchingExercises.size(), numberOfExerciseSeries);
        if (numberOfExerciseSeries != 0) {
            addExercises(user, trainingRequirements, training, numberOfExerciseSeries, matchingExercises);
        }
        //add relax
        addRelaxExerciseSeries(training);
        //adjust to max distance
        if (trainingRequirements.getMaxDistance() != 0) {
            training = getAdaptedTrainingToMaxDistance(training, trainingRequirements.getMaxDistance());
        }
        training.setDurationInSeconds(getDurationOfTraining(training));
        training.setUser(user);
        return training;
    }

    private int getDurationOfTraining(Training training) {
        int durationOfTraining = 900;
        for (ExerciseSeries exerciseSeries : training.getExerciseSeries()) {
            durationOfTraining += (exerciseSeries.getDurationOfOneExerciseInSeconds() + exerciseSeries.getBreakInSeconds()) * exerciseSeries.getRepeats();
        }
        return durationOfTraining;
    }

    private void addExercises(User user, TrainingRequirements trainingRequirements,
                              Training training, int numberOfExerciseSeries, List<Exercise> matchingExercises)
            throws UnsatisfiedTimeRequirementsException {
        int durationOfOneExerciseSeriesInSeconds = trainingCalculator.getDurationOfOneExerciseSeries(numberOfExerciseSeries, trainingRequirements.getMaxDurationInSeconds() - 900);
        for (int i = 0; i < numberOfExerciseSeries; i++) {
            Exercise exercise = matchingExercises.get(randomGenerator.generateRandomInt(matchingExercises.size()));
            addExerciseSeries(training, exercise, trainingRequirements, user, durationOfOneExerciseSeriesInSeconds);
            matchingExercises.remove(exercise);
        }
    }

    private List<Exercise> getMatchingExercises(TrainingRequirements trainingRequirements) {
        return trainingRequirements.getStyles().stream()
                .flatMap(style -> exerciseRepository.findByStyle(style).stream())
                .filter(e -> !e.isWarmUpRelax()).collect(Collectors.toList());
    }

    private void addWarmUpExerciseSeries(Training training) {
        ExerciseSeries exerciseSeries = new ExerciseSeries();
        exerciseSeries.setRepeats(1);
        exerciseSeries.setDistance(200);
        List<Exercise> exerciseList = exerciseRepository.findByWarmUpRelax(true);
        Exercise exercise = exerciseList.get(randomGenerator.generateRandomInt(exerciseList.size()));
        exerciseSeries.setExercise(exercise);
        training.getExerciseSeries().add(exerciseSeries);
    }

    private void addRelaxExerciseSeries(Training training) {
        ExerciseSeries exerciseSeries = new ExerciseSeries();
        exerciseSeries.setRepeats(1);
        exerciseSeries.setDistance(100);
        List<Exercise> exerciseList = exerciseRepository.findByWarmUpRelax(true);
        Exercise exercise = exerciseList.get(randomGenerator.generateRandomInt(exerciseList.size()));
        exerciseSeries.setExercise(exercise);
        training.getExerciseSeries().add(exerciseSeries);
    }

    private boolean areTrainingRequirementsGiven(User user, TrainingRequirements trainingRequirements) {
        Collection<Style> userStylesFromStatistics = user.getStyleStatistics().stream().map(StyleStatistics::getStyle).collect(Collectors.toList());
        boolean doesUserChoseStyles = !trainingRequirements.getStyles().isEmpty();
        boolean doesUserHaveStatisticsForChosenStyles = false;
        LocalDateTime trainingDateTime = trainingRequirements.getTrainingDateTime();
        boolean doesUserChoseTrainingDateTime = trainingDateTime != null && trainingDateTime.isAfter(LocalDateTime.now());
        if (doesUserChoseStyles) {
            doesUserHaveStatisticsForChosenStyles = trainingRequirements.getStyles().stream().allMatch(userStylesFromStatistics::contains);
        }
        boolean isMaxDurationOrMaxDistanceSet = (trainingRequirements.getMaxDistance() > 0 || trainingRequirements.getMaxDurationInSeconds() > 0);
        boolean isIntensityLevelSet = trainingRequirements.getIntensityLevel() != null;
        return (doesUserHaveStatisticsForChosenStyles && doesUserChoseStyles && isMaxDurationOrMaxDistanceSet
                && isIntensityLevelSet && doesUserChoseTrainingDateTime);
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
        int timeOfRepeatAndBreak = durationOfOneRepeatInSeconds + breakInSeconds;
        if (timeOfRepeatAndBreak == 0) {
            exerciseSeries.setRepeats(0);
        } else {
            exerciseSeries.setRepeats(trainingCalculator.getNumberOfRepeatsInOneSeries(durationOfOneSeries, timeOfRepeatAndBreak));
        }
        return exerciseSeries;
    }

    private void addExerciseSeries(Training training, Exercise exercise, TrainingRequirements trainingRequirements, User user, int durationOfOneSeries) throws UnsatisfiedTimeRequirementsException {
        ExerciseSeries exerciseSeries = createExerciseSeries(exercise, trainingRequirements, user, durationOfOneSeries);
        if (exerciseSeries.getRepeats() > 0) {
            training.getExerciseSeries().add(exerciseSeries);
        }
    }

    Training getAdaptedTrainingToMaxDistance(Training training, int maxDistance) {
        if (trainingCalculator.calculateDistanceOfTraining(training.getExerciseSeries()) > maxDistance) {
            Iterator<ExerciseSeries> iterator = training.getExerciseSeries().iterator();
            List<ExerciseSeries> exerciseSeries = new ArrayList<>(training.getExerciseSeries());
            boolean isTrainingDistanceLongerThanMaxDistance = true;
            while (isTrainingDistanceLongerThanMaxDistance) {

                ExerciseSeries series = iterator.next();
                series.setRepeats(series.getRepeats() - 1);
                isTrainingDistanceLongerThanMaxDistance = trainingCalculator.calculateDistanceOfTraining(exerciseSeries) > maxDistance;

                if (!iterator.hasNext()) {
                    iterator = training.getExerciseSeries().iterator();
                }
            }

            exerciseSeries.removeIf(item -> item.getRepeats() == 0);
            training.setExerciseSeries(exerciseSeries);
        }
        return training;
    }
}
