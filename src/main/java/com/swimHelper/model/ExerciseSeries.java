package com.swimHelper.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

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
    private int breakInSeconds;
    private int durationOfOneExerciseInSeconds;
    private int completedRepeats;
    private int averageDurationOfOneRepeatInSeconds;
    private double completedPercentage = 0;
}
