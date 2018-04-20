package com.code10.kts.model.dto;

import com.code10.kts.model.domain.Answer;
import com.code10.kts.model.domain.Question;
import com.code10.kts.model.domain.Survey;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents new survey question.
 */
public class QuestionCreateDto {

    /**
     * Text of a question.
     */
    @NotEmpty
    private String question;

    /**
     * List of possible answers.
     */
    @NotEmpty
    private List<String> answers;

    public QuestionCreateDto() {
        answers = new ArrayList<>();
    }

    public QuestionCreateDto(String question, List<String> answers) {
        this.question = question;
        this.answers = answers;
    }

    /**
     * Creates Question from this DTO.
     *
     * @param survey survey to which this question is added
     * @return Question model
     */
    public Question createQuestion(Survey survey) {
        final Question createQuestion = new Question();
        createQuestion.setAnswers(answers.stream().map(t ->
                new Answer(t, createQuestion)).collect(Collectors.toList()));
        createQuestion.setText(question);
        createQuestion.setSurvey(survey);
        return createQuestion;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }
}