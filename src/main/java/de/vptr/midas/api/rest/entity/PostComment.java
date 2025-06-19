package de.vptr.midas.api.rest.entity;

import java.time.LocalDateTime;
import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "post_comments")
public class PostComment extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @NotBlank
    public String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    public Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User user;

    public LocalDateTime created;

    // Helper method to find comments by post
    public static List<PostComment> findByPostId(final Long postId) {
        return find("post.id", postId).list();
    }

    // Helper method to find comments by user
    public static List<PostComment> findByUserId(final Long userId) {
        return find("user.id", userId).list();
    }

    // Helper method to find recent comments
    public static List<PostComment> findRecentComments(final int limit) {
        return find("ORDER BY created DESC").page(0, limit).list();
    }
}
