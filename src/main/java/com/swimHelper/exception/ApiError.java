package com.swimHelper.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by Marcin Szalek on 20.07.17.
 */
@AllArgsConstructor
@Data
public class ApiError {
    private String message;
}
