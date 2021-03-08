package ru.skillbox.socialnetwork;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.socialnetwork.api.requests.ParentIdCommentTextRequest;
import ru.skillbox.socialnetwork.api.requests.TitlePostTextRequest;
import ru.skillbox.socialnetwork.api.responses.*;
import ru.skillbox.socialnetwork.config.security.JwtTokenProvider;
import ru.skillbox.socialnetwork.model.entities.Post;
import ru.skillbox.socialnetwork.model.entities.PostComment;
import ru.skillbox.socialnetwork.repositories.NotificationsRepository;
import ru.skillbox.socialnetwork.repositories.PostCommentRepository;
import ru.skillbox.socialnetwork.repositories.PostLikeRepository;
import ru.skillbox.socialnetwork.repositories.PostRepository;
import ru.skillbox.socialnetwork.services.ConvertTimeService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
class PostControllerTestsTwo {

    private Post savedPost = null;
    private PostComment savedComment = null;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private NotificationsRepository notificationsRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostCommentRepository commentRepository;
    @Autowired
    private PostLikeRepository postLikeRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    @Test
    @WithUserDetails("shred@mail.who")
    @Sql(value = {"/Add2Users.sql", "/AddPosts.sql", "/AddCommentsToPost.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/ClearComments.sql", "/RemovePosts.sql", "/RemoveTestUsers.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testGetApiPostSearch() throws Exception {

        savedPost = postRepository.findById(1L).get();
        savedComment = commentRepository.findById(1L).get();
        List<Post> postList = new ArrayList<>();
        postList.add(savedPost);
        ErrorTimeTotalOffsetPerPageListDataResponse errorTimeTotalOffsetPerPageListDataResponse =
                new ErrorTimeTotalOffsetPerPageListDataResponse(
                        1,
                        0,
                        5,
                        getPostEntityResponseListByPosts(postList)
                );

        long minusMonth = 1;
        mvc.perform(MockMvcRequestBuilders
                .get("/post/")
                .param("text", savedPost.getPostText())
                .param("date_from", getMillis(savedPost.getTime().minusMonths(minusMonth)).toString())
                .param("date_to", getMillis(LocalDateTime.now()).toString())
                .param("offset", String.valueOf(0))
                .param("itemPerPage", String.valueOf(5)))

                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.total")
                        .value(String.valueOf(errorTimeTotalOffsetPerPageListDataResponse.getTotal())))
                .andExpect(jsonPath("$.offset")
                        .value(String.valueOf(errorTimeTotalOffsetPerPageListDataResponse.getOffset())))
                .andExpect(jsonPath("$.perPage")
                        .value(String.valueOf(errorTimeTotalOffsetPerPageListDataResponse.getPerPage())))
                .andExpect(jsonPath("$.data[:1].id")
                        .value(Integer.parseInt(String.valueOf(savedPost.getId()))))
                .andExpect(jsonPath("$.data[:1].time")
                        .value(getMillis(savedPost.getTime())))
                .andExpect(jsonPath("$.data[:1].author.id")
                        .value(Integer.parseInt(String.valueOf(savedPost.getAuthor().getId()))))
                .andExpect(jsonPath("$.data[:1].author.email")
                        .value(String.valueOf(savedPost.getAuthor().getEmail())))
                .andExpect(jsonPath("$.data[:1].author.phone")
                        .value(String.valueOf(savedPost.getAuthor().getPhone())))
                .andExpect(jsonPath("$.data[:1].author.photo")
                        .value(String.valueOf(savedPost.getAuthor().getPhoto())))
                .andExpect(jsonPath("$.data[:1].author.about")
                        .value(String.valueOf(savedPost.getAuthor().getAbout())))
                .andExpect(jsonPath("$.data[:1].author.city")
                        .value(String.valueOf(savedPost.getAuthor().getCity())))
                .andExpect(jsonPath("$.data[:1].author.country")
                        .value(String.valueOf(savedPost.getAuthor().getCountry())))
                .andExpect(jsonPath("$.data[:1].author.first_name")
                        .value(String.valueOf(savedPost.getAuthor().getFirstName())))
                .andExpect(jsonPath("$.data[:1].author.last_name")
                        .value(String.valueOf(savedPost.getAuthor().getLastName())))
                .andExpect(jsonPath("$.data[:1].author.reg_date")
                        .value(getMillis(savedPost.getAuthor().getRegDate())))
                .andExpect(jsonPath("$.data[:1].author.birth_date")
                        .value(getMillis(savedPost.getAuthor().getBirthDate())))
                .andExpect(jsonPath("$.data[:1].author.messages_permission")
                        .value(savedPost.getAuthor().getMessagePermission()))
                .andExpect(jsonPath("$.data[:1].author.last_online_time")
                        .value(getMillis(savedPost.getAuthor().getLastOnlineTime())))
                .andExpect(jsonPath("$.data[:1].author.is_blocked")
                        .value(savedPost.getAuthor().getIsBlocked() == 1))
                .andExpect(jsonPath("$.data[:1].title")
                        .value(savedPost.getTitle()))
                .andExpect(jsonPath("$.data[:1].likes")
                        .value(getPostEntityResponseByPost(savedPost).getLikes()))
                .andExpect(jsonPath("$.data[:1].comments[:1].id")
                        .value(Integer.parseInt(String.valueOf(savedComment.getId()))))
                .andExpect(jsonPath("$.data[:1].comments[:1].time")
                        .value(getMillis(savedComment.getTime())))
                .andExpect(jsonPath("$.data[:1].comments[:1].parent_id")
                        .value(savedComment.getParentId()))
                .andExpect(jsonPath("$.data[:1].comments[:1].comment_text")
                        .value(savedComment.getCommentText()))
                .andExpect(jsonPath("$.data[:1].comments[:1].post_id")
                        .value(Integer.parseInt(String.valueOf(savedComment.getPost().getId()))))

                .andExpect(jsonPath("$.data[:1].comments[:1].is_blocked")
                        .value(savedComment.getIsBlocked()))
                .andExpect(jsonPath("$.data[:1].post_text")
                        .value(savedPost.getPostText()))
                .andExpect(jsonPath("$.data[:1].is_blocked")
                        .value(savedPost.getIsBlocked() == 1));
    }

    @Transactional
    @Test
    @WithUserDetails("shred@mail.who")
    @Sql(value = {"/Add2Users.sql", "/AddPosts.sql", "/AddCommentsToPost.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/ClearComments.sql", "/RemovePosts.sql", "/RemoveTestUsers.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testGetApiPostById() throws Exception {
        savedPost = postRepository.findById(1L).get();
        savedComment = commentRepository.findById(1L).get();
        ErrorTimeDataResponse errorTimeDataResponse = new ErrorTimeDataResponse(
                "", ConvertTimeService.getTimestamp(savedPost.getTime()), getPostEntityResponseByPost(savedPost));

        mvc.perform(MockMvcRequestBuilders
                .get("/post/" + savedPost.getId()))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(errorTimeDataResponse.getError()))
                .andExpect(jsonPath("$.data.id")
                        .value(Integer.parseInt(String.valueOf(savedPost.getId()))))
                .andExpect(jsonPath("$.data.time")
                        .value(getMillis(savedPost.getTime())))
                .andExpect(jsonPath("$.data.author.id")
                        .value(Integer.parseInt(String.valueOf(savedPost.getAuthor().getId()))))
                .andExpect(jsonPath("$.data.author.email")
                        .value(String.valueOf(savedPost.getAuthor().getEmail())))
                .andExpect(jsonPath("$.data.author.phone")
                        .value(String.valueOf(savedPost.getAuthor().getPhone())))
                .andExpect(jsonPath("$.data.author.photo")
                        .value(String.valueOf(savedPost.getAuthor().getPhoto())))
                .andExpect(jsonPath("$.data.author.about")
                        .value(String.valueOf(savedPost.getAuthor().getAbout())))
                .andExpect(jsonPath("$.data.author.city")
                        .value(String.valueOf(savedPost.getAuthor().getCity())))
                .andExpect(jsonPath("$.data.author.country")
                        .value(String.valueOf(savedPost.getAuthor().getCountry())))
                .andExpect(jsonPath("$.data.author.first_name")
                        .value(String.valueOf(savedPost.getAuthor().getFirstName())))
                .andExpect(jsonPath("$.data.author.last_name")
                        .value(String.valueOf(savedPost.getAuthor().getLastName())))
                .andExpect(jsonPath("$.data.author.reg_date")
                        .value(getMillis(savedPost.getAuthor().getRegDate())))
                .andExpect(jsonPath("$.data.author.birth_date")
                        .value(getMillis(savedPost.getAuthor().getBirthDate())))
                .andExpect(jsonPath("$.data.author.messages_permission")
                        .value(savedPost.getAuthor().getMessagePermission()))
                .andExpect(jsonPath("$.data.author.last_online_time")
                        .value(getMillis(savedPost.getAuthor().getLastOnlineTime())))
                .andExpect(jsonPath("$.data.author.is_blocked")
                        .value(savedPost.getAuthor().getIsBlocked() == 1))
                .andExpect(jsonPath("$.data.title")
                        .value(savedPost.getTitle()))
                .andExpect(jsonPath("$.data.likes")
                        .value(getPostEntityResponseByPost(savedPost).getLikes()))
                .andExpect(jsonPath("$.data.comments[:1].id")
                        .value(Integer.parseInt(String.valueOf(savedComment.getId()))))
                .andExpect(jsonPath("$.data.comments[:1].time")
                        .value(getMillis(savedComment.getTime())))
                .andExpect(jsonPath("$.data.comments[:1].comment_text")
                        .value(savedComment.getCommentText()))
                .andExpect(jsonPath("$.data.comments[:1].post_id")
                        .value(Integer.parseInt(String.valueOf(savedPost.getId()))))
                .andExpect(jsonPath("$.data.comments[:1].is_blocked")
                        .value(savedComment.getIsBlocked()))
                .andExpect(jsonPath("$.data.post_text")
                        .value(savedPost.getPostText()))
                .andExpect(jsonPath("$.data.is_blocked")
                        .value(savedPost.getIsBlocked() == 1));
    }

    @Transactional
    @Test
    @WithUserDetails("shred@mail.who")
    @Sql(value = {"/Add2Users.sql", "/AddPosts.sql", "/AddCommentsToPost.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/ClearComments.sql", "/RemovePosts.sql", "/RemoveTestUsers.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testPutPostById() throws Exception {

        savedPost = postRepository.findById(1L).get();
        savedComment = commentRepository.findById(1L).get();

        String newTitle = "Updated post title";
        String newText = "Updated post text";
        TitlePostTextRequest request = new TitlePostTextRequest(newTitle, newText);

        savedPost.setTitle(newTitle);
        savedPost.setPostText(newText);
        ErrorTimeDataResponse errorTimeDataResponse = new ErrorTimeDataResponse(
                "", ConvertTimeService.getTimestamp(savedPost.getTime()), getPostEntityResponseByPost(savedPost));

        mvc.perform(MockMvcRequestBuilders
                .put("/post/{id}", savedPost.getId())
                .param("publish_date", getMillis(savedPost.getTime()).toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(errorTimeDataResponse.getError()))
                .andExpect(jsonPath("$.data.id")
                        .value(Integer.parseInt(String.valueOf(savedPost.getId()))))
                .andExpect(jsonPath("$.data.time")
                        .value(getMillis(savedPost.getTime())))
                .andExpect(jsonPath("$.data.author.id")
                        .value(Integer.parseInt(String.valueOf(savedPost.getAuthor().getId()))))
                .andExpect(jsonPath("$.data.author.email")
                        .value(String.valueOf(savedPost.getAuthor().getEmail())))
                .andExpect(jsonPath("$.data.author.phone")
                        .value(String.valueOf(savedPost.getAuthor().getPhone())))
                .andExpect(jsonPath("$.data.author.photo")
                        .value(String.valueOf(savedPost.getAuthor().getPhoto())))
                .andExpect(jsonPath("$.data.author.about")
                        .value(String.valueOf(savedPost.getAuthor().getAbout())))
                .andExpect(jsonPath("$.data.author.city")
                        .value(String.valueOf(savedPost.getAuthor().getCity())))
                .andExpect(jsonPath("$.data.author.country")
                        .value(String.valueOf(savedPost.getAuthor().getCountry())))
                .andExpect(jsonPath("$.data.author.first_name")
                        .value(String.valueOf(savedPost.getAuthor().getFirstName())))
                .andExpect(jsonPath("$.data.author.last_name")
                        .value(String.valueOf(savedPost.getAuthor().getLastName())))
                .andExpect(jsonPath("$.data.author.reg_date")
                        .value(getMillis(savedPost.getAuthor().getRegDate())))
                .andExpect(jsonPath("$.data.author.birth_date")
                        .value(getMillis(savedPost.getAuthor().getBirthDate())))
                .andExpect(jsonPath("$.data.author.messages_permission")
                        .value(savedPost.getAuthor().getMessagePermission()))
                .andExpect(jsonPath("$.data.author.last_online_time")
                        .value(getMillis(savedPost.getAuthor().getLastOnlineTime())))
                .andExpect(jsonPath("$.data.author.is_blocked")
                        .value(savedPost.getAuthor().getIsBlocked() == 1))
                .andExpect(jsonPath("$.data.title")
                        .value(savedPost.getTitle()))
                .andExpect(jsonPath("$.data.likes")
                        .value(getPostEntityResponseByPost(savedPost).getLikes()))
                .andExpect(jsonPath("$.data.comments[:1].id")
                        .value(Integer.parseInt(String.valueOf(savedComment.getId()))))
                .andExpect(jsonPath("$.data.comments[:1].time")
                        .value(getMillis(savedComment.getTime())))
                .andExpect(jsonPath("$.data.comments[:1].comment_text")
                        .value(savedComment.getCommentText()))
                .andExpect(jsonPath("$.data.comments[:1].post_id")
                        .value(Integer.parseInt(String.valueOf(savedComment.getPost().getId()))))

                .andExpect(jsonPath("$.data.comments[:1].is_blocked")
                        .value(savedComment.getIsBlocked()))
                .andExpect(jsonPath("$.data.post_text")
                        .value(savedPost.getPostText()))
                .andExpect(jsonPath("$.data.is_blocked")
                        .value(savedPost.getIsBlocked() == 1));
    }

    @Test
    @WithUserDetails("shred@mail.who")
    @Sql(value = {"/Add2Users.sql", "/AddPosts.sql", "/AddCommentsToPost.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/ClearComments.sql", "/RemovePosts.sql", "/RemoveTestUsers.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testDeletePostById() throws Exception {
        savedPost = postRepository.findById(1L).get();
        savedComment = commentRepository.findById(1L).get();
        ErrorTimeDataResponse errorTimeDataResponse = new ErrorTimeDataResponse(new IdResponse(savedPost.getId()));

        assertEquals(0, postRepository.findById(savedPost.getId()).get().getIsDeleted());
        mvc.perform(MockMvcRequestBuilders
                .delete("/post/{id}", savedPost.getId()))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(errorTimeDataResponse.getError()))
                .andExpect(jsonPath("$.data.id")
                        .value(Integer.parseInt(String.valueOf(savedPost.getId()))));
        assertEquals(1, postRepository.findById(savedPost.getId()).get().getIsDeleted());
    }

    @Transactional
    @Test
    @WithUserDetails("shred@mail.who")
    @Sql(value = {"/Add2Users.sql", "/AddPosts.sql", "/AddCommentsToPost.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/ClearComments.sql", "/RemovePosts.sql", "/RemoveTestUsers.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testPutPostRecover() throws Exception {
        savedPost = postRepository.findById(1L).get();
        savedComment = commentRepository.findById(1L).get();
        savedPost.setIsDeleted(1);
        postRepository.saveAndFlush(savedPost);
        ErrorTimeDataResponse errorTimeDataResponse = new ErrorTimeDataResponse(
                "", ConvertTimeService.getTimestamp(savedPost.getTime()), getPostEntityResponseByPost(savedPost));

        assertEquals(1, postRepository.findById(savedPost.getId()).get().getIsDeleted());
        mvc.perform(MockMvcRequestBuilders
                .put("/post/{id}/recover/", String.valueOf(savedPost.getId())))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(errorTimeDataResponse.getError()))
                .andExpect(jsonPath("$.data.id")
                        .value(Integer.parseInt(String.valueOf(savedPost.getId()))))
                .andExpect(jsonPath("$.data.time")
                        .value(getMillis(savedPost.getTime())))
                .andExpect(jsonPath("$.data.author.id")
                        .value(Integer.parseInt(String.valueOf(savedPost.getAuthor().getId()))))
                .andExpect(jsonPath("$.data.author.email")
                        .value(String.valueOf(savedPost.getAuthor().getEmail())))
                .andExpect(jsonPath("$.data.author.phone")
                        .value(String.valueOf(savedPost.getAuthor().getPhone())))
                .andExpect(jsonPath("$.data.author.photo")
                        .value(String.valueOf(savedPost.getAuthor().getPhoto())))
                .andExpect(jsonPath("$.data.author.about")
                        .value(String.valueOf(savedPost.getAuthor().getAbout())))
                .andExpect(jsonPath("$.data.author.city")
                        .value(String.valueOf(savedPost.getAuthor().getCity())))
                .andExpect(jsonPath("$.data.author.country")
                        .value(String.valueOf(savedPost.getAuthor().getCountry())))
                .andExpect(jsonPath("$.data.author.first_name")
                        .value(String.valueOf(savedPost.getAuthor().getFirstName())))
                .andExpect(jsonPath("$.data.author.last_name")
                        .value(String.valueOf(savedPost.getAuthor().getLastName())))
                .andExpect(jsonPath("$.data.author.reg_date")
                        .value(getMillis(savedPost.getAuthor().getRegDate())))
                .andExpect(jsonPath("$.data.author.birth_date")
                        .value(getMillis(savedPost.getAuthor().getBirthDate())))
                .andExpect(jsonPath("$.data.author.messages_permission")
                        .value(savedPost.getAuthor().getMessagePermission()))
                .andExpect(jsonPath("$.data.author.last_online_time")
                        .value(getMillis(savedPost.getAuthor().getLastOnlineTime())))
                .andExpect(jsonPath("$.data.author.is_blocked")
                        .value(savedPost.getAuthor().getIsBlocked() == 1))
                .andExpect(jsonPath("$.data.title")
                        .value(savedPost.getTitle()))
                .andExpect(jsonPath("$.data.likes")
                        .value(getPostEntityResponseByPost(savedPost).getLikes()))
                .andExpect(jsonPath("$.data.comments[:1].id")
                        .value(Integer.parseInt(String.valueOf(savedComment.getId()))))
                .andExpect(jsonPath("$.data.comments[:1].time")
                        .value(getMillis(savedComment.getTime())))
                .andExpect(jsonPath("$.data.comments[:1].comment_text")
                        .value(savedComment.getCommentText()))
                .andExpect(jsonPath("$.data.comments[:1].post_id")
                        .value(Integer.parseInt(String.valueOf(savedComment.getPost().getId()))))

                .andExpect(jsonPath("$.data.comments[:1].is_blocked")
                        .value(savedComment.getIsBlocked()))
                .andExpect(jsonPath("$.data.post_text")
                        .value(savedPost.getPostText()))
                .andExpect(jsonPath("$.data.is_blocked")
                        .value(savedPost.getIsBlocked() == 1));

        assertEquals(0, postRepository.findById(savedPost.getId()).get().getIsDeleted());
    }

    @Transactional
    @Test
    @WithUserDetails("shred@mail.who")
    @Sql(value = {"/Add2Users.sql", "/AddPosts.sql", "/AddCommentsToPost.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/ClearComments.sql", "/RemovePosts.sql", "/RemoveTestUsers.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testGetApiPostIdComments() throws Exception {
        savedPost = postRepository.findById(1L).get();
        savedComment = commentRepository.findById(1L).get();
        ErrorTimeTotalOffsetPerPageListDataResponse errorTimeTotalOffsetPerPageListDataResponse =
                new ErrorTimeTotalOffsetPerPageListDataResponse(
                        getCommentEntityResponseListByPost(savedPost).size(),
                        0,
                        5,
                        getCommentEntityResponseListByPost(savedPost, PageRequest.of(0, 5)));

        mvc.perform(MockMvcRequestBuilders
                .get("/post/{id}/comments", String.valueOf(savedPost.getId()))
                .param("offset", String.valueOf(0))
                .param("itemPerPage", String.valueOf(5)))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.total")
                        .value(String.valueOf(errorTimeTotalOffsetPerPageListDataResponse.getTotal())))
                .andExpect(jsonPath("$.offset")
                        .value(String.valueOf(errorTimeTotalOffsetPerPageListDataResponse.getOffset())))
                .andExpect(jsonPath("$.perPage")
                        .value(String.valueOf(errorTimeTotalOffsetPerPageListDataResponse.getPerPage())))
                .andExpect(jsonPath("$.data[:1].id")
                        .value(Integer.parseInt(String.valueOf(savedComment.getId()))))
                .andExpect(jsonPath("$.data[:1].time")
                        .value(getMillis(savedComment.getTime())))
                .andExpect(jsonPath("$.data[:1].comment_text")
                        .value(savedComment.getCommentText()))
                .andExpect(jsonPath("$.data[:1].post_id")
                        .value(Integer.parseInt(String.valueOf(savedComment.getPost().getId()))))
                .andExpect(jsonPath("$.data[:1].is_blocked")
                        .value(savedComment.getIsBlocked()));
    }

    @Test
    @WithUserDetails("shred@mail.who")
    @Sql(value = {"/Add2Users.sql", "/AddPosts.sql", "/AddCommentsToPost.sql", "/AddNotificationTypes.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/ClearComments.sql", "/RemovePosts.sql", "/RemoveTestUsers.sql", "/RemoveNotificationTypes.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testPostApiPostIdComments() throws Exception {
        savedPost = postRepository.findById(1L).get();
        savedComment = commentRepository.findById(1L).get();
        PostComment newComment = new PostComment(
                getMillisecondsToLocalDateTime(System.currentTimeMillis()), null,
                "New comment text!", false, false, savedPost.getAuthor(), savedPost);

        ParentIdCommentTextRequest parentIdCommentTextRequest = new ParentIdCommentTextRequest(
                newComment.getParentId(), newComment.getCommentText());

        mvc.perform(MockMvcRequestBuilders
                .post("/post/{id}/comments", String.valueOf(savedPost.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(parentIdCommentTextRequest)))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.comment_text").value(newComment.getCommentText()))
                .andExpect(jsonPath("$.data.post_id").value(1))
                .andExpect(jsonPath("$.data.is_blocked").value("false"));
        assertEquals(1, notificationsRepository.count());
        notificationsRepository.deleteAll();
        commentRepository.deleteAll();
    }

    @Transactional
    @Test
    @WithUserDetails("shred@mail.who")
    @Sql(value = {"/Add2Users.sql", "/AddPosts.sql", "/AddCommentsToPost.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/ClearComments.sql", "/RemovePosts.sql", "/RemoveTestUsers.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testPutApiPostIdComments() throws Exception {
        savedPost = postRepository.findById(1L).get();
        savedComment = commentRepository.findById(1L).get();
        savedComment.setCommentText("New comment text");
        postRepository.saveAndFlush(savedPost);
        ParentIdCommentTextRequest parentIdCommentTextRequest = new ParentIdCommentTextRequest(
                savedComment.getParentId(),
                savedComment.getCommentText()
        );

        ErrorTimeDataResponse errorTimeDataResponse = new ErrorTimeDataResponse(
                getCommentEntityResponseByComment(savedComment));

        mvc.perform(MockMvcRequestBuilders
                .put("/post/{id}/comments/{comment_id}", String.valueOf(savedPost.getId()),
                        String.valueOf(savedComment.getId()))
                .content(objectMapper.writeValueAsString(parentIdCommentTextRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.id").value(String.valueOf(savedComment.getId())))
                .andExpect(jsonPath("$.data.time").value(String.valueOf(getMillis(savedComment.getTime()))))
                .andExpect(jsonPath("$.data.comment_text").value(savedComment.getCommentText()))
                .andExpect(jsonPath("$.data.post_id").value(1))

                .andExpect(jsonPath("$.data.is_blocked").value("false"));

    }

    @Test
    @WithUserDetails("shred@mail.who")
    @Sql(value = {"/Add2Users.sql", "/AddPosts.sql", "/AddCommentsToPost.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/ClearComments.sql", "/RemovePosts.sql", "/RemoveTestUsers.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testDeleteApiPostIdCommentsCommentId() throws Exception {
        savedPost = postRepository.findById(1L).get();
        savedComment = commentRepository.findById(1L).get();
        assertFalse(commentRepository.findById(savedComment.getId()).get().getIsDeleted());

        mvc.perform(MockMvcRequestBuilders
                .delete("/post/{id}/comments/{commentId}", savedPost.getId(), savedComment.getId()))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.id").value(String.valueOf(savedComment.getId())));

        assertTrue(commentRepository.findById(savedComment.getId()).get().getIsDeleted());
    }

    @Transactional
    @Test
    @WithUserDetails("shred@mail.who")
    @Sql(value = {"/Add2Users.sql", "/AddPosts.sql", "/AddCommentsToPost.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/ClearComments.sql", "/RemovePosts.sql", "/RemoveTestUsers.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testPutApiPostIdCommentsCommentIdRecover() throws Exception {
        savedPost = postRepository.findById(1L).get();
        savedComment = commentRepository.findById(1L).get();

        CommentEntityResponse commentEntityResponse = new CommentEntityResponse(savedComment, commentRepository);

        savedComment.setIsDeleted(true);
        commentRepository.saveAndFlush(savedComment);
        assertTrue(commentRepository.findById(savedComment.getId()).get().getIsDeleted());
        mvc.perform(MockMvcRequestBuilders
                .put("/post/{id}/comments/{commentId}/recover", savedPost.getId(), savedComment.getId()))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.id").value(String.valueOf(commentEntityResponse.getId())))
                .andExpect(jsonPath("$.data.time").value(String.valueOf(commentEntityResponse.getTime())))
                .andExpect(jsonPath("$.data.comment_text").value(commentEntityResponse.getCommentText()))
                .andExpect(jsonPath("$.data.post_id").value(commentEntityResponse.getPostId()))

                .andExpect(jsonPath("$.data.is_blocked").value("false"));
        assertFalse(commentRepository.findById(savedComment.getId()).get().getIsDeleted());
    }

    @Test
    @WithUserDetails("shred@mail.who")
    @Sql(value = {"/Add2Users.sql", "/AddPosts.sql", "/AddCommentsToPost.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/ClearComments.sql", "/RemovePosts.sql", "/RemoveTestUsers.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testPostApiPostIdReport() throws Exception {
        savedPost = postRepository.findById(1L).get();
        savedComment = commentRepository.findById(1L).get();
        mvc.perform(MockMvcRequestBuilders
                .post("/post/{id}/report", savedPost.getId()))
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.message").value("ok"));

    }

    @Test
    @WithUserDetails("shred@mail.who")
    @Sql(value = {"/Add2Users.sql", "/AddPosts.sql", "/AddCommentsToPost.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/ClearComments.sql", "/RemovePosts.sql", "/RemoveTestUsers.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testPostApiPostIdCommentsCommentIdReport() throws Exception {
        savedPost = postRepository.findById(1L).get();
        savedComment = commentRepository.findById(1L).get();
        mvc.perform(MockMvcRequestBuilders
                .post("/post/{id}/comments/{comment_id}/report", savedPost.getId(), savedComment.getId()))
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.message").value("ok"));
    }

    private PostEntityResponse getPostEntityResponseByPost(Post post) {
        return new PostEntityResponse(
                post.getId(),
                java.util.Date
                        .from(post.getTime().atZone(ZoneId.systemDefault())
                                .toInstant()).getTime(),
                getPersonEntityResponseByPost(post),
                post.getTitle(),
                post.getPostText(),
                post.getIsBlocked() == 1,
                postLikeRepository.findByPostId(post.getId()).size(),
                getCommentEntityResponseListByPost(post)
        );
    }

    private PersonEntityResponse getPersonEntityResponseByPost(Post post) {
        return new PersonEntityResponse(post.getAuthor());
    }

    private List<CommentEntityResponse> getCommentEntityResponseListByPost(Post post) {
        List<CommentEntityResponse> commentEntityResponseList = new ArrayList<>();
        for (PostComment comment : commentRepository.getCommentsByPostId(post.getId())) {
            commentEntityResponseList.add(getCommentEntityResponseByComment(comment));
        }
        return commentEntityResponseList;
    }

    private List<CommentEntityResponse> getCommentEntityResponseListByPost(Post post, Pageable pageable) {
        List<CommentEntityResponse> commentEntityResponseList = new ArrayList<>();
        List<PostComment> comments = commentRepository.getCommentsByPostId(post.getId(), pageable);
        for (PostComment comment : comments) {
            commentEntityResponseList.add(getCommentEntityResponseByComment(comment));
        }
        return commentEntityResponseList;
    }

    private CommentEntityResponse getCommentEntityResponseByComment(PostComment comment) {
        return new CommentEntityResponse(comment, commentRepository);
    }

    private Long getTimeZonedMillis() {
        return System.currentTimeMillis();
    }

    private Long getMillis(LocalDateTime localDateTime) {
        return ConvertTimeService.getTimestamp(localDateTime);
    }

    private List<PostEntityResponse> getPostEntityResponseListByPosts(List<Post> posts) {
        List<PostEntityResponse> postEntityResponseList = new ArrayList<>();
        for (Post post : posts) {
            postEntityResponseList.add(getPostEntityResponseByPost(post));
        }
        return postEntityResponseList;
    }

    private LocalDateTime getMillisecondsToLocalDateTime(long milliseconds) {
        return ConvertTimeService.getLocalDateTime(milliseconds);
    }
}