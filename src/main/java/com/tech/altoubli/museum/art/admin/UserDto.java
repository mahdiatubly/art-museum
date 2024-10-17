package com.tech.altoubli.museum.art.admin;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserDto {
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private Boolean locked;
    private Boolean enabled;
}
