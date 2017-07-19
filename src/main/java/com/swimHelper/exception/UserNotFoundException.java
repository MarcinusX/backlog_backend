package com.swimHelper.exception;

/**
 * Created by Marcin Szalek on 19.07.17.
 */
public class UserNotFoundException extends Exception {
    public UserNotFoundException() {
        super();
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
