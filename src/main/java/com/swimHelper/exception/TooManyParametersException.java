package com.swimHelper.exception;

/**
 * Created by mstobieniecka on 2017-09-04.
 */
public class TooManyParametersException extends BusinessException {
    public TooManyParametersException() {
        super();
    }

    public TooManyParametersException(String message) {
        super(message);
    }
}
