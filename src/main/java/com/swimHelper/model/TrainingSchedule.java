package com.swimHelper.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.util.Collection;

/**
 * Created by Monia on 2017-07-18.
 */
@Entity
@Data
public class TrainingSchedule {
    @Id
    @GeneratedValue
    private Long id;
    @OneToMany
    private Collection<Training> trainings;
    private LocalDate startDate;
    private LocalDate endDate;
}
