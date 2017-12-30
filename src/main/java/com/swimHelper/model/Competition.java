package com.swimHelper.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.data.geo.Point;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Monia on 2017-07-18.
 */
@Entity
@Data
@NamedEntityGraph(name = "graph.competition.participants",
        attributeNodes = @NamedAttributeNode("participants"))
public class Competition {
    @Id
    @GeneratedValue
    private Long id;
    @Version
    private long version;
    private int participantsCounter;
    private int maxParticipants;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "competitions_participants",
            joinColumns = @JoinColumn(name = "competition_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    @JsonIgnoreProperties(value = "competitions", allowSetters = true)
    private Set<User> participants = new HashSet<>();
    @Column(nullable = false)
    private String name;
    private String description;
    @Column(nullable = false)
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime dateTime;
    @Column(nullable = false)
    private String locationName;
    @Column(nullable = false)
    private Point location;
    private boolean cancelled;
}
