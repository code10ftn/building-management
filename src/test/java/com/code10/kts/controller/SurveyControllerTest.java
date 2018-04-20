package com.code10.kts.controller;

import com.code10.kts.data.CustomPageImpl;
import com.code10.kts.data.SurveyData;
import com.code10.kts.model.domain.Survey;
import com.code10.kts.model.dto.AnswerDto;
import com.code10.kts.model.dto.SurveyCreateDto;
import com.code10.kts.model.security.UserDetailsImpl;
import com.code10.kts.repository.SurveyRepository;
import com.code10.kts.security.TokenUtils;
import com.code10.kts.util.DataUtil;
import com.code10.kts.util.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests {@link SurveyController} methods.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "test")
@AutoConfigureMockMvc
@Transactional
public class SurveyControllerTest {

    private static final String BASE_URL = "/api/buildings/" + DataUtil.EXISTING_BUILDING_ID + "/surveys";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private SurveyRepository surveyRepository;

    /**
     * Test if find all surveys returns all building surveys.
     * This should return ok.
     */
    @Test
    public void findAllShouldReturnAllBuildingSurveys() throws Exception {
        // Act
        final MvcResult result = mockMvc.perform(get(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .param("p", "1")
                .param("s", "100")
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        final CustomPageImpl surveys = JsonUtil.pojo(result.getResponse().getContentAsString(), CustomPageImpl.class);
        assertEquals(SurveyData.SURVEY_COUNT, surveys.getTotalElements());
    }

    /**
     * Test find all surveys when user has no access to building.
     * This should return forbidden.
     */
    @Test
    public void findAllShouldReturnForbiddenWhenUserHasNoAccessToBuilding() throws Exception {
        // Act
        mockMvc.perform(get(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_NON_COMPANY_USERNAME))))
                .andExpect(status().isForbidden());
    }

    /**
     * Test if find all non expired surveys returns all building non expired surveys.
     * This should return ok.
     */
    @Test
    public void findAllShouldReturnNonExpiredSurveysWhenIsActiveIsTrue() throws Exception {
        // Act
        final MvcResult result = mockMvc.perform(get(BASE_URL + "?isActive=true")
                .contentType(MediaType.APPLICATION_JSON)
                .param("p", "1")
                .param("s", "100")
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        final TypeReference type = new TypeReference<CustomPageImpl<Survey>>() {
        };
        final CustomPageImpl<Survey> surveys = JsonUtil.pojo(result.getResponse().getContentAsString(), type);
        assertEquals(SurveyData.NON_EXPIRED_SURVEY_COUNT, surveys.getTotalElements());
        for (Survey survey : surveys) {
            assertThat(survey.getExpirationDate()).isAfter(new Date());
        }
    }

    /**
     * Test if find all expired surveys returns all building expired surveys.
     * This should return ok.
     */
    @Test
    public void findAllShouldReturnExpiredSurveysWhenIsActiveIsFalse() throws Exception {
        // Act
        final MvcResult result = mockMvc.perform(get(BASE_URL + "?isActive=false")
                .contentType(MediaType.APPLICATION_JSON)
                .param("p", "1")
                .param("s", "100")
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        final TypeReference type = new TypeReference<CustomPageImpl<Survey>>() {
        };
        final CustomPageImpl<Survey> surveys = JsonUtil.pojo(result.getResponse().getContentAsString(), type);
        assertEquals(SurveyData.EXPIRED_SURVEY_COUNT, surveys.getTotalElements());
        for (Survey survey : surveys) {
            assertThat(survey.getExpirationDate()).isBefore(new Date());
        }
    }

    /**
     * Test create survey with valid data and authenticated as a non building supervisor
     * This should return forbidden.
     */
    @Test
    public void createShouldReturnForbiddenWhenUnauthorized() throws Exception {
        // Arrange
        final SurveyCreateDto surveyCreateDto = SurveyData.getSurveyCreateDto();

        // Act
        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(surveyCreateDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_NON_SUPERVISOR_USERNAME))))
                .andExpect(status().isForbidden());
    }

    /**
     * Test create survey with invalid data and authenticated as a building supervisor.
     * This should return bad request.
     */
    @Test
    public void createShouldReturnBadRequestWhenInvalid() throws Exception {
        // Arrange
        final SurveyCreateDto surveyCreateDto = new SurveyCreateDto();

        // Act
        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(surveyCreateDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_SUPERVISOR_USERNAME))))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test create survey with valid data and authenticated as a building supervisor.
     * This should return created.
     */
    @Test
    public void createShouldReturnCreatedWhenValid() throws Exception {
        // Arrange
        final SurveyCreateDto surveyCreateDto = SurveyData.getSurveyCreateDto();

        // Act
        final MvcResult mvcResult = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(surveyCreateDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_SUPERVISOR_USERNAME))))
                .andExpect(status().isCreated())
                .andReturn();

        // Assert
        final Long surveyId = JsonUtil.pojo(mvcResult.getResponse().getContentAsString(), Long.class);
        final Survey survey = surveyRepository.findByIdAndBuildingId(surveyId, DataUtil.EXISTING_BUILDING_ID).orElseGet(null);
        assertNotNull(survey);
        assertEquals(surveyCreateDto.getExpirationDate(), survey.getExpirationDate());
        assertEquals(surveyCreateDto.getQuestions().size(), survey.getQuestions().size());
    }

    /**
     * Test create survey with valid data and authenticated as a non building tenant.
     * This should return forbidden.
     */
    @Test
    public void answerSurveyShouldReturnForbiddenWhenUnauthorized() throws Exception {
        // Arrange
        final AnswerDto answerDto = SurveyData.getValidAnswerDto();

        // Act
        mockMvc.perform(post(BASE_URL + "/" + SurveyData.EXISTING_SURVEY_ID + "/answers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(answerDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_NON_TENANT_USERNAME))))
                .andExpect(status().isForbidden());
    }

    /**
     * Test create survey with invalid data and authenticated as a building tenant.
     * This should return not found.
     */
    @Test
    public void answerSurveyShouldReturnNotFoundWhenAnswerNotFound() throws Exception {
        // Arrange
        final AnswerDto answerDto = SurveyData.getInvalidAnswerDto();

        // Act
        mockMvc.perform(post(BASE_URL + "/" + SurveyData.EXISTING_SURVEY_ID + "/answers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(answerDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isNotFound());
    }

    /**
     * Test create survey with valid data and authenticated as a building tenant.
     * This should return created.
     */
    @Test
    public void answerSurveyShouldReturnCreatedWhenValid() throws Exception {
        // Arrange
        final AnswerDto answerDto = SurveyData.getValidAnswerDto();

        // Act
        mockMvc.perform(post(BASE_URL + "/" + SurveyData.EXISTING_SURVEY_ID + "/answers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(answerDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isCreated());
    }

    /**
     * Test create survey with non existing survey id, valid data and authenticated as a building tenant.
     * This should return not found.
     */
    @Test
    public void answerSurveyShouldReturnNotFoundWhenNonExistingSurvey() throws Exception {
        // Arrange
        final AnswerDto answerDto = SurveyData.getValidAnswerDto();

        // Act
        mockMvc.perform(post(BASE_URL + "/" + SurveyData.NON_EXISTING_SURVEY_ID + "/answers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(answerDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isNotFound());
    }
}