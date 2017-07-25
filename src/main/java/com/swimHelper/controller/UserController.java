package com.swimHelper.controller;

import com.swimHelper.exception.BusinessException;
import com.swimHelper.exception.ForbiddenAccessException;
import com.swimHelper.exception.UserNotFoundException;
import com.swimHelper.model.User;
import com.swimHelper.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Created by marcinus on 19.04.17.
 */
@RestController
@RequestMapping("users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User postUser(@RequestBody User user) throws BusinessException {
        return userService.addUser(user);
    }

    @RequestMapping("{userId}")
    public User getUser(@PathVariable Long userId) throws BusinessException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof User) {
            User currentUser = ((User) auth.getPrincipal());
            if (!currentUser.getId().equals(userId)) {
                throw new ForbiddenAccessException();
            }
        }
        User user = userService.getUser(userId);
        if (user == null) {
            throw new UserNotFoundException("Couldnt find user with id: " + userId);
        }
        return user;
    }
}
