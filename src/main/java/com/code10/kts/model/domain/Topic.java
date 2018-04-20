package com.code10.kts.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * Represents one possible topic in a meeting.
 */
@Entity
@Table(name = "topic")
public class Topic {

    /**
     * Topic's ID.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /**
     * Description of a topic.
     */
    @Column(name = "content", nullable = false)
    private String content;

    /**
     * Shows whether topic was accepted for a meeting.
     */
    @Column(name = "accepted")
    private boolean accepted;

    /**
     * Meeting for which this topic was suggested.
     */
    @ManyToOne
    @JoinColumn(name = "meeting_id")
    @JsonIgnore
    private Meeting meeting;

    /**
     * Comment on a topic. This is needed when meeting report is created.
     */
    @Column(name = "comment")
    private String comment;

    public Topic() {
    }

    public Topic(String content, boolean accepted, Meeting meeting) {
        this.content = content;
        this.accepted = accepted;
        this.meeting = meeting;
    }

    public Topic(Long id, String comment) {
        this.id = id;
        this.comment = comment;
    }

    public Topic(String comment) {
        this.comment = comment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Meeting getMeeting() {
        return meeting;
    }

    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
