package com.swimHelper.service;

import com.swimHelper.generator.TrainingGenerator;
import com.swimHelper.model.Training;
import com.swimHelper.repository.TrainingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by mstobieniecka on 2017-07-19.
 */
@Service
public class TrainingService {

    private final TrainingGenerator trainingGenerator;
    private final TrainingRepository trainingRepository;

    @Autowired
    public TrainingService(TrainingGenerator trainingGenerator, TrainingRepository trainingRepository) {
        this.trainingGenerator = trainingGenerator;
        this.trainingRepository = trainingRepository;
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
}
