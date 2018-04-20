package com.code10.kts.data;

import com.code10.kts.model.dto.AnswerDto;
import com.code10.kts.model.dto.QuestionCreateDto;
import com.code10.kts.model.dto.SurveyCreateDto;
import com.code10.kts.util.DateUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Survey testing constants and utility methods.
 */
public class SurveyData {

    public static final int SURVEY_COUNT = 2;

    public static final int NON_EXPIRED_SURVEY_COUNT = 1;

    public static final int EXPIRED_SURVEY_COUNT = 1;

    public static final Long EXISTING_SURVEY_ID = 1L;

    public static final Long NON_EXISTING_SURVEY_ID = 3L;

    public static final Long EXISTING_ANSWER_ID = 1L;

    public static final Long NON_EXISTING_ANSWER_ID = 5L;

    public static AnswerDto getValidAnswerDto() {
        AnswerDto answerDto = new AnswerDto();
        answerDto.setAnswerIds(new ArrayList<>(Collections.singletonList(EXISTING_ANSWER_ID)));
        return answerDto;
    }

    public static AnswerDto getInvalidAnswerDto() {
        AnswerDto answerDto = new AnswerDto();
        answerDto.setAnswerIds(new ArrayList<>(Collections.singletonList(NON_EXISTING_ANSWER_ID)));
        return answerDto;
    }

    public static SurveyCreateDto getSurveyCreateDto() {
        final SurveyCreateDto surveyCreateDto = new SurveyCreateDto();
        final QuestionCreateDto questionCreateDto = new QuestionCreateDto();

        questionCreateDto.setQuestion("Question 1?");
        questionCreateDto.setAnswers(new ArrayList<>(Arrays.asList("Answer 1", "Answer 2")));

        surveyCreateDto.setName("Some survey");
        surveyCreateDto.getQuestions().add(questionCreateDto);
        surveyCreateDto.setExpirationDate(DateUtil.DATE_FUTURE);

        return surveyCreateDto;
    }
}