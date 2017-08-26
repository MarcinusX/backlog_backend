package com.swimHelper.controller;

import com.swimHelper.exception.BusinessException;
import com.swimHelper.model.Exercise;
import com.swimHelper.service.ExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public Exercise postExercise(@RequestBody Exercise exercise) throws BusinessException {
        return exerciseService.addExercise(exercise);
    }

    @PutMapping
    public Exercise putExercise(@RequestBody Exercise exercise) throws BusinessException {
        return exerciseService.updateExercise(exercise);
    }

    @GetMapping("{id}")
    public Exercise getExercise(@PathVariable Long id) throws BusinessException {
        return exerciseService.getExercise(id);
    }
}
