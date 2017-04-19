package com.swimHelper.controller;

import com.swimHelper.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Created by lukasz on 3/28/17.
 */
public class PlayerControllerTest {

    @Mock
    UserRepository playerRepository;

    @InjectMocks
    UserController sut;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void verifyControllerCalled() {
        // ... stub
        System.out.println();
    }


}