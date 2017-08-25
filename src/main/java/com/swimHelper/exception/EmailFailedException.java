package com.swimHelper.exception;

/**
 * Created by Marcin Szalek on 22.08.17.
 */
public class EmailFailedException extends BusinessException {
    public EmailFailedException(Throwable cause) {
        super(cause);
    }
}
