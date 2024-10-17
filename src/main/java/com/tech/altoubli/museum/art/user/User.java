package com.tech.altoubli.museum.art.user;

import com.tech.altoubli.museum.art.feed.Feed;
import com.tech.altoubli.museum.art.following_request.FollowingRequest;
import com.tech.altoubli.museum.art.post.Post;
import com.tech.altoubli.museum.art.role.Role;
import com.tech.altoubli.museum.art.user_profile.UserProfile;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import static jakarta.persistence.FetchType.EAGER;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails, Principal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nickName;

    private String firstName;
    private String lastName;
    private LocalDate birthDay;

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToOne
    private Feed feed;

    @Column(columnDefinition = "boolean default false")
    private Boolean isPublic;

    private String phoneNumber;

    @ManyToMany
    @JoinTable(
            name = "user_following",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "following_user_id")
    )
    private List<User> following = new ArrayList<>();

    @ManyToMany(mappedBy = "following", fetch = EAGER)
    private List<User> followers = new ArrayList<>();

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FollowingRequest> sentFollowingRequests;

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FollowingRequest> receivedFollowingRequests;

    @ManyToMany
    @JoinTable(
            name = "user_subscribing",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "subscribing_user_id")
    )
    private List<User> subscribing = new ArrayList<>();

    @ManyToMany(mappedBy = "subscribing", fetch = EAGER)
    private List<User> subscribers = new ArrayList<>();

    @ManyToOne(fetch = EAGER)
    private Role role;

    @OneToOne
    private UserProfile profile;

    private boolean accountLocked;
    private boolean enabled;

    @Column(columnDefinition = "boolean default false")
    private boolean locked;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime lastModifiedDate;

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(role.getName()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public String fullName() {
        return getFirstName() + " " + getLastName();
    }

    @Override
    public String getName() {
        return email;
    }
}
