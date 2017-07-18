package com.swimHelper.model;

import lombok.Data;
import javax.persistence.Entity;
import java.time.LocalDate;
import java.util.Collection;

/**
 * Created by Monia on 2017-07-18.
 */
@Entity
@Data
public class TrainingSchedule {
    private Collection<Training> trainings;
    private LocalDate startDate;
    private LocalDate endDate;
}
