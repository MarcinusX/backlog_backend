package com.swimHelper.controller;

import com.swimHelper.exception.BusinessException;
import com.swimHelper.model.Exercise;
import com.swimHelper.model.Role;
import com.swimHelper.service.ExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;

/**
 * Created by mstobieniecka on 2017-08-26.
 */
@RestController
@RequestMapping("exercises")
public class ExerciseController {
    private final ExerciseService exerciseService;

    @Autowired
    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @PostMapping
    @RolesAllowed({Role.NAME_ADMIN})
    public Exercise postExercise(@RequestBody Exercise exercise) throws BusinessException {
        return exerciseService.addExercise(exercise);
    }

    @PutMapping
    @RolesAllowed({Role.NAME_ADMIN})
    public Exercise putExercise(@RequestBody Exercise exercise) throws BusinessException {
        return exerciseService.updateExercise(exercise);
    }

    @GetMapping("{id}")
    @RolesAllowed({Role.NAME_ADMIN})
    public Exercise getExercise(@PathVariable Long id) throws BusinessException {
        return exerciseService.getExercise(id);
    }
}
