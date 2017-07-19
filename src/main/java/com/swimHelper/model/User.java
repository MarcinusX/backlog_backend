package com.swimHelper.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;

/**
 * Created by marcinus on 19.04.17.
 */
@Entity
@Data
public class User {

    @GeneratedValue
    @Id
    private Long id;

    @NotNull
    private String email;

    private String firstname;

    private String lastname;

    @NotNull
    @Size(min = 5, max = 32)
    private String password;

    private double weight;

    private Collection<Training> trainings;

    private Collection<Record> records;

    private Collection<StyleStatistics> styleStatistics;
}