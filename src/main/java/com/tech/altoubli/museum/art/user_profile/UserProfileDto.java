package com.tech.altoubli.museum.art.user_profile;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class UserProfileDto {
    private String nickName;
    private String fullName;
    private String bio;
    private String imageUrl;
}
