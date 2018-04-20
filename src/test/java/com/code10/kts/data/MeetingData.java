package com.code10.kts.data;

import com.code10.kts.model.domain.Building;
import com.code10.kts.model.domain.Meeting;
import com.code10.kts.model.domain.Topic;
import com.code10.kts.model.dto.MeetingCreateDto;
import com.code10.kts.model.dto.ReportCreateDto;
import com.code10.kts.model.dto.TopicCommentDto;
import com.code10.kts.model.dto.TopicCreateDto;
import com.code10.kts.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Meeting testing constants and utility methods.
 */
public class MeetingData {

    public static final String TOPIC_COMMENT = "comment";

    public static final Long NON_EXISTING_MEETING_ID = 123L;

    public static final int MEETINGS_COUNT = 3;

    public static final int MEETING_TOPICS_COUNT = 2;

    public static Meeting getMeeting(Building building, boolean future) {
        final Meeting meeting = new Meeting();
        meeting.setBuilding(building);
        meeting.setDate(future ? DateUtil.DATE_FUTURE : DateUtil.DATE_PAST);

        final List<Topic> topics = new ArrayList<>();
        for (int i = 0; i < MEETING_TOPICS_COUNT; i++) {
            topics.add(new Topic(TopicData.TOPIC_CONTENT, true, meeting));
        }
        meeting.setTopics(topics);

        return meeting;
    }

    public static MeetingCreateDto getMeetingCreateDto() {
        final MeetingCreateDto meetingCreateDto = new MeetingCreateDto();
        meetingCreateDto.setDate(DateUtil.DATE_FUTURE);

        final List<TopicCreateDto> topics = new ArrayList<>();
        for (int i = 0; i < MEETING_TOPICS_COUNT; i++) {
            topics.add(new TopicCreateDto(TopicData.TOPIC_CONTENT));
        }
        meetingCreateDto.setTopics(topics);

        return meetingCreateDto;
    }

    public static ReportCreateDto getReportCreateDto(Meeting meeting) {
        final ReportCreateDto reportCreateDto = new ReportCreateDto();

        final List<TopicCommentDto> topicCommentDtos = new ArrayList<>();
        for (Topic topic : meeting.getTopics()) {
            topicCommentDtos.add(new TopicCommentDto(topic.getId(), TOPIC_COMMENT));
        }
        reportCreateDto.setComments(topicCommentDtos);

        return reportCreateDto;
    }
}
