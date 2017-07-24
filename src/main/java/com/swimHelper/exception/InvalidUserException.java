package com.swimHelper.exception;

/**
 * Created by Marcin Szalek on 20.07.17.
 */
public class InvalidUserException extends BusinessException {
    public InvalidUserException() {
        super();
    }

    public InvalidUserException(String message) {
        super(message);
    }

    public InvalidUserException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidUserException(Throwable cause) {
        super(cause);
    }
}
