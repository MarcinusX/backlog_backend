package com.swimHelper;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

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
