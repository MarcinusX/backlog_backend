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

    public Training generateTraining(TrainingRequirements trainingRequirements, Long userId) throws UnsatisfiedTimeRequirementsException, MissingTrainingRequirementsException, UserNotFoundException {
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

    //TODO: ADD TESTS
    public Training updateTraining(Training training) {
        if (trainingRepository.findOne(training.getId()) == null) {
            //TODO: throw Exception
        }
        return trainingRepository.saveAndFlush(training);
    }

    public Integer countDistance(Long userId, Long trainingId, LocalDateTime startDate, LocalDateTime endDate) throws UserNotFoundException, TooManyDistanceTrackerArgumentsException, TrainingNotFoundException {
        return distanceTracker.countDistance(userId, trainingId, startDate, endDate);
    }

    public Training setTrainingCompletion(Training training) throws TrainingNotFoundException, InvalidTrainingException {
        Training trainingFromDb = getTrainingForUpdate(training);
        updateTrainingSeries(training, trainingFromDb);
        try {
            return trainingRepository.saveAndFlush(trainingFromDb);
        } catch (ConstraintViolationException e) {
            throw new InvalidTrainingException(e.getMessage());
        }
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
                            }
                    );
        });
    }

    private Training getTrainingForUpdate(Training training) throws InvalidTrainingException, TrainingNotFoundException {
        if (training == null || training.getId() == null) {
            throw new InvalidTrainingException("Invalid training");
        }
        Training trainingFromDb = trainingRepository.findOne(training.getId());
        if (trainingFromDb == null) {
            throw new TrainingNotFoundException("Could not find training with id: " + training.getId());
        }
        return trainingFromDb;
    }
}
