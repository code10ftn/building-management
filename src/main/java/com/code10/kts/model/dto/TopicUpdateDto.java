package com.code10.kts.model.dto;

import com.code10.kts.model.domain.Topic;

/**
 * Represents update of a topic.
 */
public class TopicUpdateDto {

    /**
     * New topic text.
     */
    private String content;

    /**
     * Shows whether topic was accepted.
     */
    private boolean accepted;

    public TopicUpdateDto() {
    }

    public TopicUpdateDto(String content, boolean accepted) {
        this.content = content;
        this.accepted = accepted;
    }

    /**
     * Creates Topic from this DTO.
     *
     * @return Topic model
     */
    public Topic createTopic() {
        final Topic topic = new Topic();
        topic.setContent(content);
        topic.setAccepted(accepted);
        return topic;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
}
