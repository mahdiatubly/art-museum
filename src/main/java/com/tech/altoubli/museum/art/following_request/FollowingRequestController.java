package com.tech.altoubli.museum.art.following_request;

import com.tech.altoubli.museum.art.user.User;
import com.tech.altoubli.museum.art.user.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class FollowingRequestController {

    private final UserRepository userRepository;
    private final FollowingRequestService followingRequestService;

    @PostMapping("/send/follow-request/{username}")
    public ResponseEntity<Map<String, String>> sendFollowingRequest(@PathVariable String username,
                                                                    Authentication connectedUser) throws MessagingException {
        User sender = userRepository.findByEmail(connectedUser.getName())
                .orElseThrow(()-> new UsernameNotFoundException("User Not Found"));
        User receiver = userRepository.findByNickName(username)
                .orElseThrow(()-> new UsernameNotFoundException("User Not Found"));
        return followingRequestService.sendFollowingRequest(sender, receiver);
    }

    @PostMapping("/accept/follow-request/{requestId}")
    public ResponseEntity<Map<String, String>> acceptFollowingRequest(@PathVariable Long requestId,
                                                                    Authentication connectedUser) {
        User user = userRepository.findByEmail(connectedUser.getName())
                .orElseThrow(()-> new UsernameNotFoundException("User Not Found"));
        return followingRequestService.acceptFollowingRequest(requestId, user);
    }

    @DeleteMapping("/delete/follow-request/{requestId}")
    public ResponseEntity<Map<String, String>> deleteFollowingRequest(@PathVariable Long requestId,
                                                                      Authentication connectedUser) {
        User user = userRepository.findByEmail(connectedUser.getName())
                .orElseThrow(()-> new UsernameNotFoundException("User Not Found"));
        return followingRequestService.deleteFollowingRequest(requestId, user);
    }

    @GetMapping("/sent/follow-request")
    public ResponseEntity<List<FollowingRequestDto>> getAllSentFollowingRequests(Authentication connectedUser) {
        User user = userRepository.findByEmail(connectedUser.getName())
                .orElseThrow(()-> new UsernameNotFoundException("User Not Found"));
        return ResponseEntity.ok(followingRequestService.getAllSentFollowingRequest(user));
    }

    @GetMapping("/received/follow-request")
    public ResponseEntity<List<FollowingRequestDto>> getAllReceivedFollowingRequests(Authentication connectedUser) {
        User user = userRepository.findByEmail(connectedUser.getName())
                .orElseThrow(()-> new UsernameNotFoundException("User Not Found"));
        return ResponseEntity.ok(followingRequestService.getAllReceivedFollowingRequest(user));
    }
}
