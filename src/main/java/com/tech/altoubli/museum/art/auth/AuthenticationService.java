package com.tech.altoubli.museum.art.auth;

import com.tech.altoubli.museum.art.auth.requests.AuthenticationRequest;
import com.tech.altoubli.museum.art.auth.requests.RegistrationRequest;
import com.tech.altoubli.museum.art.email.EmailService;
import com.tech.altoubli.museum.art.email.EmailTemplateName;
import com.tech.altoubli.museum.art.exception.ExpiredTokenException;
import com.tech.altoubli.museum.art.exception.InvalidTokenException;
import com.tech.altoubli.museum.art.exception.PasswordMismatchException;
import com.tech.altoubli.museum.art.exception.UsernameNotUniqueException;
import com.tech.altoubli.museum.art.feed.Feed;
import com.tech.altoubli.museum.art.feed.FeedRepository;
import com.tech.altoubli.museum.art.jwt.JwtService;
import com.tech.altoubli.museum.art.role.RoleRepository;
import com.tech.altoubli.museum.art.user.Token;
import com.tech.altoubli.museum.art.user.TokenRepository;
import com.tech.altoubli.museum.art.user.User;
import com.tech.altoubli.museum.art.user.UserRepository;
import com.tech.altoubli.museum.art.user_profile.UserProfile;
import com.tech.altoubli.museum.art.user_profile.UserProfileRepository;
import jakarta.mail.MessagingException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.management.relation.RoleNotFoundException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final TokenRepository tokenRepository;
    private final RoleRepository roleRepository;
    private final UserProfileRepository userProfileRepository;
    private final FeedRepository feedRepository;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;
    @Value("${application.mailing.frontend.password-reset-url}")
    private String passwordResetUrl;

    public void register(RegistrationRequest request) throws MessagingException, RoleNotFoundException {
        Optional<User> checkUsername = userRepository.findByNickName(request.getNickName());
        if(checkUsername.isPresent()){
            throw new UsernameNotUniqueException("This username has been taken");
        }
        var user = User.builder()
                .nickName(request.getNickName())
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .isPublic(false)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(roleRepository.findByName("ROLE_USER")
                        .orElseThrow(()-> new RoleNotFoundException("Role Not Found")))
                .accountLocked(false)
                .enabled(false)
                .build();
        userRepository.save(user);
        sendValidationEmail(user);
        UserProfile userProfile = new UserProfile();
        userProfile.setUser(user);
        userProfileRepository.save(userProfile);
        Feed feed = new Feed();
        feed.setUser(user);
        feedRepository.save(feed);
        user.setFeed(feed);
        user.setProfile(userProfile);
        userRepository.save(user);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var claims = new HashMap<String, Object>();
        var user = ((User) auth.getPrincipal());
        claims.put("fullName", user.getFullName());

        var jwtToken = jwtService.generateToken(claims, (User) auth.getPrincipal());
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

//    @Transactional
    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid token"));
        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            sendValidationEmail(savedToken.getUser());
            throw new ExpiredTokenException("Activation token has been expired." +
                    " A new token has been send to the same email address");
        }

        var user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);

        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }

    private String generateAndSaveActivationToken(User user) {
        // Generate a token
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(token);

        return generatedToken;
    }

    public void setNewPassword(String token, String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new PasswordMismatchException("Passwords do not match");
        }
        Optional<Token> tokenOptional = tokenRepository.findByToken(token);
        if (tokenOptional.isPresent()) {
            Token tokenObj = tokenOptional.get();
            User user = tokenObj.getUser();
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
        } else {
            throw new InvalidTokenException("Invalid token");
        }
    }

    public void changePassword(String token, String password, String newPassword,
                               String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new PasswordMismatchException("Passwords do not match");
        }

        String username = jwtService.extractUsername(token.substring(7));
        Optional<User> user = userRepository.findByEmail(username);
        User userObj = user.orElseThrow(() ->
                new UsernameNotFoundException("User Not Found"));

        if (passwordEncoder.matches(password.trim(), userObj.getPassword())) {
            userObj.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(userObj);
        } else {
            throw new BadCredentialsException("Please check your credentials.");
        }
    }

    public void resetForgottenPassword(String email) throws MessagingException {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            User usr = user.get();
            String newToken = generateAndSaveActivationToken(usr);
            emailService.sendAccountActivationEmail(
                    usr.getEmail(),
                    usr.getFullName(),
                    EmailTemplateName.RESET_FORGOTTEN_PASSWORD,
                    passwordResetUrl,
                    newToken,
                    "Password Reset"
            );
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);

        emailService.sendAccountActivationEmail(
                user.getEmail(),
                user.getFullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account activation"
        );
    }

    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();

        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }

    public void resetActivationCode(AuthenticationRequest request) throws MessagingException {
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (DisabledException exception){
            User user = userRepository.findByEmail(request.getEmail()).get();
            List<Token> tokens = tokenRepository.findByUser(user);
            tokens.forEach((token)->{
                if (LocalDateTime.now().isBefore(token.getExpiresAt())){
                    token.setExpiresAt(LocalDateTime.now());
                }
            });
            sendValidationEmail(user);
        }
    }
}
