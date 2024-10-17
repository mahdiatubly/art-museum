package com.tech.altoubli.museum.art.auth.requests;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PasswordResetRequest {
    @NotEmpty(message = "Your Request does not have a validation token")
    @NotNull(message = "Your Request does not have a validation token")
    private String token;

    @NotEmpty(message = "Password is mandatory")
    @NotNull(message = "Password is mandatory")
    @Size(min = 8, message = "Password should be 8 characters long minimum")
    private String password;

    @NotEmpty(message = "Confirming Password is mandatory")
    @NotNull(message = "Confirming Password is mandatory")
    @Size(min = 8, message = "Password should be 8 characters long minimum")
    private String confirmPassword;
}
