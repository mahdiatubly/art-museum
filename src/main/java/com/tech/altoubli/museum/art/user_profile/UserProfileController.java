package com.tech.altoubli.museum.art.user_profile;

import com.tech.altoubli.museum.art.user.User;
import com.tech.altoubli.museum.art.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final UserRepository userRepository;

    @PostMapping(value = "/upload/profile-pic", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, String>> uploadProfilePic(
            @RequestPart("file") MultipartFile file,
            Authentication connectedUser
    ){
        userProfileService.updateProfilePicture(file, connectedUser);
        HashMap<String, String> res = new HashMap<>();
        res.put("Status", "profile_pics uploaded successfully");
        return ResponseEntity.ok(res);
    }

    @PostMapping("/update/bio")
    public ResponseEntity<Map<String, String>> updateBio(
            @RequestBody UserProfile profile,
            Authentication connectedUser
    ){
        HashMap<String, String> res = new HashMap<>();
        userProfileService.updateBio(profile, connectedUser);
        res.put("Status", "Your BIO updated successfully");
        return ResponseEntity.ok(res);
    }

    @GetMapping("get/profile")
    public ResponseEntity<UserProfileDto> getProfile(Authentication connectedUser){
        User user = userRepository.findByEmail(connectedUser.getName())
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));
        return userProfileService.getProfile(user);
    }
}
