package com.tech.altoubli.museum.art.role;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tech.altoubli.museum.art.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Role {

    @Id
    @GeneratedValue
    private Integer id;
    @Column(unique = true)
    private String name;
    @OneToMany(mappedBy = "role")
    @JsonIgnore
    private List<User> user;
}