package com.swimHelper.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by marcinus on 19.04.17.
 */
@Entity
@Data
@NoArgsConstructor //jpa
public class User {

    @GeneratedValue
    @Id
    private Long id;
    @NotNull
    private String email;
    private String firstname;
    private String lastname;
    @NotNull
    @Size(min = 5, max = 256)
    private String password;

    private double weight;

    @OneToMany(mappedBy = "user")
    private Collection<Training> trainings = new ArrayList<>();

    @OneToMany
    private Collection<Record> records = new ArrayList<>();

    @OneToMany
    @Cascade(CascadeType.ALL)
    private Collection<StyleStatistics> styleStatistics = new ArrayList<>();

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
