package com.code10.kts.service;

import com.code10.kts.controller.exception.AuthorizationException;
import com.code10.kts.controller.exception.NotFoundException;
import com.code10.kts.model.domain.Comment;
import com.code10.kts.model.domain.user.User;
import com.code10.kts.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer for comments' business logic.
 */
@Service
public class CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    /**
     * Creates a new comment for a malfunction.
     *
     * @param comment comment to create
     * @return created comment
     */
    public Comment create(Comment comment) {
        return commentRepository.save(comment);
    }

    /**
     * Gets all malfunction's comments.
     *
     * @param malfunctionId malfunction ID
     * @param buildingId    building ID
     * @return list of malfunction's comments
     */
    public List<Comment> findByMalfunctionIdAndBuildingId(long malfunctionId, long buildingId) {
        return commentRepository.findByMalfunctionIdAndBuildingId(malfunctionId, buildingId);
    }

    /**
     * Gets malfunction's comment by its ID.
     *
     * @param id            comment ID
     * @param malfunctionId malfunction ID
     * @param buildingId    building ID
     * @return malfunction's comment with matching ID
     */
    public Comment findByIdAndMalfunctionIdAndBuildingId(long id, long malfunctionId, long buildingId) {
        return commentRepository.findByIdAndMalfunctionIdAndBuildingId(id, malfunctionId, buildingId).orElseThrow(() ->
                new NotFoundException(String.format("No comment with ID %s found in malfunction with ID %s and building with ID %s!", id, malfunctionId, buildingId)));
    }

    /**
     * Deletes a malfunction's comment.
     *
     * @param id   comment ID
     * @param user current user
     */
    public void delete(long id, User user) {
        final Comment comment = commentRepository.findById(id).orElseThrow(() ->
                new NotFoundException((String.format("No comment with ID %s found!", id))));

        if (comment.getUser().getId().equals(user.getId()) ||
                comment.getMalfunction().getBuilding().getSupervisor().getId().equals(user.getId())) {
            commentRepository.delete(id);
        } else {
            throw new AuthorizationException(String
                    .format("User '%s' does not have authority to delete comment with ID %s!", user.getUsername(), id));
        }
    }
}
