package com.code10.kts.model.dto;

import com.code10.kts.model.domain.Topic;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents new meeting report.
 */
public class ReportCreateDto {

    /**
     * List of comments on meeting's topics.
     */
    @Valid
    private List<TopicCommentDto> comments;

    /**
     * Overall report comment.
     */
    private String comment;

    public ReportCreateDto() {
    }

    public ReportCreateDto(List<TopicCommentDto> comments, String comment) {
        this.comments = comments;
        this.comment = comment;
    }

    /**
     * Creates list of Topics from this DTO.
     *
     * @return List of Topic model
     */
    public List<Topic> createTopics() {
        return comments.stream().map(c -> new Topic(c.getTopicId(), c.getComment())).collect(Collectors.toList());
    }

    public List<TopicCommentDto> getComments() {
        return comments;
    }

    public void setComments(List<TopicCommentDto> comments) {
        this.comments = comments;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
