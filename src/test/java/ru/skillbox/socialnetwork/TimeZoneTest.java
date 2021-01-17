package ru.skillbox.socialnetwork;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.skillbox.socialnetwork.model.entity.Post;
import ru.skillbox.socialnetwork.repository.PostRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class TimeZoneTest {

    @Autowired
    private PostRepository postRepository;

   /* @Test
    void testZone() {
        Date now = new Date();
        Long l = now.getTime();

        LocalDateTime date = LocalDateTime.now();
        assertEquals(java.util.Date
                .from(date.atZone(ZoneId.of("Europe/Moscow"))
                        .toInstant()).getTime(), l);
    }*/

    @Test
    void testGetById() {
        long id = 2;
        Optional<Post> post = postRepository.findByIdAndTimeIsBefore(id, LocalDateTime.now());
        assertTrue(post.isPresent());
        assertEquals(id, post.get().getId());
    }
}
