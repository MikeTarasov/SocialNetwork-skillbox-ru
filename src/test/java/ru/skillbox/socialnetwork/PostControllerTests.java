package ru.skillbox.socialnetwork;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.skillbox.socialnetwork.api.responses.CommentEntityResponse;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeDataResponse;
import ru.skillbox.socialnetwork.api.responses.PersonEntityResponse;
import ru.skillbox.socialnetwork.api.responses.PostEntityResponse;
import ru.skillbox.socialnetwork.controllers.PostController;
import ru.skillbox.socialnetwork.model.entity.Person;
import ru.skillbox.socialnetwork.model.entity.Post;
import ru.skillbox.socialnetwork.model.entity.PostComment;
import ru.skillbox.socialnetwork.model.entity.PostLike;
import ru.skillbox.socialnetwork.repository.CommentRepository;
import ru.skillbox.socialnetwork.repository.PersonRepository;
import ru.skillbox.socialnetwork.repository.PostLikeRepository;
import ru.skillbox.socialnetwork.repository.PostRepository;
import ru.skillbox.socialnetwork.security.JwtTokenProvider;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class PostControllerTests {

    String timezone = "Europe/Moscow";

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final String email = "diesel-z@yandex.ru";
    private final String password = "dsd";
    private final long postId = 125;
    private final long authorId = 125;
    private final long postLikeId = 125;
    private final long postCommentId = 125;
    private final LocalDateTime time = LocalDateTime.now();
    private final Person author = new Person( authorId,
            "andrew", "larkin", LocalDateTime.of(2020, 1,
            12, 20, 40, 25),
            LocalDateTime.of(2021, 1,
                    12, 20, 40, 25),
            email, "712345678914",  encoder.encode(password),"44","Moscow", "Russia", "dd", "sss", 1, "dsd",
            LocalDateTime.of(2021, 1,
                    12, 20, 40, 25),
            1, 0, 0, new ArrayList<>(), new ArrayList<>());

    private final String title = "LinkedList vs. ArrayList";
    private final String postText = "In this article, we'll provide a comparative view of three popular technologies of Java";
    private final int isBlocked = 0;
    private final int isDeleted = 0;

    private final List<PostComment> comments = new ArrayList<>();

    private Post testPost = new Post(postId, time, author, title, postText,
            isBlocked, isDeleted, comments);
    private PostLike postLike = new PostLike(postLikeId, LocalDateTime.of(2021, 1,
            12, 20, 45, 25), authorId, postId);

    private final PostComment postComment = new PostComment(postCommentId, LocalDateTime.of(2021, 1,
            12, 20, 45, 25), 0L,
            "Good article!", 0, 0, author, testPost);



    @Autowired
    private MockMvc mvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String auth() {
        return jwtTokenProvider.getAuthentication(email, password);
    }

    /*@InjectMocks
    private PostController postController;*/

   /* @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(postController).build();
    }*/

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


    /*long idd = 1;
    Optional<Post> post = postRepository.findById(idd);
*/

    private void save() {
        if (postRepository.findByTitle(title).isEmpty()) {
            postRepository.save(testPost);
            commentRepository.save(postComment);
            personRepository.save(author);
            //postLikeRepository.save(postLike);
        }
    }

    private void delete() {
        if (postRepository.findByTitle(title).isPresent()) postRepository.delete(testPost);
        if (commentRepository.findById(postCommentId).isPresent()) commentRepository.delete(postComment);
        if (personRepository.findById(authorId).isPresent()) personRepository.delete(author);
        //if (postLikeRepository.findById(postLikeId).isPresent()) postLikeRepository.delete(postLike);
    }
    ErrorTimeDataResponse errorTimeDataResponse = new ErrorTimeDataResponse(
            "", System.currentTimeMillis(), getPostEntityResponseByPost(testPost));

    @Test
    void testGetSome() {
        long idd = 2;
        Optional<Post> postTest = postRepository.findById(idd);
        assertTrue(postTest.isPresent());
        assertEquals(idd, postTest.get().getId());
    }

   /* private void delete() {
        if (postRepository.findById(postId).isPresent()) postRepository.delete(testPost);
    }

    private void save() {
        delete();
        if (postRepository.findById(postId).isEmpty()) postRepository.save(testPost);
    }

    private void deleteOptional(boolean isPresent) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (isPresent) assertTrue(optionalPost.isPresent());
        optionalPost.ifPresent(post -> postRepository.delete(post));
    }

    private void expectOK(ResultActions resultActions) throws Exception {
        resultActions
                .andExpect(status().is(200))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.message").value("ok"));
    }*/


   @Test
    void testGetApiPostById() throws Exception {

       delete();
       save();
       String jwtToken = auth();

        mvc.perform(MockMvcRequestBuilders.get("/post/1").header(HttpHeaders.AUTHORIZATION, jwtToken))
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

        commentEntityResponseList.add(getCommentEntityResponseByComment(postComment));
        return commentEntityResponseList;
    }

   /* private List<CommentEntityResponse> getCommentEntitiResponseListByPost(Post post, Pageable pageable) {
        List<CommentEntityResponse> commentEntityResponseList = new ArrayList<>();
        List<PostComment> comments = commentRepository.getCommentsByPostId(post.getId(), pageable);
        for (PostComment comment : comments) {
            commentEntityResponseList.add(getCommentEntityResponseByComment(comment));
        }
        return commentEntityResponseList;
    }*/

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


}
