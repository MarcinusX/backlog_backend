package com.swimHelper.controller;

import com.swimHelper.exception.BusinessException;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
                                            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate startDate,
                                            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate endDate) throws BusinessException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        IntegerWrapper integerWrapper = new IntegerWrapper();
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;
        if(startDate != null && endDate != null) {
            startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);
            endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);
        }
        integerWrapper.setValue(caloriesService.calculateCalories(user.getId(), trainingId, startDateTime, endDateTime));
        return integerWrapper;
    }
}
