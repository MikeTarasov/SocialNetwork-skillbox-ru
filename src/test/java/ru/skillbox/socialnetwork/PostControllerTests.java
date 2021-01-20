package ru.skillbox.socialnetwork;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
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

import java.time.LocalDateTime;
import java.time.ZoneId;
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

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final String email = "diesel-z@yandex.ru";
    private final String password = "dsd";
    private final long postId = 35;
    private final long authorId = 49;
    private final long postLikeId = 125;
    private final long postCommentId = 12;
    private final LocalDateTime time = LocalDateTime.of(2021, 1, 17, 23, 38, 12, 195177000);
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
            12, 20, 45, 25), null,
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
            //postRepository.save(testPost);
            commentRepository.save(postComment);
            //personRepository.save(author);
            //postLikeRepository.save(postLike);
        }
    }

    private void delete() {
        if (postRepository.findByTitle(title).isPresent()) postRepository.delete(testPost);
        if (commentRepository.findByCommentText(postComment.getCommentText()).isPresent()) commentRepository.delete(postComment);
        if (personRepository.findByEmail(email).isPresent()) personRepository.delete(author);
        //if (postLikeRepository.findById(postLikeId).isPresent()) postLikeRepository.delete(postLike);
    }
    ErrorTimeDataResponse errorTimeDataResponse = new ErrorTimeDataResponse(
            "", getTimeZonedMillis(), getPostEntityResponseByPost(testPost));

    private List<Post> setPosts (Post post) {
        List<Post> list = new ArrayList<>();
        list.add(post);
        return list;
    }

    ErrorTimeTotalOffsetPerPageListDataResponse errorTimeTotalOffsetPerPageListDataResponse =
            new ErrorTimeTotalOffsetPerPageListDataResponse(
                    "",
                    System.currentTimeMillis(),
                    1,
                    0,
                    5,
                    getPostEntityResponseListByPosts(setPosts(testPost))
            );
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


        MvcResult mvcResult = (MvcResult) mvc.perform(MockMvcRequestBuilders.get("/post/" + postId).header(HttpHeaders.AUTHORIZATION, jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(errorTimeDataResponse)));



    }

    @Test
    void testGetApiPostSearch() throws Exception {

        String jwtToken = auth();
        long minusMonth = 1;
        MvcResult mvcResult = (MvcResult) mvc.perform(MockMvcRequestBuilders
                .get("/post/")
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .param("text", postText)
                .param("date_from", getlMillis(time.minusMonths(minusMonth)).toString())
                .param("date_to", getlMillis(LocalDateTime.now()).toString())
                .param("offset", String.valueOf(0))
                .param("itemPerPage", String.valueOf(5)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(errorTimeTotalOffsetPerPageListDataResponse)));

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
