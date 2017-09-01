package com.swimHelper.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Created by Monia on 2017-07-18.
 */
@Entity
@Data
@RequiredArgsConstructor
public class ExerciseSeries {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private Exercise exercise;
    private int distance;
    private int repeats;
    private int orderNumber;
    private int breakInSeconds;
    private int durationOfOneExerciseInSeconds;
    private int completedRepeats;
    private int averageDurationOfOneRepeatInSeconds;
}
