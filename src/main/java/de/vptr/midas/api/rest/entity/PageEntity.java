package de.vptr.midas.api.rest.entity;

import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "pages")
public class PageEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotBlank
    public String title;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @NotBlank
    public String content;

    // Helper method to search pages by title
    public static List<PageEntity> findByTitleContaining(final String title) {
        return find("title LIKE ?1", "%" + title + "%").list();
    }

    // Helper method to search pages by content (using fulltext search)
    public static List<PageEntity> searchContent(final String searchTerm) {
        return find("MATCH(content) AGAINST(?1)", searchTerm).list();
    }
}
