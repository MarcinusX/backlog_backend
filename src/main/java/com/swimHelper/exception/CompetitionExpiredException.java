package com.swimHelper.exception;

/**
 * Created by Marcin Szalek on 05.09.17.
 */
public class CompetitionExpiredException extends BusinessException {
    public CompetitionExpiredException() {
        super();
    }

    public CompetitionExpiredException(String message) {
        super(message);
    }

    public CompetitionExpiredException(String message, Throwable cause) {
        super(message, cause);
    }

    public CompetitionExpiredException(Throwable cause) {
        super(cause);
    }

    protected CompetitionExpiredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
