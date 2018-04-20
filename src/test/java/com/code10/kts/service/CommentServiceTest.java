package com.code10.kts.service;

import com.code10.kts.controller.exception.AuthorizationException;
import com.code10.kts.controller.exception.NotFoundException;
import com.code10.kts.data.CommentData;
import com.code10.kts.data.MalfunctionData;
import com.code10.kts.model.domain.Comment;
import com.code10.kts.model.domain.Malfunction;
import com.code10.kts.repository.CommentRepository;
import com.code10.kts.repository.MalfunctionRepository;
import com.code10.kts.repository.UserRepository;
import com.code10.kts.util.DataUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "test")
@Transactional
public class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MalfunctionRepository malfunctionRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Tests create comment when data is valid.
     * This should return created comment.
     */
    @Test
    public void createShouldReturnCommentWhenValid() {
        // Arrange
        final Malfunction malfunction = malfunctionRepository.findById(DataUtil.EXISTING_MALFUNCTION_ID).orElseGet(null);
        final Comment comment = CommentData.getComment(malfunction, malfunction.getCreator());

        // Act
        final Comment created = commentService.create(comment);

        // Assert
        final Comment persisted = commentRepository.findById(created.getId()).orElseGet(null);
        assertNotNull(persisted);
        assertNotNull(persisted.getUser());
        assertEquals(persisted.getUser().getId(), malfunction.getCreator().getId());
        assertNotNull(persisted.getMalfunction());
        assertEquals(persisted.getMalfunction().getId(), malfunction.getId());
        assertEquals(persisted.getContent(), comment.getContent());
    }

    /**
     * Tests create comment when data is invalid.
     * This should throw DataIntegrityViolationException.
     */
    @Test(expected = DataIntegrityViolationException.class)
    public void createShouldThrowExceptionWhenInvalid() {
        // Arrange
        final Malfunction malfunction = malfunctionRepository.findById(DataUtil.EXISTING_MALFUNCTION_ID).orElseGet(null);
        final Comment comment = CommentData.getComment(malfunction, malfunction.getCreator());
        comment.setContent(null);

        // Act
        commentService.create(comment);
    }

    /**
     * Test find comment by malfunction id and building id.
     * This should return comment list.
     */
    @Test
    public void findByMalfunctionIdAndBuildingIdShouldReturnComments() {
        // Arrange
        final Malfunction malfunction = malfunctionRepository.findById(DataUtil.EXISTING_MALFUNCTION_ID).orElseGet(null);
        final Comment comment = CommentData.getComment(malfunction, malfunction.getCreator());
        commentRepository.save(comment);

        // Act
        List<Comment> comments = commentService.findByMalfunctionIdAndBuildingId(malfunction.getId(), malfunction.getBuilding().getId());

        // Assert
        assertTrue(comments.size() > 0);
    }

    /**
     * Test find comment by id, malfunction id and building id.
     * This should return comment.
     */
    @Test
    public void findByIdAndMalfunctionIdAndBuildingIdShouldReturnComment() {
        // Arrange
        final Malfunction malfunction = malfunctionRepository.findById(DataUtil.EXISTING_MALFUNCTION_ID).orElseGet(null);
        Comment comment = CommentData.getComment(malfunction, malfunction.getCreator());
        comment = commentRepository.save(comment);

        // Act
        final Comment found = commentService.findByIdAndMalfunctionIdAndBuildingId(comment.getId(),
                malfunction.getId(), malfunction.getBuilding().getId());

        // Assert
        assertEquals(comment.getId(), found.getId());
    }

    /**
     * Test find comment by id, malfunction id and building id when comment is not in malfunction.
     * This should throw NotFound exception.
     */
    @Test(expected = NotFoundException.class)
    public void findByIdAndMalfunctionIdAndBuildingIdShouldThrowExceptionWhenMalfunctionDoesNotExist() {
        // Arrange
        final Malfunction malfunction = malfunctionRepository.findById(DataUtil.EXISTING_MALFUNCTION_ID).orElseGet(null);
        Comment comment = CommentData.getComment(malfunction, malfunction.getCreator());
        comment = commentRepository.save(comment);

        long malfunctionId = malfunctionRepository.save(MalfunctionData.getMalfunction(malfunction.getCreator(), malfunction.getBuilding())).getId();

        // Act
        commentService.findByIdAndMalfunctionIdAndBuildingId(comment.getId(), malfunction.getId(), malfunctionId);
    }

    /**
     * Tests delete comment.
     * This should delete comment.
     */
    @Test
    public void deleteShouldDeleteComment() {
        // Arrange
        final Malfunction malfunction = malfunctionRepository.findById(DataUtil.EXISTING_MALFUNCTION_ID).orElseGet(null);
        Comment comment = CommentData.getComment(malfunction, malfunction.getCreator());
        comment = commentRepository.save(comment);

        // Act
        commentService.delete(comment.getId(), comment.getUser());

        // Assert
        Optional<Comment> deleted = commentRepository.findById(comment.getId());
        assertFalse(deleted.isPresent());
    }

    /**
     * Tests delete comment that does not exist.
     * This should throw NotFound exception.
     */
    @Test(expected = NotFoundException.class)
    public void deleteShouldThrowExceptionWhenCommentDoesNotExist() {
        // Arrange
        final Malfunction malfunction = malfunctionRepository.findById(DataUtil.EXISTING_MALFUNCTION_ID).orElseGet(null);
        Comment comment = CommentData.getComment(malfunction, malfunction.getCreator());
        comment = commentRepository.save(comment);
        commentRepository.delete(comment.getId());

        // Act
        commentService.delete(comment.getId(), comment.getUser());
    }

    /**
     * Tests delete comment when user is not a creator.
     * This should throw authorization exception.
     */
    @Test(expected = AuthorizationException.class)
    public void deleteShouldThrowExceptionWhenUserNotCreator() {
        // Arrange
        final Malfunction malfunction = malfunctionRepository.findById(DataUtil.EXISTING_MALFUNCTION_ID).orElseGet(null);
        Comment comment = CommentData.getComment(malfunction, malfunction.getCreator());
        comment = commentRepository.save(comment);

        // Act
        commentService.delete(comment.getId(), userRepository.findById(DataUtil.EXISTING_BUILDING_NON_TENANT_ID).orElseGet(null));
    }
}
