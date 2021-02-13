package ru.skillbox.socialnetwork;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.skillbox.socialnetwork.api.requests.ParentIdCommentTextRequest;
import ru.skillbox.socialnetwork.api.requests.TitlePostTextRequest;
import ru.skillbox.socialnetwork.api.responses.*;
import ru.skillbox.socialnetwork.model.entity.Person;
import ru.skillbox.socialnetwork.model.entity.Post;
import ru.skillbox.socialnetwork.model.entity.PostComment;
import ru.skillbox.socialnetwork.repository.NotificationsRepository;
import ru.skillbox.socialnetwork.repository.PersonRepository;
import ru.skillbox.socialnetwork.repository.PostCommentRepository;
import ru.skillbox.socialnetwork.repository.PostRepository;
import ru.skillbox.socialnetwork.security.JwtTokenProvider;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerTests {

    private final String email = "testtest@test.gmail";
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final String password = "testPassword";
    private final Person testPerson = new Person(0, "Steve", "Jobs",
            LocalDateTime.of(2020, 1, 1, 15, 30, 00),
            LocalDateTime.of(1982, 12, 31, 21, 00, 00),
            email, "+71234567890", encoder.encode(password), "pictures.org/photo.jpg",
            "smth about author", "Ufa", "Russian Federation", "some confirmation code",
            1, "ALL", LocalDateTime.of(2020, 5, 5, 5, 30, 00),
            0, 0, 0);
    private final Post testPost = new Post(0, LocalDateTime.of(2021, 1, 1, 15, 30, 00),
            testPerson, "Test post title", "Test post text", 0, 0, new ArrayList<>());
    private final PostComment testPostComment = new PostComment(0, LocalDateTime.of(2021, 1,
            12, 20, 45, 25), null,
            "Good article!", 0, 0, testPerson, testPost);
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
    private PersonRepository personRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private String auth() {
        return jwtTokenProvider.getAuthentication(email, password);
    }

    @BeforeEach
    public void savePostToPostRepository() {
        savedPost = postRepository.save(testPost);
        testPostComment.setPerson(savedPost.getAuthor());
        testPostComment.setPost(savedPost);
        savedComment = commentRepository.save(testPostComment);
        savedPost.getComments().add(savedComment);
    }

    @AfterEach
    public void restoreDb() {
        notificationsRepository.deleteAll();
        commentRepository.delete(savedComment);
        postRepository.delete(savedPost);
        personRepository.delete(savedPost.getAuthor());
    }

    private List<Post> setPosts(Post post) {
        List<Post> list = new ArrayList<>();
        list.add(post);
        return list;
    }

    @Test
    void started() {
    }


    @Test
    void testGetApiPostSearch() throws Exception {

        ErrorTimeTotalOffsetPerPageListDataResponse errorTimeTotalOffsetPerPageListDataResponse =
                new ErrorTimeTotalOffsetPerPageListDataResponse(
                        "",
                        System.currentTimeMillis(),
                        0,
                        0,
                        5,
                        getPostEntityResponseListByPosts(setPosts(savedPost))
                );

        String jwtToken = auth();
        long minusMonth = 1;
        mvc.perform(MockMvcRequestBuilders
                .get("/post/")
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .param("text", savedPost.getPostText())
                .param("date_from", getMillis(savedPost.getTime().minusMonths(minusMonth)).toString())
                .param("date_to", getMillis(LocalDateTime.now()).toString())
                .param("offset", String.valueOf(0))
                .param("itemPerPage", String.valueOf(5)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.total")
                        .value(String.valueOf(errorTimeTotalOffsetPerPageListDataResponse.getTotal())))
                .andExpect(jsonPath("$.offset")
                        .value(String.valueOf(errorTimeTotalOffsetPerPageListDataResponse.getOffset())))
                .andExpect(jsonPath("$.perPage")
                        .value(String.valueOf(errorTimeTotalOffsetPerPageListDataResponse.getPerPage())));
//                .andExpect(jsonPath("$.data[:1].id")
//                        .value(Integer.parseInt(String.valueOf(savedPost.getId()))))
//                .andExpect(jsonPath("$.data[:1].time")
//                        .value(getMillis(savedPost.getTime())))
//                .andExpect(jsonPath("$.data[:1].author.id")
//                        .value(Integer.parseInt(String.valueOf(savedPost.getAuthor().getId()))))
//                .andExpect(jsonPath("$.data[:1].author.email")
//                        .value(String.valueOf(savedPost.getAuthor().getEmail())))
//                .andExpect(jsonPath("$.data[:1].author.phone")
//                        .value(String.valueOf(savedPost.getAuthor().getPhone())))
//                .andExpect(jsonPath("$.data[:1].author.photo")
//                        .value(String.valueOf(savedPost.getAuthor().getPhoto())))
//                .andExpect(jsonPath("$.data[:1].author.about")
//                        .value(String.valueOf(savedPost.getAuthor().getAbout())))
//                .andExpect(jsonPath("$.data[:1].author.city")
//                        .value(String.valueOf(savedPost.getAuthor().getCity())))
//                .andExpect(jsonPath("$.data[:1].author.country")
//                        .value(String.valueOf(savedPost.getAuthor().getCountry())))
//                .andExpect(jsonPath("$.data[:1].author.first_name")
//                        .value(String.valueOf(savedPost.getAuthor().getFirstName())))
//                .andExpect(jsonPath("$.data[:1].author.last_name")
//                        .value(String.valueOf(savedPost.getAuthor().getLastName())))
//                .andExpect(jsonPath("$.data[:1].author.reg_date")
//                        .value(getMillis(savedPost.getAuthor().getRegDate())))
//                .andExpect(jsonPath("$.data[:1].author.birth_date")
//                        .value(getMillis(savedPost.getAuthor().getBirthDate())))
//                .andExpect(jsonPath("$.data[:1].author.messages_permission")
//                        .value(savedPost.getAuthor().getMessagePermission()))
//                .andExpect(jsonPath("$.data[:1].author.last_online_time")
//                        .value(getMillis(savedPost.getAuthor().getLastOnlineTime())))
//                .andExpect(jsonPath("$.data[:1].author.is_blocked")
//                        .value(savedPost.getAuthor().getIsBlocked() == 1))
//                .andExpect(jsonPath("$.data[:1].title")
//                        .value(savedPost.getTitle()))
//                .andExpect(jsonPath("$.data[:1].likes")
//                        .value(getPostEntityResponseByPost(savedPost).getLikes()))
//                .andExpect(jsonPath("$.data[:1].comments[:1].id")
//                        .value(Integer.parseInt(String.valueOf(savedComment.getId()))))
//                .andExpect(jsonPath("$.data[:1].comments[:1].time")
//                        .value(getMillis(savedComment.getTime())))
//                .andExpect(jsonPath("$.data[:1].comments[:1].parent_id")
//                        .value(Integer.parseInt(String.valueOf(savedComment.getParentId()))))
//                .andExpect(jsonPath("$.data[:1].comments[:1].comment_text")
//                        .value(savedComment.getCommentText()))
//                .andExpect(jsonPath("$.data[:1].comments[:1].post_id")
//                        .value(Integer.parseInt(String.valueOf(savedComment.getPost().getId()))))
//                .andExpect(jsonPath("$.data[:1].comments[:1].author_id")
//                        .value(Integer.parseInt(String.valueOf(savedComment.getPerson().getId()))))
//                .andExpect(jsonPath("$.data[:1].comments[:1].is_blocked")
//                        .value(savedComment.getIsBlocked()))
//                .andExpect(jsonPath("$.data[:1].post_text")
//                        .value(savedPost.getPostText()))
//                .andExpect(jsonPath("$.data[:1].is_blocked")
//                        .value(savedPost.getIsBlocked() == 1));
    }

    @Test
    void testGetApiPostById() throws Exception {
        String jwtToken = auth();
        ErrorTimeDataResponse errorTimeDataResponse = new ErrorTimeDataResponse(
                "", getTimeZonedMillis(), getPostEntityResponseByPost(savedPost));

        mvc.perform(MockMvcRequestBuilders
                .get("/post/" + savedPost.getId()).header(HttpHeaders.AUTHORIZATION, jwtToken))
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
                //.andExpect(jsonPath("$.data.comments[:1].parent_id")
                //        .value(Integer.parseInt(String.valueOf(savedComment.getParentId()))))
                .andExpect(jsonPath("$.data.comments[:1].comment_text")
                        .value(savedComment.getCommentText()))
                .andExpect(jsonPath("$.data.comments[:1].post_id")
                        .value(Integer.parseInt(String.valueOf(savedComment.getPost().getId()))))
                .andExpect(jsonPath("$.data.comments[:1].author_id")
                        .value(Integer.parseInt(String.valueOf(savedComment.getPerson().getId()))))
                .andExpect(jsonPath("$.data.comments[:1].is_blocked")
                        .value(savedComment.getIsBlocked()))
                .andExpect(jsonPath("$.data.post_text")
                        .value(savedPost.getPostText()))
                .andExpect(jsonPath("$.data.is_blocked")
                        .value(savedPost.getIsBlocked() == 1));
    }

    @Test
    void testPutPostById() throws Exception {
        String jwtToken = auth();

        String newTitle = "Updated post title";
        String newText = "Updated post text";
        TitlePostTextRequest request = new TitlePostTextRequest(newTitle, newText);

        savedPost.setTitle(newTitle);
        savedPost.setPostText(newText);
        ErrorTimeDataResponse errorTimeDataResponse = new ErrorTimeDataResponse(
                "", getTimeZonedMillis(), getPostEntityResponseByPost(savedPost));

        mvc.perform(MockMvcRequestBuilders
                .put("/post/{id}", savedPost.getId())
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .param("publish_date", getMillis(savedPost.getTime()).toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
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
                //.andExpect(jsonPath("$.data.comments[:1].parent_id")
                //        .value(Integer.parseInt(String.valueOf(savedComment.getParentId()))))
                .andExpect(jsonPath("$.data.comments[:1].comment_text")
                        .value(savedComment.getCommentText()))
                .andExpect(jsonPath("$.data.comments[:1].post_id")
                        .value(Integer.parseInt(String.valueOf(savedComment.getPost().getId()))))
                .andExpect(jsonPath("$.data.comments[:1].author_id")
                        .value(Integer.parseInt(String.valueOf(savedComment.getPerson().getId()))))
                .andExpect(jsonPath("$.data.comments[:1].is_blocked")
                        .value(savedComment.getIsBlocked()))
                .andExpect(jsonPath("$.data.post_text")
                        .value(savedPost.getPostText()))
                .andExpect(jsonPath("$.data.is_blocked")
                        .value(savedPost.getIsBlocked() == 1));
    }

    @Test
    void testDeletePostById() throws Exception {
        String jwtToken = auth();
        ErrorTimeDataResponse errorTimeDataResponse = new ErrorTimeDataResponse(
                "", getTimeZonedMillis(), new IdResponse(savedPost.getId()));

        assertEquals(0, postRepository.findById(savedPost.getId()).get().getIsDeleted());
        mvc.perform(MockMvcRequestBuilders
                .delete("/post/{id}", savedPost.getId())
                .header(HttpHeaders.AUTHORIZATION, jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(errorTimeDataResponse.getError()))
                .andExpect(jsonPath("$.data.id")
                        .value(Integer.parseInt(String.valueOf(savedPost.getId()))));
        assertEquals(1, postRepository.findById(savedPost.getId()).get().getIsDeleted());
    }

    @Test
    void testPutPostRecover() throws Exception {
        savedPost.setIsDeleted(1);
        postRepository.saveAndFlush(savedPost);
        ErrorTimeDataResponse errorTimeDataResponse = new ErrorTimeDataResponse(
                "", getTimeZonedMillis(), getPostEntityResponseByPost(savedPost));

        assertEquals(1, postRepository.findById(savedPost.getId()).get().getIsDeleted());
        String jwtToken = auth();
        mvc.perform(MockMvcRequestBuilders
                .put("/post/{id}/recover/", String.valueOf(savedPost.getId()))
                .header(HttpHeaders.AUTHORIZATION, jwtToken))
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
               // .andExpect(jsonPath("$.data.comments[:1].parent_id")
               //         .value(Integer.parseInt(String.valueOf(savedComment.getParentId()))))
                .andExpect(jsonPath("$.data.comments[:1].comment_text")
                        .value(savedComment.getCommentText()))
                .andExpect(jsonPath("$.data.comments[:1].post_id")
                        .value(Integer.parseInt(String.valueOf(savedComment.getPost().getId()))))
                .andExpect(jsonPath("$.data.comments[:1].author_id")
                        .value(Integer.parseInt(String.valueOf(savedComment.getPerson().getId()))))
                .andExpect(jsonPath("$.data.comments[:1].is_blocked")
                        .value(savedComment.getIsBlocked()))
                .andExpect(jsonPath("$.data.post_text")
                        .value(savedPost.getPostText()))
                .andExpect(jsonPath("$.data.is_blocked")
                        .value(savedPost.getIsBlocked() == 1));

        assertEquals(0, postRepository.findById(savedPost.getId()).get().getIsDeleted());
    }

    @Test
    void testGetApiPostIdComments() throws Exception {
        ErrorTimeTotalOffsetPerPageListDataResponse errorTimeTotalOffsetPerPageListDataResponse =
                new ErrorTimeTotalOffsetPerPageListDataResponse(
                        "",
                        System.currentTimeMillis(),
                        getCommentEntityResponseListByPost(savedPost).size(),
                        0,
                        5,
                        getCommentEntityResponseListByPost(savedPost, PageRequest.of(0, 5)));

        String jwtToken = auth();
        mvc.perform(MockMvcRequestBuilders
                .get("/post/{id}/comments", String.valueOf(savedPost.getId()))
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .param("offset", String.valueOf(0))
                .param("itemPerPage", String.valueOf(5)))
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
                //.andExpect(jsonPath("$.data[:1].parent_id")
                //        .value(Integer.parseInt(String.valueOf(savedComment.getParentId()))))
                .andExpect(jsonPath("$.data[:1].comment_text")
                        .value(savedComment.getCommentText()))
                .andExpect(jsonPath("$.data[:1].post_id")
                        .value(Integer.parseInt(String.valueOf(savedComment.getPost().getId()))))
                .andExpect(jsonPath("$.data[:1].author_id")
                        .value(Integer.parseInt(String.valueOf(savedComment.getPerson().getId()))))
                .andExpect(jsonPath("$.data[:1].is_blocked")
                        .value(savedComment.getIsBlocked()))
        ;
    }

    @Test
    void testPostApiPostIdComments() throws Exception {
        PostComment newComment = new PostComment(savedComment.getId() + 1,
                getMillisecondsToLocalDateTime(System.currentTimeMillis()), null,
                "New comment text!", 0, 0, savedPost.getAuthor(), savedPost);

        ParentIdCommentTextRequest parentIdCommentTextRequest = new ParentIdCommentTextRequest(
                newComment.getParentId(), newComment.getCommentText());

        String jwtToken = auth();
        mvc.perform(MockMvcRequestBuilders
                .post("/post/{id}/comments", String.valueOf(savedPost.getId()))
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(parentIdCommentTextRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.id").value(String.valueOf(newComment.getId())))
                //.andExpect(jsonPath("$.data.parent_id").value(newComment.getParentId()))
                .andExpect(jsonPath("$.data.comment_text").value(newComment.getCommentText()))
                .andExpect(jsonPath("$.data.post_id").value(newComment.getPost().getId()))
                .andExpect(jsonPath("$.data.author_id").value(newComment.getPost().getAuthor().getId()))
                .andExpect(jsonPath("$.data.is_blocked").value("false"));

        commentRepository.deleteById(newComment.getId());
    }

    @Test
    void testPutApiPostIdComments() throws Exception {
        savedComment.setCommentText("New comment text");
        postRepository.saveAndFlush(savedPost);
        ParentIdCommentTextRequest parentIdCommentTextRequest = new ParentIdCommentTextRequest(
                savedComment.getParentId(),
                savedComment.getCommentText()
        );

        ErrorTimeDataResponse errorTimeDataResponse = new ErrorTimeDataResponse(
                "", getTimeZonedMillis(), getCommentEntityResponseByComment(savedComment));
        String jwtToken = auth();

        mvc.perform(MockMvcRequestBuilders
                .put("/post/{id}/comments/{comment_id}", String.valueOf(savedPost.getId()),
                        String.valueOf(savedComment.getId()))
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .content(objectMapper.writeValueAsString(parentIdCommentTextRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.id").value(String.valueOf(savedComment.getId())))
                .andExpect(jsonPath("$.data.time").value(String.valueOf(getMillis(savedComment.getTime()))))
                //.andExpect(jsonPath("$.data.parent_id").value(savedComment.getParentId()))
                .andExpect(jsonPath("$.data.comment_text").value(savedComment.getCommentText()))
                .andExpect(jsonPath("$.data.post_id").value(savedComment.getPost().getId()))
                .andExpect(jsonPath("$.data.author_id").value(savedComment.getPost().getAuthor().getId()))
                .andExpect(jsonPath("$.data.is_blocked").value("false"));

    }

    @Test
    void testDeleteApiPostIdCommentsCommentId() throws Exception {
        String jwtToken = auth();

        assertFalse(commentRepository.findById(savedComment.getId()).get().getIsDeleted());

        mvc.perform(MockMvcRequestBuilders
                .delete("/post/{id}/comments/{commentId}", savedPost.getId(), savedComment.getId())
                .header(HttpHeaders.AUTHORIZATION, jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.id").value(String.valueOf(savedComment.getId())));

        assertTrue(commentRepository.findById(savedComment.getId()).get().getIsDeleted());
    }

    @Test
    void testPutApiPostIdCommentsCommentIdRecover() throws Exception {
        String jwtToken = auth();

        CommentEntityResponse commentEntityResponse = CommentEntityResponse.builder()
                .id(savedComment.getId())
                .parentId(savedComment.getParentId())
                .commentText(savedComment.getCommentText())
                .authorId(savedComment.getPerson().getId())
                .postId(savedComment.getPost().getId())
                .isBlocked(savedComment.getIsBlocked())
                .time(getMillis(savedComment.getTime())).build();

        ErrorTimeDataResponse errorTimeDataResponse = new ErrorTimeDataResponse(
                "", getTimeZonedMillis(), commentEntityResponse);

        savedComment.setIsDeleted(true);
        commentRepository.saveAndFlush(savedComment);
        assertTrue(commentRepository.findById(savedComment.getId()).get().getIsDeleted());
        mvc.perform(MockMvcRequestBuilders
                .put("/post/{id}/comments/{commentId}/recover", savedPost.getId(), savedComment.getId())
                .header(HttpHeaders.AUTHORIZATION, jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.id").value(String.valueOf(savedComment.getId())))
                .andExpect(jsonPath("$.data.time").value(String.valueOf(getMillis(savedComment.getTime()))))
                //.andExpect(jsonPath("$.data.parent_id").value(savedComment.getParentId()))
                .andExpect(jsonPath("$.data.comment_text").value(savedComment.getCommentText()))
                .andExpect(jsonPath("$.data.post_id").value(savedComment.getPost().getId()))
                .andExpect(jsonPath("$.data.author_id").value(savedComment.getPost().getAuthor().getId()))
                .andExpect(jsonPath("$.data.is_blocked").value("false"));
        assertFalse(commentRepository.findById(savedComment.getId()).get().getIsDeleted());
    }

    @Test
    void testPostApiPostIdReport() throws Exception {
        String jwtToken = auth();
        ErrorTimeDataResponse errorTimeDataResponse = new ErrorTimeDataResponse(
                "", getTimeZonedMillis(), new MessageResponse());

        mvc.perform(MockMvcRequestBuilders
                .post("/post/{id}/report", savedPost.getId())
                .header(HttpHeaders.AUTHORIZATION, jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.message").value("ok"));

    }

    @Test
    void testPostApiPostIdCommentsCommentIdReport() throws Exception {
        String jwtToken = auth();
        ErrorTimeDataResponse errorTimeDataResponse = new ErrorTimeDataResponse(
                "", getTimeZonedMillis(), new MessageResponse());

        mvc.perform(MockMvcRequestBuilders
                .post("/post/{id}/comments/{comment_id}/report", savedPost.getId(), savedComment.getId())
                .header(HttpHeaders.AUTHORIZATION, jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.message").value("ok"));

    }

    private PostEntityResponse getPostEntityResponseByPost(Post post) {
        return new PostEntityResponse(
                post.getId(),
                java.util.Date
                        .from(post.getTime().atZone(ZoneId.of("Europe/Moscow"))
                                .toInstant()).getTime(),
                getPersonEntityResponseByPost(post),
                post.getTitle(),
                post.getPostText(),
                post.getIsBlocked() == 1,
                1,
                getCommentEntityResponseListByPost(post)
        );
    }

    private PersonEntityResponse getPersonEntityResponseByPost(Post post) {
        Person author = post.getAuthor();
        return new PersonEntityResponse(
                author.getId(),
                author.getFirstName(),
                author.getLastName(),
                java.util.Date
                        .from(author.getRegDate().atZone(ZoneId.of("Europe/Moscow"))
                                .toInstant()).getTime(),
                java.util.Date
                        .from(author.getBirthDate().atZone(ZoneId.of("Europe/Moscow"))
                                .toInstant()).getTime(),
                author.getEmail(),
                author.getPhone(),
                author.getPhoto(),
                author.getAbout(),
                author.getCity(),
                author.getCountry(),
                author.getMessagePermission(),
                java.util.Date
                        .from(author.getLastOnlineTime().atZone(ZoneId.of("Europe/Moscow"))
                                .toInstant()).getTime(),
                author.getIsBlocked() == 1
        );
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
        return new CommentEntityResponse(
                comment.getParentId(),
                comment.getCommentText(),
                comment.getId(),
                comment.getPost().getId(),
                java.util.Date
                        .from(comment.getTime().atZone(ZoneId.of("Europe/Moscow"))
                                .toInstant()).getTime(),
                comment.getPerson().getId(),
                comment.getIsBlocked()
        );
    }

    private Long getTimeZonedMillis() {
        return java.util.Date
                .from(LocalDateTime.now().atZone(ZoneId.of("Europe/Moscow"))
                        .toInstant()).getTime();
    }

    private Long getMillis(LocalDateTime localDateTime) {
        return java.util.Date
                .from(localDateTime.atZone(ZoneId.of("Europe/Moscow"))
                        .toInstant()).getTime();
    }

    private List<PostEntityResponse> getPostEntityResponseListByPosts(List<Post> posts) {
        List<PostEntityResponse> postEntityResponseList = new ArrayList<>();
        for (Post post : posts) {
            postEntityResponseList.add(getPostEntityResponseByPost(post));
        }
        return postEntityResponseList;
    }

    private LocalDateTime getMillisecondsToLocalDateTime(long milliseconds) {
        LocalDateTime localDateTime =
                Instant.ofEpochMilli(milliseconds).atZone(ZoneId.of("Europe/Moscow")).toLocalDateTime();
        return localDateTime;

    }
}