package com.tech.altoubli.museum.art.post;

import com.tech.altoubli.museum.art.user.User;
import com.tech.altoubli.museum.art.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity<PostDto> createPost(@RequestPart("description") String description,
                                              @RequestPart("requireSubscription") String requireSubscription,
                                              @RequestPart("file") MultipartFile file,
                                              Authentication connectedUser) {
        PostDto postDto = postService.createPost(description, Boolean.valueOf(requireSubscription), file, connectedUser);
        return new ResponseEntity<>(postDto, HttpStatus.CREATED);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> getPostById(@PathVariable Long postId,
                                               Authentication connectedUser) {
        User user = userRepository.findByEmail(connectedUser.getName())
                .orElseThrow(()-> new UsernameNotFoundException("User Not Found"));
        return ResponseEntity.ok(postService.getPostById(postId, user));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<PostDto>> getPostByUser(@PathVariable String username,
                                                     Authentication connectedUser) {
        User user = userRepository.findByEmail(connectedUser.getName())
                .orElseThrow(()-> new UsernameNotFoundException("User Not Found"));
        return ResponseEntity.ok(postService.getPostByUser(username, user));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostDto> updatePostById(@PathVariable Long postId,
                                               @RequestBody PostUpdateRequest request,
                                               Authentication connectedUser) {
        User user = userRepository.findByEmail(connectedUser.getName())
                .orElseThrow(()-> new UsernameNotFoundException("User Not Found"));
        return ResponseEntity.ok(postService.updatePostById(postId, request, user));
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<Map<String, String>> deletePostById(@PathVariable Long postId,
                                         Authentication connectedUser) {
        User user = userRepository.findByEmail(connectedUser.getName())
                .orElseThrow(()-> new UsernameNotFoundException("User Not Found"));
        postService.deletePostById(postId, user);
        Map<String, String> res = new HashMap<>();
        res.put("Status", "The post has been deleted successfully.");
        return ResponseEntity.ok(res);
    }

}
