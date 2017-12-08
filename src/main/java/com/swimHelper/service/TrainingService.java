package com.swimHelper.service;

import com.swimHelper.component.training.DistanceTracker;
import com.swimHelper.exception.*;
import com.swimHelper.generator.TrainingGenerator;
import com.swimHelper.model.ExerciseSeries;
import com.swimHelper.model.Training;
import com.swimHelper.model.TrainingRequirements;
import com.swimHelper.model.User;
import com.swimHelper.repository.TrainingRepository;
import com.swimHelper.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mstobieniecka on 2017-07-19.
 */
@Transactional
@Service
public class TrainingService {

    private final TrainingGenerator trainingGenerator;
    private final UserRepository userRepository;
    private final TrainingRepository trainingRepository;
    private final DistanceTracker distanceTracker;

    @Autowired
    public TrainingService(TrainingGenerator trainingGenerator,
                           UserRepository userRepository,
                           TrainingRepository trainingRepository,
                           DistanceTracker distanceTracker) {
        this.trainingGenerator = trainingGenerator;
        this.userRepository = userRepository;
        this.trainingRepository = trainingRepository;
        this.distanceTracker = distanceTracker;
    }

    public Training generateTraining(TrainingRequirements trainingRequirements, Long userId) throws BusinessException {
        User user = userRepository.findOne(userId);
        if (user == null) {
            throw new UserNotFoundException("Could not find user with id: " + user.getId());
        }
        Training training = trainingGenerator.generateTraining(user, trainingRequirements);
        return trainingRepository.saveAndFlush(training);
    }

    public List<Training> getTrainingsToBeNotified() {
        return trainingRepository.findTrainingsToBeNotified(LocalDateTime.now());
    }

    public Training setUserNotified(Long trainingId) throws BusinessException {
        Training trainingFromDb = getTrainingForUpdate(trainingId);
        trainingFromDb.setHasUserBeenNotified(true);
        return trainingRepository.saveAndFlush(trainingFromDb);
    }

    public int countDistance(Long userId, Long trainingId, LocalDateTime startDate, LocalDateTime endDate) throws BusinessException {
        return distanceTracker.countDistance(userId, trainingId, startDate, endDate);
    }

    public Training setTrainingCompletion(Training training) throws BusinessException {
        if (training == null || training.getId() == null) {
            throw new InvalidTrainingException("Invalid training");
        }
        Training trainingFromDb = getTrainingForUpdate(training.getId());
        updateTrainingSeries(training, trainingFromDb);
        try {
            return trainingRepository.saveAndFlush(trainingFromDb);
        } catch (ConstraintViolationException e) {
            throw new InvalidTrainingException(e.getMessage());
        }
    }

    public List<Training> getUpcomingTrainings(Long id) {
        List<Training> trainings = trainingRepository.findTrainingsByUser(id);
        return trainings.stream().filter(t -> t.getTrainingDateTime().isAfter(LocalDateTime.now())).collect(Collectors.toList());
    }

    public List<Training> getFinishedTrainings(Long id) {
        List<Training> trainings = trainingRepository.findTrainingsByUser(id);
        return trainings.stream().filter(t ->
                t.getTrainingDateTime().isBefore(LocalDateTime.now())).collect(Collectors.toList());
    }

    public List<Training> getCompletedTrainings(Long id) {
        List<Training> trainings = trainingRepository.findTrainingsByUser(id);
        return trainings.stream().filter(t ->
            t.getTrainingDateTime().isBefore(LocalDateTime.now()) && countCompletedDistance(t) > 0
        ).collect(Collectors.toList());
    }

    public List<Training> getUncompletedTrainings(Long id) {
        List<Training> trainings = trainingRepository.findTrainingsByUser(id);
        return trainings.stream().filter(t ->
                t.getTrainingDateTime().isBefore(LocalDateTime.now()) && countCompletedDistance(t) == 0
        ).collect(Collectors.toList());
    }

    public Training getTraining(Long userId, Long trainingId) throws ForbiddenAccessException {
        Training training = trainingRepository.findOne(trainingId);
        if(userId.equals(training.getUser().getId())) {
            return training;
        } else {
            throw  new ForbiddenAccessException();
        }
    }

    private int countCompletedDistance(Training training) {
        int percentage = 0;
        for (ExerciseSeries exerciseSeries : training.getExerciseSeries()) {
            percentage += exerciseSeries.getCompletedPercentage();
        }
        return percentage;
    }
    private void updateTrainingSeries(Training training, Training trainingFromDb) {
        List<ExerciseSeries> existingExerciseSeries = new ArrayList<>(trainingFromDb.getExerciseSeries());
        List<ExerciseSeries> exerciseSeriesToUpdate = new ArrayList<>(training.getExerciseSeries());
        exerciseSeriesToUpdate.forEach(series -> {
            existingExerciseSeries.stream().filter(es -> series.getId().equals(es.getId()))
                    .findFirst()
                    .ifPresent(es -> {
                                es.setAverageDurationOfOneRepeatInSeconds(series.getAverageDurationOfOneRepeatInSeconds());
                                es.setCompletedRepeats(series.getCompletedRepeats());
                                es.setCompletedPercentage(series.getCompletedRepeats() / es.getRepeats());
                            }
                    );
        });
        training.setCompletedPercentage(countCompletedPercentageForTraining(training));
    }

    private double countCompletedPercentageForTraining (Training training) {
        int completedRepeats = 0;
        int allRepeats = 0;
        for(ExerciseSeries series: training.getExerciseSeries()) {
            completedRepeats += series.getCompletedRepeats();
            allRepeats += series.getRepeats();
        }

        return completedRepeats * 100.0 / allRepeats;
    }

    private Training getTrainingForUpdate(Long trainingId) throws BusinessException {
        if (trainingId == null) {
            throw new InvalidTrainingException("Invalid training");
        }
        Training trainingFromDb = trainingRepository.findOne(trainingId);
        if (trainingFromDb == null) {
            throw new TrainingNotFoundException("Could not find training with id: " + trainingId);
        }
        return trainingFromDb;
    }
}
