package com.swimHelper.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Collection;

/**
 * Created by Monia on 2017-07-18.
 */
@Entity
@Data
public class Training {
    @Id
    @GeneratedValue
    private Long id;
    private Collection<ExerciseSeries> exerciseSeries;
    private LocalDateTime dateTime;
    private int durationInMinutes;
    private User user;
}
