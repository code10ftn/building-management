package com.code10.kts.model.dto;

import java.util.List;

/**
 * Represents tenant's chosen answers on a survey.
 */
public class AnswerDto {

    /**
     * List of answers' IDs.
     */
    private List<Long> answerIds;

    public AnswerDto() {
    }

    public AnswerDto(List<Long> answerIds) {
        this.answerIds = answerIds;
    }

    public List<Long> getAnswerIds() {
        return answerIds;
    }

    public void setAnswerIds(List<Long> answerIds) {
        this.answerIds = answerIds;
    }
}