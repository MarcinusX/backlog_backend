package com.swimHelper.model;

import lombok.Data;

import javax.persistence.Entity;
import java.time.LocalDateTime;

/**
 * Created by Monia on 2017-07-18.
 */
@Entity
@Data
public class Competition {
    private String name;
    private String location;
    private LocalDateTime dateTime;
}
