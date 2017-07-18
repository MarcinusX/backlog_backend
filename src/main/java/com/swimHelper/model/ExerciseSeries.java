package com.swimHelper.model;

import lombok.Data;

import javax.persistence.Entity;

/**
 * Created by Monia on 2017-07-18.
 */
@Entity
@Data
public class ExerciseSeries {
    private Exercise exercise;
    private int distance;
    private int repeats;
    private int orderNumber;
    private int breakInSeconds;
}
