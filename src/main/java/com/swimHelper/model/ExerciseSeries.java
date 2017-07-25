package com.swimHelper.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

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
    @OneToOne
    private Exercise exercise;
    private int distance;
    private int repeats;
    private int orderNumber;
    private int breakInSeconds;
}
