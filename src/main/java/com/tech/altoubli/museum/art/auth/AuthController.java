package com.tech.altoubli.museum.art.auth;

import com.tech.altoubli.museum.art.auth.requests.AuthenticationRequest;
import com.tech.altoubli.museum.art.auth.requests.PasswordResetInitialRequest;
import com.tech.altoubli.museum.art.auth.requests.PasswordResetRequest;
import com.tech.altoubli.museum.art.auth.requests.RegistrationRequest;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.management.relation.RoleNotFoundException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService service;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> register(
            @RequestBody @Valid RegistrationRequest request
    ) throws MessagingException, RoleNotFoundException {
        service.register(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }
    @GetMapping("/activate-account")
    public ResponseEntity<Map<String, String>> confirm(
            @RequestParam String token
    ) throws MessagingException {
        service.activateAccount(token);
        HashMap<String, String> res = new HashMap<>();
        res.put("Status", "The account has been activated");
        return ResponseEntity.ok(res);
    }
    @PostMapping("/send-new-activation-code")
    public void newActivationCodeRequest(
            @RequestBody AuthenticationRequest request
    ) throws MessagingException {
        service.resetActivationCode(request);
    }
    @PostMapping("/reset-password-request")
    public void resetPasswordRequest(
            @RequestBody @Valid PasswordResetInitialRequest request
    ) throws MessagingException {
        System.out.println(request.getEmail());
        service.resetForgottenPassword(request.getEmail());
    }
    @PostMapping("/reset-password")
    public ResponseEntity<HashMap<String, String>> resetPassword(
            @RequestBody PasswordResetRequest request
    ) throws MessagingException {
        service.setNewPassword(request.getToken(), request.getPassword(),
                request.getConfirmPassword());
        HashMap<String, String> map = new HashMap<>();
        map.put("Status", "Password reset successfully");
        return ResponseEntity.ok(map);
    }
}
