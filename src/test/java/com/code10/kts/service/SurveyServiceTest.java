package com.code10.kts.service;

import com.code10.kts.controller.exception.BadRequestException;
import com.code10.kts.controller.exception.NotFoundException;
import com.code10.kts.data.SurveyData;
import com.code10.kts.model.domain.Answer;
import com.code10.kts.model.domain.Building;
import com.code10.kts.model.domain.Survey;
import com.code10.kts.model.domain.user.Tenant;
import com.code10.kts.repository.AnswerRepository;
import com.code10.kts.repository.BuildingRepository;
import com.code10.kts.repository.SurveyRepository;
import com.code10.kts.repository.UserRepository;
import com.code10.kts.util.DataUtil;
import com.code10.kts.util.DateUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests {@link SurveyService} methods.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "test")
@Transactional
public class SurveyServiceTest {

    @Autowired
    private SurveyService surveyService;

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    /**
     * Test create survey with invalid data.
     */
    @Test(expected = BadRequestException.class)
    public void createShouldThrowExceptionWhenExpirationDatePassed() {
        // Arrange
        final Survey survey = new Survey();
        survey.setExpirationDate(DateUtil.DATE_PAST);

        // Act
        surveyService.create(survey);
    }

    /**
     * Test create survey with valid data.
     */
    @Test
    public void createShouldReturnCreatedSurveyWhenValid() {
        // Arrange
        final Building building = buildingRepository.findById(DataUtil.EXISTING_BUILDING_ID).orElseGet(null);
        final Survey survey = SurveyData.getSurveyCreateDto().createSurvey(building);

        // Act
        final Survey created = surveyService.create(survey);

        assertNotNull(created);
    }

    /**
     * Test find by id when survey exists.
     */
    @Test
    public void findByBuildingIdAndIdShouldReturnSurvey() {
        // Act
        final Survey survey = surveyService.findByBuildingIdAndId(DataUtil.EXISTING_BUILDING_ID,
                SurveyData.EXISTING_SURVEY_ID);

        // Assert
        assertNotNull(survey);
        assertEquals(SurveyData.EXISTING_SURVEY_ID, survey.getId());
    }

    /**
     * Test find by id when survey does not exist.
     */
    @Test(expected = NotFoundException.class)
    public void findByIdShouldThrowNotFoundExceptionWhenSurveyDoesNotExistInBuilding() {
        // Arrange
        surveyRepository.delete(SurveyData.EXISTING_SURVEY_ID);

        // Act
        surveyService.findByBuildingIdAndId(DataUtil.EXISTING_BUILDING_ID, SurveyData.EXISTING_SURVEY_ID);
    }

    /**
     * Test find by building id.
     */
    @Test
    public void findByBuildingIdShouldReturnSurveys() {
        // Act
        final Page<Survey> surveys = surveyService.findByBuildingId(DataUtil.EXISTING_BUILDING_ID, new PageRequest(0, 100));

        // Assert
        assertEquals(SurveyData.SURVEY_COUNT, surveys.getTotalElements());
    }

    /**
     * Test if adding tenant to survey and answer is valid.
     */
    @Test
    public void addSurveyAnswersShouldAddTenantToAnswersAndSurvey() {
        // Arrange
        final Tenant tenant = (Tenant) userRepository.findById(DataUtil.EXISTING_TENANT_ID).orElseGet(null);
        final List<Long> answerIds = new ArrayList<>(Collections.singletonList(SurveyData.EXISTING_ANSWER_ID));

        // Act
        surveyService.addSurveyAnswers(SurveyData.EXISTING_SURVEY_ID, DataUtil.EXISTING_BUILDING_ID,
                tenant, answerIds);

        // Assert
        final Survey survey = surveyRepository.findByIdAndBuildingId(SurveyData.EXISTING_SURVEY_ID,
                DataUtil.EXISTING_BUILDING_ID).orElseGet(null);
        assertTrue(survey.getAnsweredBy().stream().anyMatch(t -> t.getId().equals(DataUtil.EXISTING_TENANT_ID)));

        final Answer answer = answerRepository.findById(SurveyData.EXISTING_ANSWER_ID).orElseGet(null);
        assertTrue(answer.getTenants().stream().anyMatch(t -> t.getId().equals(DataUtil.EXISTING_TENANT_ID)));
    }

    /**
     * Test adding tenant to survey and answer when invalid data.
     */
    @Test(expected = NotFoundException.class)
    public void addSurveyAnswersShouldThrowExceptionWhenInvalid() {
        // Arrange
        final Tenant tenant = (Tenant) userRepository.findById(DataUtil.EXISTING_TENANT_ID).orElseGet(null);
        final List<Long> answerIds = new ArrayList<>(Collections.singletonList(SurveyData.NON_EXISTING_ANSWER_ID));

        // Act
        surveyService.addSurveyAnswers(SurveyData.EXISTING_SURVEY_ID, DataUtil.EXISTING_BUILDING_ID,
                tenant, answerIds);
    }
}