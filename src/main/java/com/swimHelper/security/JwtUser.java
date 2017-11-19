package com.swimHelper.security;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by mstobieniecka on 2017-11-19.
 */
@Data
@AllArgsConstructor
public class JwtUser {
    private String username;
    private String password;
}
