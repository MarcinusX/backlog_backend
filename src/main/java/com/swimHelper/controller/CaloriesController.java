package com.swimHelper.controller;

import com.swimHelper.service.CaloriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by Marcin Szalek on 04.09.17.
 */
@Controller
@RequestMapping(value = "calories")
public class CaloriesController {

    private final CaloriesService caloriesService;

    @Autowired
    public CaloriesController(CaloriesService caloriesService) {
        this.caloriesService = caloriesService;
    }

    //https://stackoverflow.com/questions/40274353/spring-failed-to-convert-string-to-localdatetime
    @GetMapping
    public int getCaloriesBurned(@RequestParam(value = "trainingId") long trainingId) {
        return 0;
    }
}
