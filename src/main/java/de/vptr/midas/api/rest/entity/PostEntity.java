package de.vptr.midas.api.rest.entity;

import java.time.LocalDateTime;
import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "posts")
public class PostEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotBlank
    public String title;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @NotBlank
    public String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    public PostCategoryEntity category;

    @Column(columnDefinition = "TINYINT(1)")
    public Boolean published = false;

    @Column(columnDefinition = "TINYINT(1)")
    public Boolean commentable = false;

    public LocalDateTime created;

    @Column(name = "last_edit")
    public LocalDateTime lastEdit;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    public List<PostCommentEntity> comments;
}
