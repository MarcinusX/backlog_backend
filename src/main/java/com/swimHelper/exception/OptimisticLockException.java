package com.swimHelper.exception;

/**
 * Created by Marcin Szalek on 05.09.17.
 */
public class OptimisticLockException extends BusinessException {
    public OptimisticLockException() {
        super();
    }

    public OptimisticLockException(String message) {
        super(message);
    }

    public OptimisticLockException(String message, Throwable cause) {
        super(message, cause);
    }

    public OptimisticLockException(Throwable cause) {
        super(cause);
    }

    protected OptimisticLockException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
