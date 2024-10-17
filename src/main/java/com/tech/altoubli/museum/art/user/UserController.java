package com.tech.altoubli.museum.art.user;

import com.tech.altoubli.museum.art.auth.AuthenticationService;
import com.tech.altoubli.museum.art.auth.requests.ChangePasswordRequest;
import com.tech.altoubli.museum.art.post.PostDto;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final AuthenticationService service;
    private final UserRepository userRepository;
    private final UserService userService;

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            @RequestHeader("Authorization") String token,
            @RequestBody ChangePasswordRequest request
    ) throws MessagingException {
        service.changePassword(token, request.getPassword(),
                request.getNewPassword(), request.getConfirmPassword());
        HashMap<String, String> map = new HashMap<>();
        map.put("Status", "Password changed successfully");
        return ResponseEntity.ok(map);
    }

    @PutMapping("/change-account-status/{status}")
    public ResponseEntity<Map<String, Boolean>> changeUserStatus(@PathVariable String status,
                                                                Authentication connectedUser){
        Boolean booleanStatus = Boolean.valueOf(status);
        User user = userRepository.findByEmail(connectedUser.getName())
                        .orElseThrow(()-> new UsernameNotFoundException("User Not Found"));
        HashMap<String, Boolean> map = new HashMap<>();
        map.put("isPublic", userService.changeAccountStatus(booleanStatus, user));
        return ResponseEntity.ok(map);
    }

    @PutMapping("/change-username/{username}")
    public ResponseEntity<Map<String, String>> changeUsername(@PathVariable String username,
                                                                 Authentication connectedUser){
        User user = userRepository.findByEmail(connectedUser.getName())
                .orElseThrow(()-> new UsernameNotFoundException("User Not Found"));
        HashMap<String, String> map = new HashMap<>();
        map.put("New Username", userService.changeUserName(user, username));
        return ResponseEntity.ok(map);
    }

    @GetMapping("/get-user-feed")
    public ResponseEntity<List<PostDto>> getUserFeed(Authentication connectedUser){
        User user = userRepository.findByEmail(connectedUser.getName())
                .orElseThrow(()-> new UsernameNotFoundException("User Not Found"));
        return ResponseEntity.ok(userService.getUserFeed(user));
    }

    @GetMapping("/get-user-posts")
    public ResponseEntity<List<PostDto>> getUserPosts(Authentication connectedUser){
        User user = userRepository.findByEmail(connectedUser.getName())
                .orElseThrow(()-> new UsernameNotFoundException("User Not Found"));
        return ResponseEntity.ok(userService.getUserPosts(user));
    }
}
