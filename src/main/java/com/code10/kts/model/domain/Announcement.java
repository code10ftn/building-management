package com.code10.kts.model.domain;

import com.code10.kts.model.domain.user.User;

import javax.persistence.*;
import java.util.Date;

/**
 * Represents a building's announcement.
 * Tenants and supervisors can post announcements so that other tenants can be aware of certain problems etc.
 */
@Entity
@Table(name = "announcement")
public class Announcement {

    /**
     * Announcement's ID.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /**
     * Text of announcement.
     */
    @Column(name = "content", nullable = false)
    private String content;

    /**
     * Date when announcement was posted.
     */
    @Column(name = "timestamp")
    private Date timestamp;

    /**
     * Creator of the announcement.
     */
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    /**
     * Building in which this announcement was posted.
     */
    @ManyToOne
    @JoinColumn(name = "building_id")
    private Building building;

    public Announcement() {
    }

    public Announcement(String content, User author, Building building) {
        this.content = content;
        this.timestamp = new Date();
        this.author = author;
        this.building = building;
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

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }
}
