package com.tech.altoubli.museum.art.feed;

import com.tech.altoubli.museum.art.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeedRepository extends JpaRepository<Feed, Long> {
    Optional<Feed> findByUser(User user);
}
