package com.swimHelper.component.training;

import com.swimHelper.exception.BusinessException;
import com.swimHelper.exception.TooManyParametersException;
import com.swimHelper.exception.TrainingNotFoundException;
import com.swimHelper.exception.UserNotFoundException;
import com.swimHelper.model.Training;
import com.swimHelper.model.User;
import com.swimHelper.repository.TrainingRepository;
import com.swimHelper.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by mstobieniecka on 2017-09-04.
 */
@Component
public class DistanceTracker {

    private final UserRepository userRepository;
    private final TrainingRepository trainingRepository;

    public DistanceTracker(UserRepository userRepository, TrainingRepository trainingRepository) {
        this.userRepository = userRepository;
        this.trainingRepository = trainingRepository;
    }

    public int countDistance(Long userId, Long trainingId, LocalDateTime startDate, LocalDateTime endDate) throws BusinessException {
        User user = userRepository.findOne(userId);
        if (user == null) {
            throw new UserNotFoundException("Could not find user with id: " + user.getId());
        }
        if (trainingId != null && (startDate != null || endDate != null)) {
            throw new TooManyParametersException("Too many distance tracker parameters given");
        } else if (trainingId != null) {
            Training training = trainingRepository.findOne(trainingId);
            if (training == null) {
                throw new TrainingNotFoundException("Could not find training with id: " + training.getId());
            } else {
                return countDistanceByTrainingId(user, trainingId);
            }
        } else if (startDate != null && endDate != null) {
            return countDistanceByDates(user, startDate, endDate);
        } else {
            return countDistanceByUser(user);
        }
    }

    private int countDistanceByDates(User user, LocalDateTime startDate, LocalDateTime endDate) {
        List<Training> trainings = trainingRepository.findTrainingsByUserAndDates(user.getId(), startDate, endDate);
        return trainings.stream().mapToInt(this::countDistanceForOneTraining).sum();
    }

    private int countDistanceByTrainingId(User user, Long trainingId) {
        Training training = trainingRepository.findTrainingByUserAndId(user.getId(), trainingId);
        return countDistanceForOneTraining(training);
    }

    private int countDistanceByUser(User user) {
        List<Training> trainings = trainingRepository.findTrainingsByUser(user.getId());
        return trainings.stream().mapToInt(this::countDistanceForOneTraining).sum();
    }

    private int countDistanceForOneTraining(Training training) {
        return training.getExerciseSeries()
                .stream().mapToInt(es -> es.getCompletedRepeats() * es.getDistance())
                .sum();
    }
}
