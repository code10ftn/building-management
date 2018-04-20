package com.code10.kts.data;

import com.code10.kts.model.domain.Comment;
import com.code10.kts.model.domain.Malfunction;
import com.code10.kts.model.domain.user.User;
import com.code10.kts.model.dto.CommentCreateDto;

import java.util.Date;

/**
 * Comment testing constants and utility methods.
 */
public class CommentData {

    public static CommentCreateDto getCreateDto() {
        return new CommentCreateDto("test");
    }

    public static Comment getComment(Malfunction malfunction, User user) {
        return new Comment("test", malfunction, user, new Date());
    }
}
