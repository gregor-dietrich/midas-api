package de.vptr.midas.api.rest.entity;

import java.time.LocalDateTime;
import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "user_groups_meta")
public class UserGroupMeta extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    public UserGroup group;

    public LocalDateTime timestamp;

    // Helper method to find by user and group
    public static UserGroupMeta findByUserAndGroup(final Long userId, final Long groupId) {
        return find("user.id = ?1 AND group.id = ?2", userId, groupId).firstResult();
    }

    // Helper method to check if user is in group
    public static boolean isUserInGroup(final Long userId, final Long groupId) {
        return count("user.id = ?1 AND group.id = ?2", userId, groupId) > 0;
    }

    // Helper method to find all groups for a user
    public static List<UserGroupMeta> findByUserId(final Long userId) {
        return find("user.id", userId).list();
    }

    // Helper method to find all users in a group
    public static List<UserGroupMeta> findByGroupId(final Long groupId) {
        return find("group.id", groupId).list();
    }
}
