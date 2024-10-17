package com.tech.altoubli.museum.art.post;

import com.tech.altoubli.museum.art.user_profile.UserProfileDto;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDto {
    private UserProfileDto userProfile;
    private String imageUrl;
    private String description;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private Boolean requireSubscription;
}
