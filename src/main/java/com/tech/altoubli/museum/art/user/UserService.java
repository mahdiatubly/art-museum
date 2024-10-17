package com.tech.altoubli.museum.art.user;

import com.tech.altoubli.museum.art.exception.UsernameNotUniqueException;
import com.tech.altoubli.museum.art.post.PostDto;
import com.tech.altoubli.museum.art.user_profile.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {


    private final UserRepository userRepository;
    private final UserProfileService userProfileService;


    public Boolean changeAccountStatus(Boolean booleanStatus, User user) {
        user.setIsPublic(booleanStatus);
        userRepository.save(user);
        return booleanStatus;
    }

    public String changeUserName(User user, String username) {
        Optional<User> checkUsername = userRepository.findByNickName(username);
        if(checkUsername.isPresent()){
            throw new UsernameNotUniqueException("This username has been taken");
        }
        user.setNickName(username);
        userRepository.save(user);
        return username;
    }

    public List<PostDto> getUserFeed(User user) {
        return user.getFeed().getPosts().stream()
                .map((post)-> PostDto
                        .builder()
                        .userProfile(userProfileService.getProfile(post.getCreator()).getBody())
                        .description(post.getDescription())
                        .imageUrl(post.getImageUrl())
                        .requireSubscription(post.getRequireSubscription())
                        .build())
                .collect(Collectors.toList());
    }

    public List<PostDto> getUserPosts(User user) {
        return user.getPosts().stream()
                .map((post)-> PostDto
                        .builder()
                        .userProfile(userProfileService.getProfile(post.getCreator()).getBody())
                        .description(post.getDescription())
                        .imageUrl(post.getImageUrl())
                        .requireSubscription(post.getRequireSubscription())
                        .build())
                .collect(Collectors.toList());
    }
}
