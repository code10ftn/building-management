package com.code10.kts.service;

import com.code10.kts.controller.exception.BadRequestException;
import com.code10.kts.controller.exception.NotFoundException;
import com.code10.kts.model.domain.Answer;
import com.code10.kts.model.domain.Survey;
import com.code10.kts.model.domain.user.Tenant;
import com.code10.kts.repository.AnswerRepository;
import com.code10.kts.repository.SurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Service layer for building's surveys' business logic.
 */
@Service
public class SurveyService {

    private final SurveyRepository surveyRepository;

    private final AnswerRepository answerRepository;

    @Autowired
    public SurveyService(SurveyRepository surveyRepository, AnswerRepository answerRepository) {
        this.surveyRepository = surveyRepository;
        this.answerRepository = answerRepository;
    }

    /**
     * Creates a new survey for a building.
     *
     * @param survey survey to create
     * @return created survey
     */
    public Survey create(Survey survey) {
        if (survey.datePassed()) {
            throw new BadRequestException("Survey date passed!");
        }
        return surveyRepository.save(survey);
    }

    /**
     * Gets building's survey by its ID.
     *
     * @param id         survey ID
     * @param buildingId building ID
     * @return building's survey with matching ID
     */
    public Survey findByBuildingIdAndId(long id, long buildingId) {
        return surveyRepository.findByIdAndBuildingId(id, buildingId).orElseThrow(
                () -> new NotFoundException(String.format("No survey with ID %s found!", id)));
    }

    /**
     * Gets all building's surveys.
     *
     * @param buildingId building ID
     * @param pageable   page number
     * @return one page of building's surveys
     */
    public Page<Survey> findByBuildingId(long buildingId, Pageable pageable) {
        return surveyRepository.findByBuildingId(buildingId, pageable);
    }

    /**
     * Gets all building's active or inactive surveys.
     *
     * @param buildingId building ID
     * @param isActive   isActive
     * @param pageable   page number
     * @return one page of active or inactive surveys
     */
    public Page<Survey> findByBuildingIdAndIsActive(long buildingId, boolean isActive, Pageable pageable) {
        final Date currentDate = new Date();

        if (isActive) {
            return surveyRepository.findByBuildingIdAndExpirationDateAfter(buildingId, currentDate, pageable);
        } else {
            return surveyRepository.findByBuildingIdAndExpirationDateBefore(buildingId, currentDate, pageable);
        }
    }

    /**
     * Gets all building's surveys with expiration date between start and end date.
     *
     * @param buildingId building ID
     * @param start      start date
     * @param end        end date
     * @return list of all surveys with expiration date between start and end date
     */
    public List<Survey> findByBuildingIdAndDateBetween(long buildingId, Date start, Date end) {
        return surveyRepository.findByBuildingIdAndExpirationDateBetween(buildingId, start, end);
    }

    /**
     * Gets all surveys from a meeting report.
     *
     * @param reportId report ID
     * @return list of report's surveys
     */
    public List<Survey> findByReportId(Long reportId) {
        return surveyRepository.findByReportId(reportId);
    }

    /**
     * Creates a tenant's answer(s) for a survey.
     *
     * @param id         survey ID
     * @param buildingId building ID
     * @param tenant     tenant that answered survey
     * @param answerIds  list of answer IDs
     */
    public void addSurveyAnswers(long id, long buildingId, Tenant tenant, List<Long> answerIds) {
        final Survey survey = findByBuildingIdAndId(id, buildingId);
        for (Long answerId : answerIds) {
            addTenantAnswer(answerId, tenant);
        }
        survey.getAnsweredBy().add(tenant);
        surveyRepository.save(survey);
    }

    /**
     * Adds a tenant to answer.
     *
     * @param id     answer ID
     * @param tenant tenant
     */
    private void addTenantAnswer(long id, Tenant tenant) {
        final Answer answer = answerRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format("No answer with ID %s found!", id)));
        answer.getTenants().add(tenant);
    }
}