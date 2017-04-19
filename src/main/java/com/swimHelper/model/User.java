package com.swimHelper.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by marcinus on 19.04.17.
 */
@Entity
@Data
@RequiredArgsConstructor
public class User {
    @Column
    @NotNull
    private String email;
    @Column
    @NotNull
    @Size(min = 5, max = 32)
    private String password;
    @GeneratedValue
    @Id
    private Long id;

}
