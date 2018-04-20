package com.code10.kts.controller;

import com.code10.kts.model.domain.Comment;
import com.code10.kts.model.domain.Malfunction;
import com.code10.kts.model.domain.user.User;
import com.code10.kts.model.dto.CommentCreateDto;
import com.code10.kts.service.CommentService;
import com.code10.kts.service.MalfunctionService;
import com.code10.kts.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * REST controller for managing a malfunction's comments.
 */
@RestController
@RequestMapping("/api/buildings/{buildingId}/malfunctions/{malfunctionId}/comments")
public class CommentController {

    private final MalfunctionService malfunctionService;

    private final UserService userService;

    private final CommentService commentService;

    @Autowired
    public CommentController(MalfunctionService malfunctionService,
                             UserService userService, CommentService commentService) {
        this.malfunctionService = malfunctionService;
        this.userService = userService;
        this.commentService = commentService;
    }

    /**
     * GET /api/buildings/buildingID/malfunctions/malfunctionID/comments
     * Gets all malfunction's comments.
     *
     * @param buildingId    building ID
     * @param malfunctionId building's malfunction ID
     * @return ResponseEntity with list of all building's comments
     */
    @GetMapping
    @PreAuthorize("@userService.hasAccessToBuildingMalfunctions(#buildingId)")
    public ResponseEntity findAll(@PathVariable long buildingId, @PathVariable long malfunctionId) {
        final List<Comment> comments = commentService.findByMalfunctionIdAndBuildingId(malfunctionId, buildingId);

        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    /**
     * POST /api/buildings/buildingID/malfunctions/malfunctionID/comments
     * Creates a new comment for a malfunction.
     *
     * @param buildingId       building ID
     * @param malfunctionId    building's malfunction ID
     * @param commentCreateDto DTO with comment
     * @return ResponseEntity with created comment's ID
     */
    @PostMapping
    @PreAuthorize("@userService.hasAccessToBuildingMalfunctions(#buildingId)")
    public ResponseEntity create(@PathVariable long buildingId, @PathVariable long malfunctionId,
                                 @Valid @RequestBody CommentCreateDto commentCreateDto) {
        final User currentUser = userService.findCurrentUser();
        final Malfunction malfunction = malfunctionService.findByIdAndBuildingId(malfunctionId, buildingId);

        final Comment comment = commentService.create(commentCreateDto
                .createComment(malfunction, currentUser));

        return new ResponseEntity<>(comment.getId(), HttpStatus.CREATED);
    }

    /**
     * DELETE /api/buildings/buildingID/malfunctions/malfunctionID/comments/ID
     * Deletes a malfunction's comment.
     *
     * @param buildingId    building ID
     * @param malfunctionId building's malfunction ID
     * @param id            malfunction's comment ID
     * @return ResponseEntity
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@userService.hasAccessToBuildingMalfunctions(#buildingId)")
    public ResponseEntity delete(@PathVariable long buildingId, @PathVariable long malfunctionId, @PathVariable long id) {
        commentService.findByIdAndMalfunctionIdAndBuildingId(id, malfunctionId, buildingId);
        final User user = userService.findCurrentUser();
        commentService.delete(id, user);
        return new ResponseEntity(HttpStatus.OK);
    }
}
