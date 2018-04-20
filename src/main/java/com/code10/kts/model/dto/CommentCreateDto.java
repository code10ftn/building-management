package com.code10.kts.model.dto;

import com.code10.kts.model.domain.Comment;
import com.code10.kts.model.domain.Malfunction;
import com.code10.kts.model.domain.user.User;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Date;

/**
 * Represents a new comment on a malfunction.
 */
public class CommentCreateDto {

    /**
     * Comment's text.
     */
    @NotEmpty
    private String content;

    public CommentCreateDto() {
    }

    public CommentCreateDto(String content) {
        this.content = content;
    }

    /**
     * Creates Comment from this DTO.
     *
     * @param malfunction malfunction to which this comment is added
     * @param user        user that created this comment
     * @return Comment model
     */
    public Comment createComment(Malfunction malfunction, User user) {
        return new Comment(content, malfunction, user, new Date());
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
