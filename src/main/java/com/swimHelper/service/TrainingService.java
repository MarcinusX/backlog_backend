package com.swimHelper.service;

import com.swimHelper.generator.TrainingGenerator;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by mstobieniecka on 2017-07-19.
 */
public class TrainingService {

    private final TrainingGenerator trainingGenerator;

    @Autowired
    public TrainingService(TrainingGenerator trainingGenerator) {
        this.trainingGenerator = trainingGenerator;
    }
}
