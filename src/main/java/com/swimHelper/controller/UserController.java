package com.swimHelper.controller;

import com.swimHelper.exception.BusinessException;
import com.swimHelper.exception.UserNotFoundException;
import com.swimHelper.model.Role;
import com.swimHelper.model.User;
import com.swimHelper.security.JwtTokenUtil;
import com.swimHelper.security.JwtUser;
import com.swimHelper.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by marcinus on 19.04.17.
 */
@RestController
@RequestMapping("users")
public class UserController {

    private final UserService userService;

    @Value("${jwt.header}")
    private String tokenHeader;

    private final JwtTokenUtil jwtTokenUtil;

    private final UserDetailsService userDetailsService;

    public UserController(UserService userService, JwtTokenUtil jwtTokenUtil, UserDetailsService userDetailsService) {
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
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

    @GetMapping
    @RolesAllowed({Role.NAME_ADMIN})
    public List<User> getUsers() throws BusinessException {
        return userService.getAll();
    }

    @PutMapping("admin/{userId}/makeAdmin")
    @RolesAllowed({Role.NAME_ADMIN})
    public User makeUserAdmin(@PathVariable Long userId) throws BusinessException {
        return userService.makeUserAdmin(userId);
    }

    @RequestMapping(value = "user", method = RequestMethod.GET)
    public JwtUser getAuthenticatedUser(HttpServletRequest request) {
        String token = request.getHeader(tokenHeader).substring(7);
        String username = jwtTokenUtil.getUsernameFromToken(token);
        JwtUser user = (JwtUser) userDetailsService.loadUserByUsername(username);
        return user;
    }
}
