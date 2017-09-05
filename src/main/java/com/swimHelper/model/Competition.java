package com.swimHelper.model;

import lombok.Data;
import org.springframework.data.geo.Point;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Created by Monia on 2017-07-18.
 */
@Entity
@Data
public class Competition {
    @Id
    @GeneratedValue
    private Long id;
    @Version
    private long version;
    private int participantsCounter;
    private int maxParticipants;
    @ManyToMany
    private Set<User> participants;
    @Column(nullable = false)
    private String name;
    private String description;
    @Column(nullable = false)
    private LocalDateTime dateTime;
    @Column(nullable = false)
    private String locationName;
    @Column(nullable = false)
    private Point location;
    private boolean cancelled;
}
