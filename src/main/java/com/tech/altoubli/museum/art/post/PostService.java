package com.tech.altoubli.museum.art.post;

import com.tech.altoubli.museum.art.exception.NonAuthorizedActionException;
import com.tech.altoubli.museum.art.exception.PostNotFoundException;
import com.tech.altoubli.museum.art.file_upload.FileUploadService;
import com.tech.altoubli.museum.art.user.User;
import com.tech.altoubli.museum.art.user.UserRepository;
import com.tech.altoubli.museum.art.user_profile.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final UserProfileService userProfileService;
    private final KafkaTemplate<String, Long> kafkaTemplate;
    private final FileUploadService fileUploadService;

    private static final String TOPIC = "feed.posts";

    public PostDto createPost(String description, Boolean requireSubscription, MultipartFile file,
                                     Authentication connectedUser) {
        User user = userRepository.findByEmail(connectedUser.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
        String filePath = fileUploadService.uploadFile(file, "posts", connectedUser);
        Post post = Post.builder()
                .creator(user)
                .imageUrl(filePath)
                .description(description)
                .createdAt(LocalDate.now())
                .requireSubscription(requireSubscription)
                .build();

        post = postRepository.save(post);
        kafkaTemplate.send(TOPIC, post.getId());

        return PostDto.builder()
                .userProfile(userProfileService.getProfile(
                        post.getCreator()).getBody())
                .imageUrl(post.getImageUrl())
                .description(post.getDescription())
                .requireSubscription(post.getRequireSubscription())
                .createdAt(post.getCreatedAt())
                .build();
    }

    public PostDto getPostById(Long postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post Not Found"));
        User creator = post.getCreator();
        if (creator.getIsPublic() && !post.getRequireSubscription()){
            return PostDto.builder()
                    .userProfile(userProfileService.getProfile(creator).getBody())
                    .imageUrl(post.getImageUrl())
                    .description(post.getDescription())
                    .requireSubscription(post.getRequireSubscription())
                    .createdAt(post.getCreatedAt())
                    .build();
        }
        boolean isCreator = user.getId().equals(creator.getId());
        boolean isSubscribed = user.getSubscribing().contains(creator);
        boolean isFollowingNonExclusive = user.getFollowing().contains(creator) && !post.getRequireSubscription();

        if (isCreator || isSubscribed || isFollowingNonExclusive) {
            return PostDto.builder()
                    .userProfile(userProfileService.getProfile(creator).getBody())
                    .imageUrl(post.getImageUrl())
                    .description(post.getDescription())
                    .requireSubscription(post.getRequireSubscription())
                    .createdAt(post.getCreatedAt())
                    .build();
        } else {
            throw new AccessDeniedException("You do not have permission to view this post");
        }
    }

    public void deletePostById(Long postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new PostNotFoundException("Post Not Found"));
        if(user.getId().equals(post.getCreator().getId())){
            postRepository.deleteById(postId);
        } else {
            throw new NonAuthorizedActionException("You're not Authorized to make this action");
        }
    }

    public PostDto updatePostById(Long postId, PostUpdateRequest request, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new PostNotFoundException("Post Not Found"));
        if(user.getId().equals(post.getCreator().getId())){
            if(request.getDescription() != null){
                post.setDescription(request.getDescription());
                post.setUpdatedAt(LocalDate.now());
            }
            if(request.getRequireSubscription() != null){
                post.setRequireSubscription(request.getRequireSubscription());
                post.setUpdatedAt(LocalDate.now());
            }
            postRepository.save(post);
        } else {
            throw new NonAuthorizedActionException("You're not Authorized to make this action");
        }
        return PostDto.builder()
                .userProfile(userProfileService.getProfile(post.getCreator()).getBody())
                .imageUrl(post.getImageUrl())
                .description(post.getDescription())
                .requireSubscription(post.getRequireSubscription())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    public List<PostDto> getPostByUser(String username, User user) {
        User accountUser = userRepository.findByNickName(username)
                .orElseThrow(()-> new UsernameNotFoundException("User Not Found"));
        if(user.getSubscribing().contains(accountUser) || user.equals(accountUser)){
            return accountUser.getPosts().stream()
                    .map((post)-> PostDto
                            .builder()
                            .userProfile(userProfileService.getProfile(post.getCreator()).getBody())
                            .description(post.getDescription())
                            .imageUrl(post.getImageUrl())
                            .requireSubscription(post.getRequireSubscription())
                            .build())
                    .collect(Collectors.toList());
        }
        if(user.getIsPublic() || user.getFollowing().contains(accountUser)){
            return accountUser.getPosts().stream()
                    .filter((post)-> !post.getRequireSubscription())
                    .map((post)-> PostDto
                            .builder()
                            .userProfile(userProfileService.getProfile(post.getCreator()).getBody())
                            .description(post.getDescription())
                            .imageUrl(post.getImageUrl())
                            .requireSubscription(post.getRequireSubscription())
                            .build())
                    .collect(Collectors.toList());

        }
        throw new AccessDeniedException("You need to follow "
                + accountUser.getUsername() + " to see their posts" );
    }
}
