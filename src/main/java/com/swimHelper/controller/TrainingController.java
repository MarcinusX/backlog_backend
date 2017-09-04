package com.swimHelper.controller;

import com.swimHelper.exception.BusinessException;
import com.swimHelper.exception.InvalidTrainingException;
import com.swimHelper.exception.TrainingNotFoundException;
import com.swimHelper.model.Training;
import com.swimHelper.model.TrainingRequirements;
import com.swimHelper.model.User;
import com.swimHelper.service.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Created by mstobieniecka on 2017-07-19.
 */
@RestController
@RequestMapping("trainings")
public class TrainingController {

    private final TrainingService trainingService;

    @Autowired
    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @PostMapping
    public Training generateTraining(@RequestBody TrainingRequirements trainingRequirements) throws BusinessException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Training training = trainingService.generateTraining(trainingRequirements, user.getId());
        return training;
    }

    @PreAuthorize("principal.id == #training.user.id")
    @PutMapping
    public Training updateTraining(@RequestBody Training training) throws TrainingNotFoundException, InvalidTrainingException {
        return trainingService.setTrainingCompletion(training);
    }
}

