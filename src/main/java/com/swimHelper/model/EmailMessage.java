package com.swimHelper.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Marcin Szalek on 22.08.17.
 */
@Data
@NoArgsConstructor
public class EmailMessage {
    private String textContent;
    private String subject;
    private Person to;
    private Person from;

    @Data
    @AllArgsConstructor
    public static class Person {
        String name;
        String emailAddress;
    }
}
