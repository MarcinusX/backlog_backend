package com.swimHelper.service;

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

    @Autowired
    public TrainingService(TrainingGenerator trainingGenerator, UserRepository userRepository, TrainingRepository trainingRepository) {
        this.trainingGenerator = trainingGenerator;
        this.userRepository = userRepository;
        this.trainingRepository = trainingRepository;
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

    public Training setTrainingCompletion(Training training) throws TrainingNotFoundException, InvalidTrainingException {
        Training trainingFromDb = trainingRepository.findOne(training.getId());
        if (trainingFromDb != null) {
            updateTrainingSeries(training, trainingFromDb);
            try {
                return trainingRepository.save(trainingFromDb);
            } catch (ConstraintViolationException e) {
                throw new InvalidTrainingException(e.getMessage());
            }
        } else {
            throw new TrainingNotFoundException("Could not find user with id: " + training.getId());
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
}
