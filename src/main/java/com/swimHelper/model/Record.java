package com.swimHelper.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * Created by Monia on 2017-07-18.
 */
@Entity
public abstract class Record {
    @Id
    @GeneratedValue
    protected Long id;
    protected String name;
    @OneToOne
    protected Training training;
    protected int value;
    protected String unit;
}
