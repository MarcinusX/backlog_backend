package com.swimHelper.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
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
public class User implements UserDetails {

    @GeneratedValue
    @Id
    private Long id;
    @NotNull
    @Column(unique = true)
    private String email;
    private String firstname;
    private String lastname;
    private double weight;
    @NotNull
    @Size(min = 5, max = 256)
    private String password;

    private boolean credentialsNonExpired = true;
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean enabled = true;

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

    //
    // ===== UserDetails interface =====
    //

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;//TODO
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
