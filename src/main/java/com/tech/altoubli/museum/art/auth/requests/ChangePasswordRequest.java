package com.tech.altoubli.museum.art.auth.requests;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChangePasswordRequest {
    private String password;
    private String newPassword;
    private String confirmPassword;
}
