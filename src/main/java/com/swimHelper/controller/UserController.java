package com.swimHelper.controller;

import com.swimHelper.exception.BusinessException;
import com.swimHelper.exception.UserNotFoundException;
import com.swimHelper.model.User;
import com.swimHelper.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PutMapping
    @PreAuthorize("principal.id == #user.id")
    public User putUser(@RequestBody User user) throws BusinessException {
        return userService.updateUser(user);
    }

    @RequestMapping("{userId}")
    @PreAuthorize("principal.id == #userId")
    public User getUser(@PathVariable Long userId) throws BusinessException {
        User user = userService.getUser(userId);
        if (user == null) {
            throw new UserNotFoundException("Couldn't find user with id: " + userId);
        }
        return user;
    }
}
