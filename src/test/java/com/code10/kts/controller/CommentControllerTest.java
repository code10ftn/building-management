package com.code10.kts.controller;

import com.code10.kts.data.CommentData;
import com.code10.kts.model.domain.Comment;
import com.code10.kts.model.domain.Malfunction;
import com.code10.kts.model.domain.user.User;
import com.code10.kts.model.dto.CommentCreateDto;
import com.code10.kts.model.security.UserDetailsImpl;
import com.code10.kts.repository.CommentRepository;
import com.code10.kts.repository.MalfunctionRepository;
import com.code10.kts.security.TokenUtils;
import com.code10.kts.util.DataUtil;
import com.code10.kts.util.JsonUtil;
import org.junit.Before;
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

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests {@link CommentController} methods.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "test")
@AutoConfigureMockMvc
@Transactional
public class CommentControllerTest {

    private static final String BASE_URL = String.format("/api/buildings/%s/malfunctions/%s/comments",
            DataUtil.EXISTING_BUILDING_ID, DataUtil.EXISTING_MALFUNCTION_ID);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private MalfunctionRepository malfunctionRepository;

    @Autowired
    private CommentRepository commentRepository;

    private Malfunction malfunction;

    @Before
    public void init() {
        malfunction = malfunctionRepository.findById(DataUtil.EXISTING_MALFUNCTION_ID).orElseGet(null);
    }

    /**
     * Tests create new comment for malfunction.
     * This should return created.
     */
    @Test
    public void createShouldReturnCreatedWhenValid() throws Exception {
        // Arrange
        final CommentCreateDto commentCreateDto = CommentData.getCreateDto();

        // Act
        final MvcResult mvcResult = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(commentCreateDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(malfunction.getCreator().getUsername()))))
                .andExpect(status().isCreated())
                .andReturn();

        // Assert
        final Long commentId = JsonUtil.pojo(mvcResult.getResponse().getContentAsString(), Long.class);
        final Comment comment = commentRepository.findById(commentId).orElseGet(null);
        assertNotNull(comment);
        assertEquals(commentCreateDto.getContent(), comment.getContent());
    }

    /**
     * Tests create new comment for malfunction with invalid data.
     * This should return bad request.
     */
    @Test
    public void createShouldReturnBadRequestWhenInvalid() throws Exception {
        // Arrange
        final CommentCreateDto commentCreateDto = CommentData.getCreateDto();
        commentCreateDto.setContent("");

        // Act
        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(commentCreateDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(malfunction.getCreator().getUsername()))))
                .andExpect(status().isBadRequest());
    }

    /**
     * Tests create new comment for malfunction when user is unauthorised.
     * This should return forbidden.
     */
    @Test
    public void createShouldReturnForbiddenWhenUserCannotComment() throws Exception {
        // Arrange
        final CommentCreateDto commentCreateDto = CommentData.getCreateDto();

        // Act
        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(commentCreateDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_NON_COMPANY_USERNAME))))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests find comments for malfunction when malfunction exists.
     * This should return ok.
     */
    @Test
    public void findByMalfunctionIdShouldReturnOkWhenWhenMalfunctionExists() throws Exception {
        // Arrange
        final User user = malfunction.getCreator();
        final Comment comment = CommentData.getComment(malfunction, user);
        commentRepository.save(comment);

        // Act
        final MvcResult result = mockMvc.perform(get(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(user.getUsername()))))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        Comment[] comments = JsonUtil.pojo(result.getResponse().getContentAsString(), Comment[].class);

        assertTrue(comments.length > 0);
    }

    /**
     * Tests find comments for malfunction when user is unauthorised.
     * This should return forbidden.
     */
    @Test
    public void findByMalfunctionIdShouldReturnForbiddenWhenUserDoesNotHaveAccessToBuilding() throws Exception {
        // Act
        mockMvc.perform(get(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_NON_COMPANY_USERNAME))))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests delete comment with id for malfunction.
     * This should return ok.
     */
    @Test
    public void deleteShouldReturnOkIfCommentExists() throws Exception {
        // Arrange
        final User user = malfunction.getCreator();
        Comment comment = CommentData.getComment(malfunction, user);
        comment = commentRepository.save(comment);

        // Act
        mockMvc.perform(delete(BASE_URL + '/' + comment.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(user.getUsername()))))
                .andExpect(status().isOk());
    }

    /**
     * Tests delete comment with id for malfunction that does not exist.
     * This should return not found.
     */
    @Test
    public void deleteShouldReturnNotFoundIfCommentDoesNotExists() throws Exception {
        // Arrange
        final User user = malfunction.getCreator();
        Comment comment = CommentData.getComment(malfunction, user);
        comment = commentRepository.save(comment);
        commentRepository.delete(comment);

        // Act
        mockMvc.perform(delete(BASE_URL + '/' + comment.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(user.getUsername()))))
                .andExpect(status().isNotFound());
    }
}
