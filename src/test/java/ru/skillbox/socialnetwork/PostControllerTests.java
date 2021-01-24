package ru.skillbox.socialnetwork;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.skillbox.socialnetwork.api.requests.ParentIdCommentTextRequest;
import ru.skillbox.socialnetwork.api.requests.TitlePostTextRequest;
import ru.skillbox.socialnetwork.api.responses.*;
import ru.skillbox.socialnetwork.model.entity.Person;
import ru.skillbox.socialnetwork.model.entity.Post;
import ru.skillbox.socialnetwork.model.entity.PostComment;
import ru.skillbox.socialnetwork.model.entity.PostLike;
import ru.skillbox.socialnetwork.repository.CommentRepository;
import ru.skillbox.socialnetwork.repository.PersonRepository;
import ru.skillbox.socialnetwork.repository.PostLikeRepository;
import ru.skillbox.socialnetwork.repository.PostRepository;
import ru.skillbox.socialnetwork.security.JwtTokenProvider;

import javax.swing.border.TitledBorder;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerTests {

    private final String email = "test@test.gmail";
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final String password = "testPassword";
    private Post savedPost = null;
    private PostComment savedComment = null;

    private Person testPerson = new Person(0, "Steve", "Jobs",
            LocalDateTime.of(2020, 1, 1, 15, 30, 00),
            LocalDateTime.of(1982, 12, 31, 21, 00, 00),
            email, "+71234567890", encoder.encode(password), "pictures.org/photo.jpg",
            "smth about author", "Ufa", "Russian Federation", "some confirmation code",
            1, "ALL", LocalDateTime.of(2020, 5, 5, 5, 30, 00),
            0, 0, 0, new ArrayList<>(), new ArrayList<>());

    private Post testPost = new Post(0, LocalDateTime.of(2021, 1, 1, 15, 30, 00),
            testPerson, "Test post title", "Test post text", 0, 0, new ArrayList<>());

    private PostComment testPostComment = new PostComment(0, LocalDateTime.of(2021, 1,
            12, 20, 45, 25), 1L,
            "Good article!", 0, 0, testPerson, testPost);

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String auth() {
        return jwtTokenProvider.getAuthentication(email, password);
    }



    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private ObjectMapper objectMapper;


    @BeforeEach
    public void savePostToPostRepository() {
        savedPost = postRepository.save(testPost);
        testPostComment.setPerson(savedPost.getAuthor());
        testPostComment.setPost(savedPost);
        savedComment = commentRepository.save(testPostComment);
    }

    @AfterEach
    public void restoreDb() {
        commentRepository.delete(savedComment);
        postRepository.delete(savedPost);
        personRepository.delete(savedPost.getAuthor());
    }



    private List<Post> setPosts (Post post) {
        List<Post> list = new ArrayList<>();
        list.add(post);
        return list;
    }


    @Test
    public void started() {
    }

    @Test
    void testGetSome() {
        long idd = 2;
        Optional<Post> postTest = postRepository.findById(idd);
        assertTrue(postTest.isPresent());
        assertEquals(idd, postTest.get().getId());
    }

   @Test
    void testGetApiPostById() throws Exception {

       String jwtToken = auth();
       ErrorTimeDataResponse errorTimeDataResponse = new ErrorTimeDataResponse(
               "", getTimeZonedMillis(), getPostEntityResponseByPost(savedPost));

        MvcResult mvcResult = (MvcResult) mvc.perform(MockMvcRequestBuilders
                .get("/post/" + savedPost.getId()).header(HttpHeaders.AUTHORIZATION, jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(errorTimeDataResponse)));

    }

    @Test
    void testGetApiPostSearch() throws Exception {

        ErrorTimeTotalOffsetPerPageListDataResponse errorTimeTotalOffsetPerPageListDataResponse =
                new ErrorTimeTotalOffsetPerPageListDataResponse(
                        "",
                        System.currentTimeMillis(),
                        1,
                        0,
                        5,
                        getPostEntityResponseListByPosts(setPosts(savedPost))
                );

        String jwtToken = auth();
        long minusMonth = 1;
        MvcResult mvcResult = (MvcResult) mvc.perform(MockMvcRequestBuilders
                .get("/post/")
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .param("text", savedPost.getPostText())
                .param("date_from", getlMillis(savedPost.getTime().minusMonths(minusMonth)).toString())
                .param("date_to", getlMillis(LocalDateTime.now()).toString())
                .param("offset", String.valueOf(0))
                .param("itemPerPage", String.valueOf(5)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(errorTimeTotalOffsetPerPageListDataResponse)));

    }

    @Test
    void testPutPostById() throws Exception {

        String newTitle = "Updated post title";
        String newText = "Updated post text";

        TitlePostTextRequest titlePostTextRequest = new TitlePostTextRequest(
                newTitle,
                newText
        );

        savedPost.setTitle(newTitle);
        savedPost.setPostText(newText);

        ErrorTimeDataResponse errorTimeDataResponse = new ErrorTimeDataResponse(
                "", getTimeZonedMillis(), getPostEntityResponseByPost(savedPost));

        String jwtToken = auth();
        MvcResult mvcResult = (MvcResult) mvc.perform(MockMvcRequestBuilders
                .put("/post/{id}", String.valueOf(savedPost.getId()))
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .param("publish_date", getlMillis(savedPost.getTime()).toString())
                .content(objectMapper.writeValueAsString(titlePostTextRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(errorTimeDataResponse)));

    }

    @Test
    void testDeletePostById() throws Exception {

        ErrorTimeDataResponse errorTimeDataResponse = new ErrorTimeDataResponse(
                "", getTimeZonedMillis(), new IdResponse(savedPost.getId()));
        assertEquals(0, postRepository.findById(savedPost.getId()).get().getIsDeleted());

        String jwtToken = auth();
        MvcResult mvcResult = (MvcResult) mvc.perform(MockMvcRequestBuilders
                .delete("/post/{id}", savedPost.getId())
                .header(HttpHeaders.AUTHORIZATION, jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(errorTimeDataResponse)));
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
        MvcResult mvcResult = (MvcResult) mvc.perform(MockMvcRequestBuilders
                .put("/post/{id}/recover/", String.valueOf(savedPost.getId()))
                .header(HttpHeaders.AUTHORIZATION, jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(errorTimeDataResponse)));

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
        MvcResult mvcResult = (MvcResult) mvc.perform(MockMvcRequestBuilders
                .get("/post/{id}/comments", String.valueOf(savedPost.getId()))
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .param("offset", String.valueOf(0))
                .param("itemPerPage", String.valueOf(5)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(errorTimeTotalOffsetPerPageListDataResponse)));

    }

    @Test
    void testPostApiPostIdComments() throws Exception {

        ParentIdCommentTextRequest parentIdCommentTextRequest = new ParentIdCommentTextRequest(
                savedComment.getParentId(),
                savedComment.getCommentText()
        );

                ErrorTimeDataResponse errorTimeDataResponse = new ErrorTimeDataResponse(
                "", getTimeZonedMillis(), getCommentEntityResponseByComment(savedComment));

        String jwtToken = auth();
        MvcResult mvcResult = (MvcResult) mvc.perform(MockMvcRequestBuilders
                .post("/post/{id}/comments", String.valueOf(savedPost.getId()))
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .content(objectMapper.writeValueAsString(parentIdCommentTextRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(errorTimeDataResponse)));

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

    private Long getlMillis(LocalDateTime localDateTime) {
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
}
