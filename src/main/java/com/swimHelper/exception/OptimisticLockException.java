package com.swimHelper.exception;

/**
 * Created by Marcin Szalek on 05.09.17.
 */
public class OptimisticLockException extends BusinessException {
    public OptimisticLockException() {
        super();
    }

    public OptimisticLockException(Throwable cause) {
        super(cause);
    }

}
