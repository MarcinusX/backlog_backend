package com.swimHelper.controller;

import com.swimHelper.exception.BusinessException;
import com.swimHelper.model.IntegerWrapper;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

//    @PreAuthorize("principal.id == #training.user.id")
    @PutMapping
    public Training updateTraining(@RequestBody Training training) throws BusinessException {
        return trainingService.setTrainingCompletion(training);
    }

    @GetMapping
    public IntegerWrapper countDistance(@RequestParam(required = false) Long trainingId,
                                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) throws BusinessException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        IntegerWrapper integerWrapper = new IntegerWrapper();
        integerWrapper.setValue(trainingService.countDistance(user.getId(), trainingId, startDate, endDate));
        return integerWrapper;
    }

    @GetMapping("upcoming")
    public List<Training> getUpcompingTrainings() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return trainingService.getUpcomingTrainings(user.getId());
    }

    @GetMapping("finished")
    public List<Training> getFinishedTrainings() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return trainingService.getFinishedTrainings(user.getId());
    }

    @GetMapping("completed")
    public List<Training> getCompletedTrainings() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return trainingService.getCompletedTrainings(user.getId());
    }

    @GetMapping("uncompleted")
    public List<Training> getUncompletedTrainings() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return trainingService.getUncompletedTrainings(user.getId());
    }
}

