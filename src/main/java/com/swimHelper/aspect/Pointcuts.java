package com.swimHelper.aspect;

import org.aspectj.lang.annotation.Pointcut;

/**
 * Created by Marcin Szalek on 25.08.17.
 */
public class Pointcuts {

    @Pointcut("execution(* *..controller..*(..))")
    public void anyControllerMethod() {
    }

    @Pointcut("@annotation(TimeMeasured)")
    public void timeMeasured() {
    }
}
