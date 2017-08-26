package com.swimHelper.service;

import com.swimHelper.exception.BusinessException;
import com.swimHelper.exception.ExerciseExistsException;
import com.swimHelper.exception.ExerciseNotFoundException;
import com.swimHelper.exception.InvalidExerciseException;
import com.swimHelper.model.Exercise;
import com.swimHelper.repository.ExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolationException;

/**
 * Created by mstobieniecka on 2017-08-26.
 */
@Service
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;

    private final static String NOT_FOUND_EXERCISE_MESSAGE = "Exercise with given id doesn't exist";
    private final static String EXERCISE_EXISTS_MESSAGE = "Exercise with that name already exists";

    @Autowired
    public ExerciseService(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    public Exercise addExercise(Exercise exercise) throws BusinessException {
        try {
            return exerciseRepository.saveAndFlush(exercise);
        } catch (ConstraintViolationException ex) {
            throw new InvalidExerciseException(ex.getMessage());
        } catch (DataIntegrityViolationException ex) {
            throw new ExerciseExistsException(EXERCISE_EXISTS_MESSAGE);
        }
    }

    public Exercise updateExercise(Exercise exercise) throws BusinessException {
        Exercise exerciseFromRepo = exerciseRepository.findOne(exercise.getId());
        if (exerciseFromRepo == null) {
            throw new ExerciseNotFoundException(NOT_FOUND_EXERCISE_MESSAGE);
        }
        exerciseFromRepo.setName(exercise.getName());
        exerciseFromRepo.setWarmUpRelax(exercise.isWarmUpRelax());
        exerciseFromRepo.setStyle(exercise.getStyle());
        exerciseFromRepo.setDescription(exercise.getDescription());
        exerciseFromRepo.setVideoUrl(exercise.getVideoUrl());
        try {
            return exerciseRepository.saveAndFlush(exerciseFromRepo);
        } catch (ConstraintViolationException ex) {
            throw new InvalidExerciseException(ex.getMessage());
        } catch (DataIntegrityViolationException ex) {
            throw new ExerciseExistsException(EXERCISE_EXISTS_MESSAGE);
        }
    }

    public Exercise getExercise(Long id) throws ExerciseNotFoundException {
        Exercise exercise = exerciseRepository.findOne(id);
        if (exercise == null) {
            throw new ExerciseNotFoundException(NOT_FOUND_EXERCISE_MESSAGE);
        }
        return exercise;
    }
}
