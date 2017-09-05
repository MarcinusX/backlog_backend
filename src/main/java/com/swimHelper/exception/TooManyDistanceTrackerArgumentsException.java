package com.swimHelper.exception;

/**
 * Created by mstobieniecka on 2017-09-04.
 */
public class TooManyDistanceTrackerArgumentsException extends BusinessException {
    public TooManyDistanceTrackerArgumentsException() {
        super();
    }

    public TooManyDistanceTrackerArgumentsException(String message) {
        super(message);
    }
}
