package com.swimHelper.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * Created by Marcin Szalek on 25.08.17.
 */
@Aspect
@Component
public class LoggingAspect {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Before("com.swimHelper.aspect.Pointcuts.anyControllerMethod()")
    public void trace(JoinPoint joinPoint) {
        logger.info("==>Method called: " + joinPoint.getSignature());
        for (Object o : joinPoint.getArgs()) {
            if (o != null) {
                logger.info("--With param: " + o.toString());
            }
        }

    }

    @Around("com.swimHelper.aspect.Pointcuts.timeMeasured()")
    public Object measureTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        logger.info("Measuring time: " + proceedingJoinPoint.getSignature());
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object result = proceedingJoinPoint.proceed();
        stopWatch.stop();
        logger.info("Method " + proceedingJoinPoint.getSignature() + " took " + stopWatch.getTotalTimeMillis() + " ms");

        return result;
    }

}


