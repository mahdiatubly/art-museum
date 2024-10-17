package com.tech.altoubli.museum.art.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByNickName(String username);
    List<User> findByRole_Name(String roleName);
}

