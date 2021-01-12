package ru.skillbox.socialnetwork;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.skillbox.socialnetwork.model.entity.Person;
import ru.skillbox.socialnetwork.model.entity.Post;
import ru.skillbox.socialnetwork.model.entity.PostComment;
import ru.skillbox.socialnetwork.repository.PostRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = SocialNetworkApplication.class)
@AutoConfigureMockMvc
public class PostControllerTests {

    private final long postId = 7;
    private final LocalDateTime time = LocalDateTime.now();
    private final Person author = new Person("diesel-z@yandex.ru", "1456",
            "andrew", "larkin", LocalDateTime.of(2021, 1,
            12, 20, 40, 25));
    private final String title = "LinkedList vs. ArrayList";
    private final String postText = "In this article, we'll provide a comparative view of three popular technologies of Java";
    private final int isBlocked = 0;
    private final int isDeleted = 0;

    private final List<PostComment> comments = new ArrayList<>();

    private Post testPost = new Post(postId, time, author, title, postText,
            isBlocked, isDeleted, comments);

    private final PostComment postComment = new PostComment(10, LocalDateTime.of(2021, 1,
            12, 20, 45, 25), 0L,
            "Good article!", 0, 0, author, testPost);

    @Autowired
    MockMvc mvc;

    @Autowired
    WebApplicationContext wac;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ObjectMapper objectMapper;


    private void delete() {
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
    }

    @BeforeTestMethod
    public void setup() {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.wac).dispatchOptions(true).build();
    }

    @Test
    void testGetPostById_OK() throws Exception {
        assertTrue(postRepository.findById(5L).isPresent());

    }


}
