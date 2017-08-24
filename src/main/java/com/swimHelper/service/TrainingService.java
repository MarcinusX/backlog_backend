package com.swimHelper.service;

import com.swimHelper.exception.MissingTrainingRequirementsException;
import com.swimHelper.exception.UnsatisfiedTimeRequirementsException;
import com.swimHelper.exception.UserNotFoundException;
import com.swimHelper.generator.TrainingGenerator;
import com.swimHelper.model.Training;
import com.swimHelper.model.TrainingRequirements;
import com.swimHelper.model.User;
import com.swimHelper.repository.TrainingRepository;
import com.swimHelper.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
