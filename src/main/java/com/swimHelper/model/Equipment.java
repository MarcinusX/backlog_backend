package com.swimHelper.model;

import lombok.Data;
import javax.persistence.Entity;

/**
 * Created by Monia on 2017-07-18.
 */
@Entity
@Data
public class Equipment {
    private String name;
    private String imageUrl;
}
