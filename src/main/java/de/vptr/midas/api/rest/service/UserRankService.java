package de.vptr.midas.api.rest.service;

import java.util.List;
import java.util.Optional;

import de.vptr.midas.api.rest.entity.UserRank;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class UserRankService {

    public List<UserRank> getAllRanks() {
        return UserRank.listAll();
    }

    public Optional<UserRank> findById(final Long id) {
        return UserRank.findByIdOptional(id);
    }

    public Optional<UserRank> findByName(final String name) {
        return UserRank.find("name", name).firstResultOptional();
    }

    @Transactional
    public UserRank createRank(final UserRank rank) {
        // Set default permissions if not provided
        if (rank.postAdd == null)
            rank.postAdd = false;
        if (rank.postDelete == null)
            rank.postDelete = false;
        if (rank.postEdit == null)
            rank.postEdit = false;
        if (rank.postCategoryAdd == null)
            rank.postCategoryAdd = false;
        if (rank.postCategoryDelete == null)
            rank.postCategoryDelete = false;
        if (rank.postCategoryEdit == null)
            rank.postCategoryEdit = false;
        if (rank.postCommentAdd == null)
            rank.postCommentAdd = false;
        if (rank.postCommentDelete == null)
            rank.postCommentDelete = false;
        if (rank.postCommentEdit == null)
            rank.postCommentEdit = false;
        if (rank.userAdd == null)
            rank.userAdd = false;
        if (rank.userDelete == null)
            rank.userDelete = false;
        if (rank.userEdit == null)
            rank.userEdit = false;
        if (rank.userGroupAdd == null)
            rank.userGroupAdd = false;
        if (rank.userGroupDelete == null)
            rank.userGroupDelete = false;
        if (rank.userGroupEdit == null)
            rank.userGroupEdit = false;
        if (rank.userRankAdd == null)
            rank.userRankAdd = false;
        if (rank.userRankDelete == null)
            rank.userRankDelete = false;
        if (rank.userRankEdit == null)
            rank.userRankEdit = false;

        rank.persist();
        return rank;
    }

    @Transactional
    public UserRank updateRank(final UserRank rank) {
        final UserRank existingRank = UserRank.findById(rank.id);
        if (existingRank == null) {
            throw new WebApplicationException("User rank not found", Response.Status.NOT_FOUND);
        }

        // Complete replacement (PUT semantics)
        existingRank.name = rank.name;
        existingRank.postAdd = rank.postAdd != null ? rank.postAdd : false;
        existingRank.postDelete = rank.postDelete != null ? rank.postDelete : false;
        existingRank.postEdit = rank.postEdit != null ? rank.postEdit : false;
        existingRank.postCategoryAdd = rank.postCategoryAdd != null ? rank.postCategoryAdd : false;
        existingRank.postCategoryDelete = rank.postCategoryDelete != null ? rank.postCategoryDelete : false;
        existingRank.postCategoryEdit = rank.postCategoryEdit != null ? rank.postCategoryEdit : false;
        existingRank.postCommentAdd = rank.postCommentAdd != null ? rank.postCommentAdd : false;
        existingRank.postCommentDelete = rank.postCommentDelete != null ? rank.postCommentDelete : false;
        existingRank.postCommentEdit = rank.postCommentEdit != null ? rank.postCommentEdit : false;
        existingRank.userAdd = rank.userAdd != null ? rank.userAdd : false;
        existingRank.userDelete = rank.userDelete != null ? rank.userDelete : false;
        existingRank.userEdit = rank.userEdit != null ? rank.userEdit : false;
        existingRank.userGroupAdd = rank.userGroupAdd != null ? rank.userGroupAdd : false;
        existingRank.userGroupDelete = rank.userGroupDelete != null ? rank.userGroupDelete : false;
        existingRank.userGroupEdit = rank.userGroupEdit != null ? rank.userGroupEdit : false;
        existingRank.userRankAdd = rank.userRankAdd != null ? rank.userRankAdd : false;
        existingRank.userRankDelete = rank.userRankDelete != null ? rank.userRankDelete : false;
        existingRank.userRankEdit = rank.userRankEdit != null ? rank.userRankEdit : false;

        existingRank.persist();
        return existingRank;
    }

    @Transactional
    public UserRank patchRank(final UserRank rank) {
        final UserRank existingRank = UserRank.findById(rank.id);
        if (existingRank == null) {
            throw new WebApplicationException("User rank not found", Response.Status.NOT_FOUND);
        }

        // Partial update (PATCH semantics) - only update provided fields
        if (rank.name != null)
            existingRank.name = rank.name;
        if (rank.postAdd != null)
            existingRank.postAdd = rank.postAdd;
        if (rank.postDelete != null)
            existingRank.postDelete = rank.postDelete;
        if (rank.postEdit != null)
            existingRank.postEdit = rank.postEdit;
        if (rank.postCategoryAdd != null)
            existingRank.postCategoryAdd = rank.postCategoryAdd;
        if (rank.postCategoryDelete != null)
            existingRank.postCategoryDelete = rank.postCategoryDelete;
        if (rank.postCategoryEdit != null)
            existingRank.postCategoryEdit = rank.postCategoryEdit;
        if (rank.postCommentAdd != null)
            existingRank.postCommentAdd = rank.postCommentAdd;
        if (rank.postCommentDelete != null)
            existingRank.postCommentDelete = rank.postCommentDelete;
        if (rank.postCommentEdit != null)
            existingRank.postCommentEdit = rank.postCommentEdit;
        if (rank.userAdd != null)
            existingRank.userAdd = rank.userAdd;
        if (rank.userDelete != null)
            existingRank.userDelete = rank.userDelete;
        if (rank.userEdit != null)
            existingRank.userEdit = rank.userEdit;
        if (rank.userGroupAdd != null)
            existingRank.userGroupAdd = rank.userGroupAdd;
        if (rank.userGroupDelete != null)
            existingRank.userGroupDelete = rank.userGroupDelete;
        if (rank.userGroupEdit != null)
            existingRank.userGroupEdit = rank.userGroupEdit;
        if (rank.userRankAdd != null)
            existingRank.userRankAdd = rank.userRankAdd;
        if (rank.userRankDelete != null)
            existingRank.userRankDelete = rank.userRankDelete;
        if (rank.userRankEdit != null)
            existingRank.userRankEdit = rank.userRankEdit;

        existingRank.persist();
        return existingRank;
    }

    @Transactional
    public boolean deleteRank(final Long id) {
        return UserRank.deleteById(id);
    }
}
