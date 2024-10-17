package com.tech.altoubli.museum.art.user_profile;

import com.tech.altoubli.museum.art.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    public Optional<UserProfile> findByUser(User user);
}
