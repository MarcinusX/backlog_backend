package com.swimHelper.aspect;

import com.swimHelper.controller.UserController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

/**
 * Created by Marcin Szalek on 04.09.17.
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class PointcutsTest {

    @SpyBean
    private LoggingAspect loggingAspect;

    @Autowired
    private UserController anyController;

    @Test
    public void testControllerPointcut() throws Exception {
        //given
        //when
        anyController.getUsers();
        //then
        verify(loggingAspect).trace(any());
    }

}
