package com.code10.kts.model.dto;

import com.code10.kts.model.domain.Building;
import com.code10.kts.model.domain.Survey;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a new survey.
 */
public class SurveyCreateDto {

    /**
     * List of survey questions.
     */
    @NotEmpty
    private List<QuestionCreateDto> questions;

    /**
     * Survey's name.
     */
    @NotNull
    private String name;

    /**
     * Survey's expiration date.
     */
    @NotNull
    private Date expirationDate;

    public SurveyCreateDto() {
        this.questions = new ArrayList<>();
    }

    public SurveyCreateDto(String name, List<QuestionCreateDto> questions, Date expirationDate) {
        this.name = name;
        this.questions = questions;
        this.expirationDate = expirationDate;
    }

    /**
     * Creates Survey from this DTO
     *
     * @param building building in which this survey is created
     * @return Survey model
     */
    public Survey createSurvey(Building building) {
        final Survey survey = new Survey();
        survey.setName(name);
        survey.setQuestions(questions.stream().map(t -> t.createQuestion(survey)).collect(Collectors.toList()));
        survey.setExpirationDate(expirationDate);
        survey.setBuilding(building);
        return survey;
    }

    public List<QuestionCreateDto> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionCreateDto> questions) {
        this.questions = questions;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}