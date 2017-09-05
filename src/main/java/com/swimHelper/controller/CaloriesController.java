package com.swimHelper.controller;

import com.swimHelper.exception.TooManyParametersException;
import com.swimHelper.exception.TrainingNotFoundException;
import com.swimHelper.exception.UserNotFoundException;
import com.swimHelper.model.IntegerWrapper;
import com.swimHelper.model.User;
import com.swimHelper.service.CaloriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * Created by Marcin Szalek on 04.09.17.
 */
@RestController
@RequestMapping(value = "calories")
public class CaloriesController {

    private final CaloriesService caloriesService;

    @Autowired
    public CaloriesController(CaloriesService caloriesService) {
        this.caloriesService = caloriesService;
    }

    @GetMapping
    public IntegerWrapper getCaloriesBurned(@RequestParam(required = false) Long trainingId,
                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) throws UserNotFoundException, TooManyParametersException, TrainingNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        IntegerWrapper integerWrapper = new IntegerWrapper();
        integerWrapper.setDistance(caloriesService.calculateCalories(user.getId(), trainingId, startDate, endDate));
        return integerWrapper;
    }
}
