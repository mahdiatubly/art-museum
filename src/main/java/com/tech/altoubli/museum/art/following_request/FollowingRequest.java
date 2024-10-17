package com.tech.altoubli.museum.art.following_request;

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
public class FollowingRequest {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne()
    private User sender;
    @ManyToOne()
    private User receiver;
    private LocalDate sentAt;
    private Boolean accepted;
    private LocalDate acceptedAt;

}
