package com.code10.kts.model.dto;

import com.code10.kts.model.domain.Meeting;
import com.code10.kts.model.domain.Topic;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Represents a new topic.
 */
public class TopicCreateDto {

    /**
     * Text of a topic.
     */
    @NotEmpty
    private String content;

    public TopicCreateDto() {
    }

    public TopicCreateDto(String content) {
        this.content = content;
    }

    /**
     * Creates Topic from this DTO.
     *
     * @param meeting meeting to which this topic is suggested
     * @return Topic model
     */
    public Topic createTopic(Meeting meeting) {
        return new Topic(content, true, meeting);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
