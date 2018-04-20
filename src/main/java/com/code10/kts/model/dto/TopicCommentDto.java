package com.code10.kts.model.dto;

import javax.validation.constraints.NotNull;

/**
 * Represents comment on a topic.
 */
public class TopicCommentDto {

    /**
     * Topic's ID which this comment is regarding.
     */
    @NotNull
    private Long topicId;

    /**
     * Text of a comment.
     */
    private String comment;

    public TopicCommentDto() {
    }

    public TopicCommentDto(String comment) {
        this.comment = comment;
    }

    public TopicCommentDto(Long topicId, String comment) {
        this.topicId = topicId;
        this.comment = comment;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
