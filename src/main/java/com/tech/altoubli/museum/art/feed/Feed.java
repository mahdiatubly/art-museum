package com.tech.altoubli.museum.art.feed;

import com.tech.altoubli.museum.art.post.Post;
import com.tech.altoubli.museum.art.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Feed {
    @Id
    @GeneratedValue
    private Long id;
    @OneToOne
    private User user;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "feed_posts",
            joinColumns = @JoinColumn(name = "feed_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id")
    )
    private List<Post> posts;
}
