package com.swimHelper.exception;

/**
 * Created by Marcin Szalek on 20.07.17.
 */
public class UserExistsException extends BusinessException {
    public UserExistsException() {
        super();
    }

    public UserExistsException(String message) {
        super(message);
    }

    public UserExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserExistsException(Throwable cause) {
        super(cause);
    }
}
