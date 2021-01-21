//package ru.skillbox.socialnetwork;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithUserDetails;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.context.jdbc.Sql;
//import org.springframework.test.web.servlet.MockMvc;
//import ru.skillbox.socialnetwork.api.requests.PersonEditRequest;
//import ru.skillbox.socialnetwork.api.requests.TitlePostTextRequest;
//import ru.skillbox.socialnetwork.controllers.ProfileController;
//import ru.skillbox.socialnetwork.model.entity.Person;
//import ru.skillbox.socialnetwork.model.entity.Post;
//import ru.skillbox.socialnetwork.model.enums.MessagesPermissions;
//import ru.skillbox.socialnetwork.repository.PersonRepository;
//import ru.skillbox.socialnetwork.repository.PostRepository;
//
//import java.util.TimeZone;
//
//import static org.hamcrest.Matchers.containsInAnyOrder;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@WithUserDetails("shred@mail.who")
//@TestPropertySource("/application-test.properties")
//@Sql(value = {"/AddTestUsers.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//@Sql(value = {"/RemoveTestUsers.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
//public class ProfileControllerTest {
//
//    private final long currentUserId = 9L;  // shred@mail.who
//    @Autowired
//    private ProfileController profileController;
//    @Autowired
//    private MockMvc mockMvc;
//    @Autowired
//    private ObjectMapper objectMapper;
//    @Autowired
//    private PostRepository postRepository;
//    @Autowired
//    private PersonRepository personRepository;
//
//    @Test
//    public void getUserTest() throws Exception {
//        this.mockMvc.perform(get("/users/8"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(authenticated())
//                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.error").value(""))
//                .andExpect(jsonPath("$.data.id").value("8"))
//                .andExpect(jsonPath("$.data.email").value("dedm@mail.who"))
//                .andExpect(jsonPath("$.data.phone").value("+888888888"))
//                .andExpect(jsonPath("$.data.photo").value("https://avatarko.ru/img/avatar/25/spinoj_Novyj_god_Ded_Moroz_Snegurochka_24185.jpg"))
//                .andExpect(jsonPath("$.data.about").value("Борода из ваты!"))
//                .andExpect(jsonPath("$.data.city").value("Великие Луки"))
//                .andExpect(jsonPath("$.data.country").value("Россия"))
//                .andExpect(jsonPath("$.data.first_name").value("Дед"))
//                .andExpect(jsonPath("$.data.reg_date").value("1599491415000"))
//                .andExpect(jsonPath("$.data.last_name").value("Мороз"))
//                .andExpect(jsonPath("$.data.birth_date").value("925502400000"))
//                .andExpect(jsonPath("$.data.messages_permission").value("ALL"))
//                .andExpect(jsonPath("$.data.last_online_time").value("1606939887000"))
//                .andExpect(jsonPath("$.data.is_blocked").value("false"));
//    }
//
//    @Test
//    public void getCurrentUserTest() throws Exception {
//        this.mockMvc.perform(get("/users/me"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(authenticated())
//                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.error").value(""))
//                .andExpect(jsonPath("$.data.id").value("9"))
//                .andExpect(jsonPath("$.data.email").value("shred@mail.who"))
//                .andExpect(jsonPath("$.data.phone").value("+999999999"))
//                .andExpect(jsonPath("$.data.photo").value("https://avatarko.ru/img/avatar/18/kot_multfilm_17215.jpg"))
//                .andExpect(jsonPath("$.data.about").value("мяу"))
//                .andExpect(jsonPath("$.data.city").value("Book"))
//                .andExpect(jsonPath("$.data.country").value("Wonderland"))
//                .andExpect(jsonPath("$.data.first_name").value("Котик"))
//                .andExpect(jsonPath("$.data.reg_date").value("1595434225000"))
//                .andExpect(jsonPath("$.data.last_name").value("Чеширский"))
//                .andExpect(jsonPath("$.data.birth_date").value("46299600000"))
//                .andExpect(jsonPath("$.data.messages_permission").value("ALL"))
//                .andExpect(jsonPath("$.data.last_online_time").value("1606936287000"))
//                .andExpect(jsonPath("$.data.is_blocked").value("false"));
//    }
//
//    @Test
//    public void deleteCurrentUserTest() throws Exception {
//        this.mockMvc.perform(delete("/users/me"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(authenticated());
//
//        assertTrue(personRepository.findById(currentUserId).isPresent());
//        assertEquals(1, personRepository.findById(currentUserId).get().getIsDeleted());
//    }
//
//    @Test
//    public void updateCurrentUserTest() throws Exception {
//        String firstName = "Donald";
//        String lastName = "Trump";
//        long birthDate = 1559751301818L;
//        String phone = "0000000";
//        String photo = "BLOB";
//        String about = "Make America great again";
//        String city = "Balashikha";
//        String country = "Russia";
//        MessagesPermissions permissions = MessagesPermissions.FRIENDS;
//        PersonEditRequest request = new PersonEditRequest(
//                firstName,
//                lastName,
//                birthDate,
//                phone,
//                photo,
//                about,
//                city,
//                country,
//                permissions
//        );
//
//        this.mockMvc.perform(put("/users/me")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(authenticated())
//                .andExpect(jsonPath("$.data.id").value(currentUserId))
//                .andExpect(jsonPath("$.data.birth_date").value(birthDate));
//
//        assertTrue(personRepository.findById(currentUserId).isPresent());
//        Person person = personRepository.findById(currentUserId).get();
//        assertEquals(firstName, person.getFirstName());
//        assertEquals(lastName, person.getLastName());
//        assertEquals(phone, person.getPhone());
//        assertEquals(photo, person.getPhoto());
//        assertEquals(about, person.getAbout());
//        assertEquals(city, person.getCity());
//        assertEquals(country, person.getCountry());
//        assertEquals(permissions, MessagesPermissions.valueOf(person.getMessagePermission()));
//    }
//
//    @Test
//    public void updateCurrentUserTest_null() throws Exception {
//        String firstName = null;
//        String lastName = null;
//        long birthDate = 0;
//        String phone = null;
//        String photo = null;
//        String about = null;
//        String city = null;
//        String country = null;
//        MessagesPermissions permissions = null;
//        PersonEditRequest request = new PersonEditRequest(
//                firstName,
//                lastName,
//                birthDate,
//                phone,
//                photo,
//                about,
//                city,
//                country,
//                permissions
//        );
//
//        Person expectedPerson = personRepository.findById(currentUserId).get();
//        this.mockMvc.perform(put("/users/me")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(authenticated())
//                .andExpect(jsonPath("$.data.id").value(currentUserId))
//                .andExpect(jsonPath("$.data.birth_date").value(expectedPerson.getBirthDate().atZone(TimeZone.getDefault().toZoneId()).toInstant().toEpochMilli()));
//
//        assertTrue(personRepository.findById(currentUserId).isPresent());
//        Person actualPerson = personRepository.findById(currentUserId).get();
//        assertEquals(expectedPerson.getFirstName(), actualPerson.getFirstName());
//        assertEquals(expectedPerson.getLastName(), actualPerson.getLastName());
//        assertEquals(expectedPerson.getPhone(), actualPerson.getPhone());
//        assertEquals(expectedPerson.getPhoto(), actualPerson.getPhoto());
//        assertEquals(expectedPerson.getAbout(), actualPerson.getAbout());
//        assertEquals(expectedPerson.getCity(), actualPerson.getCity());
//        assertEquals(expectedPerson.getCountry(), actualPerson.getCountry());
//        assertEquals(expectedPerson.getMessagePermission(), actualPerson.getMessagePermission());
//    }
//
//    @Test
//    public void userSearchByFirstNameTest() throws Exception {
//        this.mockMvc.perform(get("/users/search")
//                .queryParam("first_name", "Котик"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.error").value(""))
//                .andExpect(jsonPath("$.total").value("1"))
//                .andExpect(jsonPath("$.data[0].id").value("9"));
//
//    }
//
//    @Test
//    public void userSearchByLastNameTest() throws Exception {
//        this.mockMvc.perform(get("/users/search")
//                .queryParam("last_name", "Чеширский"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.error").value(""))
//                .andExpect(jsonPath("$.total").value("1"))
//                .andExpect(jsonPath("$.data[0].id").value("9"));
//
//    }
//
//    @Test
//    public void userSearchByAgeFromTest() throws Exception {
//        this.mockMvc.perform(get("/users/search")
//                .queryParam("age_from", "20"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.error").value(""))
//                .andExpect(jsonPath("$.total").value("2"))
//                .andExpect(jsonPath("$.data[*].id", containsInAnyOrder(8, 9)));
//    }
//
//    @Test
//    public void userSearchByAgeToTest() throws Exception {
//        this.mockMvc.perform(get("/users/search")
//                .queryParam("age_to", "30"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.error").value(""))
//                .andExpect(jsonPath("$.total").value("1"))
//                .andExpect(jsonPath("$.data[*].id", containsInAnyOrder(8)));
//    }
//
//    @Sql(value = {"/AddTestUsers.sql", "/AddPosts.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//    @Sql(value = {"/RemovePosts.sql", "/RemoveTestUsers.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
//    @Test
//    public void getNotesOnUserWallTest() throws Exception {
//        this.mockMvc.perform(get("/users/8/wall"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.error").value(""))
//                .andExpect(jsonPath("$.data[0].id").value("2"))
//                .andExpect(jsonPath("$.data[0].author.email").value("dedm@mail.who"))
//                .andExpect(jsonPath("$.data[1].id").value("1"))
//                .andExpect(jsonPath("$.data[1].author.email").value("dedm@mail.who"))
//                .andExpect(jsonPath("$.total").value("2"));
//    }
//
//    @Sql(value = {"/AddTestUsers.sql", "/RemovePosts.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//    @Sql(value = {"/RemovePosts.sql", "/RemoveTestUsers.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
//    @Test
//    public void postNoteOnUserWallTest() throws Exception {
//        TitlePostTextRequest request = new TitlePostTextRequest("TitleTest", "TextTest");
//        this.mockMvc.perform(post("/users/9/wall")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
//                .andDo(print())
//                .andExpect(status().isOk());
//
//        assertTrue(postRepository.findById(10L).isPresent());
//        Post post = postRepository.findById(10L).get();
//        assertEquals("TextTest", post.getPostText());
//        assertEquals("TitleTest", post.getTitle());
//        assertEquals("shred@mail.who", post.getAuthor().getEmail());
//    }
//
//
//    @Test
//    public void blockUserByIdTest() throws Exception {
//        long UserForBlockingId = 8L;
//        this.mockMvc.perform(put("/users/block/" + UserForBlockingId))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(authenticated())
//                .andExpect(jsonPath("$.data.message").value("ok"));
//
//        assertTrue(personRepository.findById(UserForBlockingId).isPresent());
//        assertEquals(1, personRepository.findById(UserForBlockingId).get().getIsBlocked());
//    }
//
//    @Test
//    public void unblockUserByIdTest() throws Exception {
//        long UserForUnblockingId = 8L;
//        this.mockMvc.perform(delete("/users/block/" + UserForUnblockingId))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(authenticated())
//                .andExpect(jsonPath("$.data.message").value("ok"));
//
//        assertTrue(personRepository.findById(UserForUnblockingId).isPresent());
//        assertEquals(0, personRepository.findById(UserForUnblockingId).get().getIsBlocked());
//    }
//
//    @Test
//    public void blockUserByIdTest_wrongId() throws Exception {
//        long UserForBlockingId = 7L;
//        this.mockMvc.perform(put("/users/block/" + UserForBlockingId))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(authenticated())
//                .andExpect(jsonPath("$.error").value("invalid_request"));
//
//    }
//}
