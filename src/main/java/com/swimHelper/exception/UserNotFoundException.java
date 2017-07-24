package com.swimHelper.exception;

/**
 * Created by Marcin Szalek on 19.07.17.
 */
public class UserNotFoundException extends BusinessException {
    public UserNotFoundException() {
        super();
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
