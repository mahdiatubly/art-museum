package com.tech.altoubli.museum.art.admin;

import com.tech.altoubli.museum.art.user.User;
import com.tech.altoubli.museum.art.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class AdminService {
    private final UserRepository userRepository;

    public void deleteUser(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setLocked(true);
        userRepository.save(user);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findByRole_Name("ROLE_USER")
                .stream()
                .map(user -> new UserDto(
                        user.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.isLocked(),
                        user.isEnabled()))
                .collect(Collectors.toList());
    }
}
