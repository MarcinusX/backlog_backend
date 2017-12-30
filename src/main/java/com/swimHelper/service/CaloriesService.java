package com.swimHelper.service;

import com.swimHelper.component.calories.CaloriesCalculator;
import com.swimHelper.component.calories.CaloriesCalculatorRecursiveTask;
import com.swimHelper.exception.BusinessException;
import com.swimHelper.exception.TooManyParametersException;
import com.swimHelper.exception.TrainingNotFoundException;
import com.swimHelper.exception.UserNotFoundException;
import com.swimHelper.model.ExerciseSeries;
import com.swimHelper.model.Training;
import com.swimHelper.model.User;
import com.swimHelper.repository.TrainingRepository;
import com.swimHelper.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

/**
 * Created by Marcin Szalek on 04.09.17.
 */
@Service
public class CaloriesService {

    private final TrainingRepository trainingRepository;
    private final UserRepository userRepository;
    private final CaloriesCalculator caloriesCalculator;

    @Autowired
    public CaloriesService(TrainingRepository trainingRepository, UserRepository userRepository, CaloriesCalculator caloriesCalculator) {
        this.trainingRepository = trainingRepository;
        this.userRepository = userRepository;
        this.caloriesCalculator = caloriesCalculator;
    }

    public int calculateCalories(Long userId, Long trainingId, LocalDateTime startDate, LocalDateTime endDate) throws BusinessException {
        User user = userRepository.findOne(userId);
        if (user == null) {
            throw new UserNotFoundException("Could not find user with id: " + user.getId());
        }
        if (trainingId != null && (startDate != null || endDate != null)) {
            throw new TooManyParametersException("Too many calories calculator parameters given");
        } else if (trainingId != null) {
            Training training = trainingRepository.findOne(trainingId);
            if (training == null) {
                throw new TrainingNotFoundException("Could not find training with id: " + training.getId());
            } else {
                return calculateCaloriesForTraining(user, trainingId);
            }
        } else if (startDate != null && endDate != null) {
            return calculateCaloriesForPeriod(user, startDate, endDate);
        } else {
            return calculateCaloriesForAllTrainings(user);
        }
    }

    private int calculateCaloriesForAllTrainings(User user) {
        List<Training> trainingList = trainingRepository.findTrainingsByUser(user.getId());
        return getCaloriesCalculationResultFromForkJoin(trainingList, user);
    }

    private int calculateCaloriesForTraining(User user, long trainingId) {
        List<Training> trainingList = Collections.singletonList(trainingRepository.findTrainingByUserAndId(user.getId(), trainingId));
        return getCaloriesCalculationResultFromForkJoin(trainingList, user);
    }

    private int calculateCaloriesForPeriod(User user, LocalDateTime startDate, LocalDateTime endDate) {
        List<Training> trainingList = trainingRepository.findByUserIdAndTrainingDateTimeAfterAndTrainingDateTimeBefore(user.getId(), startDate, endDate);
        return getCaloriesCalculationResultFromForkJoin(trainingList, user);
    }

    private int getCaloriesCalculationResultFromForkJoin(List<Training> trainingList, User user) {
        List<ExerciseSeries> exerciseSeriesList = trainingList.stream()
                .flatMap(t -> t.getExerciseSeries()
                        .stream()).collect(Collectors.toList());
        int calories = ForkJoinPool.commonPool()
                .invoke(new CaloriesCalculatorRecursiveTask(exerciseSeriesList, user.getWeight(), caloriesCalculator));
        return calories;
    }
}
