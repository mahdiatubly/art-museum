package com.tech.altoubli.museum.art.post;

import com.tech.altoubli.museum.art.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDate;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private User creator;
    private String imageUrl;
    private String description;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private Boolean requireSubscription;

}
