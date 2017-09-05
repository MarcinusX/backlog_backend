package com.swimHelper.controller;

import com.swimHelper.exception.*;
import com.swimHelper.model.DistanceTrackerResult;
import com.swimHelper.model.Training;
import com.swimHelper.model.TrainingRequirements;
import com.swimHelper.model.User;
import com.swimHelper.service.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

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

    @GetMapping
    public DistanceTrackerResult countDistance(@RequestParam(required = false) Long trainingId,
                                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) throws UserNotFoundException, TooManyDistanceTrackerArgumentsException, TrainingNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        DistanceTrackerResult distanceTrackerResult = new DistanceTrackerResult();
        distanceTrackerResult.setDistance(trainingService.countDistance(user.getId(), trainingId, startDate, endDate));
        return distanceTrackerResult;
    }
}

