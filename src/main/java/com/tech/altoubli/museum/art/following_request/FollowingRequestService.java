package com.tech.altoubli.museum.art.following_request;

import com.tech.altoubli.museum.art.email.EmailService;
import com.tech.altoubli.museum.art.email.EmailTemplateName;
import com.tech.altoubli.museum.art.exception.FollowingRequestNotFoundException;
import com.tech.altoubli.museum.art.exception.NonAuthorizedActionException;
import com.tech.altoubli.museum.art.feed.FeedRepository;
import com.tech.altoubli.museum.art.user.User;
import com.tech.altoubli.museum.art.user.UserRepository;
import com.tech.altoubli.museum.art.user_profile.UserProfileService;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FollowingRequestService {
    private final UserRepository userRepository;
    private final FollowingRequestRepository followingRequestRepository;
    private final EmailService emailService;
    private final FeedRepository feedRepository;
    private final UserProfileService userProfileService;

    public FollowingRequestService(UserRepository userRepository, FollowingRequestRepository followingRequestRepository, EmailService emailService, FeedRepository feedRepository, UserProfileService userProfileService) {
        this.userRepository = userRepository;
        this.followingRequestRepository = followingRequestRepository;
        this.emailService = emailService;
        this.feedRepository = feedRepository;
        this.userProfileService = userProfileService;
    }

    public ResponseEntity<Map<String, String>> sendFollowingRequest(User sender, User receiver) throws MessagingException {
        if(sender.equals(receiver)){
            Map<String, String>  res = new HashMap<>();
            res.put("Status", "This is you account, no need to subscribe.");
            return ResponseEntity.ok(res);
        } else if(receiver.getIsPublic()){
            if(sender.getFollowing().contains(receiver)){
                Map<String, String>  res = new HashMap<>();
                res.put("Status", "You're already following this account.");
                return ResponseEntity.ok(res);
            }
            FollowingRequest request = FollowingRequest
                    .builder()
                    .sender(sender)
                    .receiver(receiver)
                    .acceptedAt(LocalDate.now())
                    .sentAt(LocalDate.now())
                    .accepted(true)
                    .build();
            followingRequestRepository.save(request);
            sender.getFollowing().add(receiver);
            userRepository.save(sender);
            receiver.getFollowers().add(sender);
            userRepository.save(receiver);
            updateUserFeed(receiver, sender);
            Map<String, String>  res = new HashMap<>();
            res.put("Status", "Now you're following this account!");
            return ResponseEntity.ok(res);
        }
        if(sender.getFollowing().contains(receiver)){
            Map<String, String>  res = new HashMap<>();
            res.put("Status", "You're already following this account.");
            return ResponseEntity.ok(res);
        }
        if (!(sender.getSentFollowingRequests().stream()
                .filter((req) -> req.getReceiver().equals(receiver) && !req.getAccepted()).count() == 0)) {
            Map<String, String>  res = new HashMap<>();
            res.put("Status", "There is a following request has been sent previously.");
            return ResponseEntity.ok(res);
        }

        FollowingRequest request = FollowingRequest
                .builder()
                .sender(sender)
                .receiver(receiver)
                .sentAt(LocalDate.now())
                .accepted(false)
                .build();
        followingRequestRepository.save(request);
        emailService.sendFollowingRequestEmail(receiver.getEmail(), receiver.fullName(), EmailTemplateName.FOLLOWING_REQUEST,
                "http://localhost:8090", "http://localhost:8090",
                "Following Request", request);
        Map<String, String>  res = new HashMap<>();
        res.put("Status", "Your request has been sent, wait for acceptance!");
        return ResponseEntity.ok(res);
    }

    public ResponseEntity<Map<String, String>> acceptFollowingRequest(Long requestId, User user) {
        FollowingRequest request = followingRequestRepository.findById(requestId)
                .orElseThrow(()-> new FollowingRequestNotFoundException("Request Not Found"));
        if(request.getReceiver().equals(user)){
            request.setAccepted(true);
            request.setAcceptedAt(LocalDate.now());
            followingRequestRepository.save(request);
            user.getFollowers().add(request.getSender());
            userRepository.save(user);
            request.getSender().getFollowing().add(user);
            userRepository.save(request.getSender());
            updateUserFeed(user, request.getSender());
            Map <String, String> res = new HashMap<>();
            res.put("Status", "The request has been accepted.");
            return ResponseEntity.ok(res);
        }
        throw new NonAuthorizedActionException("You're not authorized to accept this request.");
    }

    public void updateUserFeed(User creator, User follower){
        creator.getPosts().forEach((post)->follower.getFeed().getPosts().add(post));
        feedRepository.save(follower.getFeed());
        userRepository.save(follower);
    }

    public List<FollowingRequestDto> getAllSentFollowingRequest(User user) {
        return user.getSentFollowingRequests().stream()
                .map((req)-> FollowingRequestDto.builder()
                        .sentAt(req.getSentAt())
                        .accepted(req.getAccepted())
                        .acceptedAt(req.getAcceptedAt())
                        .sender(userProfileService.getProfile(req.getSender()).getBody())
                        .receiver(userProfileService.getProfile(req.getReceiver()).getBody())
                        .build())
                .collect(Collectors.toList());
    }

    public List<FollowingRequestDto> getAllReceivedFollowingRequest(User user) {
        return user.getReceivedFollowingRequests().stream()
                .map((req)-> FollowingRequestDto.builder()
                        .sentAt(req.getSentAt())
                        .accepted(req.getAccepted())
                        .acceptedAt(req.getAcceptedAt())
                        .sender(userProfileService.getProfile(req.getSender()).getBody())
                        .receiver(userProfileService.getProfile(req.getReceiver()).getBody())
                        .build())
                .collect(Collectors.toList());
    }

    public ResponseEntity<Map<String, String>> deleteFollowingRequest(Long requestId, User user) {
        FollowingRequest request = followingRequestRepository.findById(requestId)
                .orElseThrow(()-> new FollowingRequestNotFoundException("Request Not Found"));

        if(user.equals(request.getReceiver()) || user.equals(request.getSender())){
            followingRequestRepository.deleteById(requestId);
            Map<String, String> res = new HashMap<>();
            res.put("Status", "The request has been deleted successfully");
            return ResponseEntity.ok(res);
        }
        throw new NonAuthorizedActionException("You're not Authorized to delete this record.");
    }
}
