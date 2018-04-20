package com.code10.kts.service;

import com.code10.kts.controller.exception.BadRequestException;
import com.code10.kts.controller.exception.NotFoundException;
import com.code10.kts.model.domain.Topic;
import com.code10.kts.model.domain.user.Role;
import com.code10.kts.model.domain.user.User;
import com.code10.kts.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service layer for meeting's topics' business logic.
 */
@Service
public class TopicService {

    private final TopicRepository topicRepository;

    @Autowired
    public TopicService(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    /**
     * Gets all meeting's topics.
     *
     * @param meetingId  meeting ID
     * @param buildingId building ID
     * @param pageable   page number
     * @return one page of meeting's topics
     */
    public Page<Topic> findByMeetingIdAndBuildingId(long meetingId, long buildingId, Pageable pageable) {
        return topicRepository.findByMeetingIdAndBuildingId(meetingId, buildingId, pageable);
    }

    /**
     * Gets a meeting's topic by its ID.
     *
     * @param id         topic ID
     * @param meetingId  meeting ID
     * @param buildingId building ID
     * @return meeting's topic with matching ID
     */
    public Topic findByIdAndMeetingIdAndBuildingId(long id, long meetingId, long buildingId) {
        return topicRepository.findByIdAndMeetingIdAndBuildingId(id, meetingId, buildingId)
                .orElseThrow(() -> new NotFoundException(String.format("No topic with ID %s found in meeting with ID %s!", id, meetingId)));
    }

    /**
     * Creates a new topic for a building.
     *
     * @param topic topic to create
     * @param user  topic author
     * @return created topic
     */
    public Topic create(Topic topic, User user) {
        if (topic.getMeeting().getDate() == null || topic.getMeeting().datePassed()) {
            throw new BadRequestException("Meeting date passed!");
        }

        topic.setAccepted(user.hasAuthority(Role.SUPERVISOR));
        return topicRepository.save(topic);
    }

    /**
     * Updates an existing meeting's topic.
     *
     * @param id         topic ID
     * @param meetingId  meeting ID
     * @param buildingId building ID
     * @param topic      topic to update
     * @return updated topic
     */
    public Topic update(long id, long meetingId, long buildingId, Topic topic) {
        final Topic persisted = findByIdAndMeetingIdAndBuildingId(id, meetingId, buildingId);
        if (persisted.getMeeting().getDate() == null || persisted.getMeeting().datePassed()) {
            throw new BadRequestException("Meeting date passed!");
        }

        if (topic.getContent() != null && !topic.getContent().isEmpty()) {
            persisted.setContent(topic.getContent());
        }
        persisted.setAccepted(topic.isAccepted());

        return topicRepository.save(persisted);
    }

    /**
     * Deletes a meeting's topic.
     *
     * @param topic topic to delete
     */
    public void delete(Topic topic) {
        if (topic.getMeeting().getDate() == null || topic.getMeeting().datePassed()) {
            throw new BadRequestException("Meeting date passed!");
        }

        topicRepository.delete(topic);
    }
}
