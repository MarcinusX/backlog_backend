package com.swimHelper;

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
public class Note {
    @Id
    @GeneratedValue
    private Long id;
    private String author;
    private String text;
    private int likes;
    private Long color;
}
