package com.swimHelper.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by mstobieniecka on 2017-07-19.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor //jpa
public class TrainingRequirements {
    private Collection<Style> styles = new ArrayList<>();
    private IntensityLevel intensityLevel;
    private int maxDurationInSeconds;
    private int maxDistance;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime trainingDateTime;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime notificationDateTime;
    private Collection<TrainingEquipment> availableTrainingEquipment = new ArrayList<>();
}
