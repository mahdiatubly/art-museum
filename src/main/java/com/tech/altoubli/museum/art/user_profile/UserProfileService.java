package com.tech.altoubli.museum.art.user_profile;

import com.tech.altoubli.museum.art.file_upload.FileUploadService;
import com.tech.altoubli.museum.art.user.User;
import com.tech.altoubli.museum.art.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final FileUploadService fileUploadService;

    public void updateBio(UserProfile profile, Authentication connectedUser) {
        User user = userRepository.findByEmail(connectedUser.getName())
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));
        UserProfile userProfile = user.getProfile();
        if(profile.getDescription() == null){
            profile.setDescription("");
        } else {
            userProfile.setDescription(profile.getDescription());
        }
        userProfileRepository.save(userProfile);
    }

    public ResponseEntity<UserProfileDto> getProfile(User user) {
        UserProfile userProfile = user.getProfile();
        return ResponseEntity.ok(new UserProfileDto(user.getUsername(), user.getFullName(),
                userProfile.getDescription(), userProfile.getImageUrl()));
    }

    public void updateProfilePicture(MultipartFile pic, Authentication connectedUser) {
        User user = userRepository.findByEmail(connectedUser.getName())
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));
        UserProfile userProfile = user.getProfile();
        String fullFilePath = fileUploadService.uploadFile(pic, "profile_pics", connectedUser);
        userProfile.setImageUrl(fullFilePath);
        userProfileRepository.save(userProfile);
    }
}
