package com.tech.altoubli.museum.art.following_request;

import com.tech.altoubli.museum.art.user.User;
import com.tech.altoubli.museum.art.user_profile.UserProfile;
import com.tech.altoubli.museum.art.user_profile.UserProfileDto;
import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowingRequestDto {
    private UserProfileDto sender;
    private UserProfileDto receiver;
    private LocalDate sentAt;
    private Boolean accepted;
    private LocalDate acceptedAt;
}
