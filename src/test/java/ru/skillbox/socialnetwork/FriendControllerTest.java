package ru.skillbox.socialnetwork;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import ru.skillbox.socialnetwork.api.requests.ListUserIdsRequest;
import ru.skillbox.socialnetwork.controllers.FriendController;
import ru.skillbox.socialnetwork.model.entity.Person;
import ru.skillbox.socialnetwork.model.enums.FriendStatus;
import ru.skillbox.socialnetwork.repository.FriendshipRepository;
import ru.skillbox.socialnetwork.repository.PersonRepository;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
public class FriendControllerTest {

    @Autowired
    private FriendController friendController;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private FriendshipRepository friendshipRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithUserDetails("shred@mail.who")
    @Sql(value = {"/Add3Users.sql", "/AddFriendshipFor3.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/ClearFriendshipAfterTest.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getAllFriends1Test() throws Exception {
        this.mockMvc.perform(get("/friends")
                .queryParam("offset", "0")
                .queryParam("itemPerPage","10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.total").value("2"))
                .andExpect(jsonPath("$.offset").value("0"))
                .andExpect(jsonPath("$.perPage").value("10"))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].id").value("8"))
                .andExpect(jsonPath("$.data[1].id").value("7"))
                .andExpect(jsonPath("$.data[0].first_name").value("Дед"))
                .andExpect(jsonPath("$.data[1].first_name").value("Гном"))
                .andExpect(jsonPath("$.data[0].email").value("dedm@mail.who"))
                .andExpect(jsonPath("$.data[1].email").value("gnom@mail.who"))
                .andExpect(jsonPath("$.data[0].phone").value("+888888888"))
                .andExpect(jsonPath("$.data[1].phone").value("+7777777777"))
                .andExpect(jsonPath("$.data[0].photo").value("https://avatarko.ru/img/avatar/25/spinoj_Novyj_god_Ded_Moroz_Snegurochka_24185.jpg"))
                .andExpect(jsonPath("$.data[1].photo").value("https://avatarko.ru/img/avatar/28/gnom_27031.jpg"))
                .andExpect(jsonPath("$.data[0].about").value("Борода из ваты!"))
                .andExpect(jsonPath("$.data[1].about").value("Йо-хо-хо"))
                .andExpect(jsonPath("$.data[0].city").value("Великие Луки"))
                .andExpect(jsonPath("$.data[1].city").value("Мория"))
                .andExpect(jsonPath("$.data[0].country").value("Россия"))
                .andExpect(jsonPath("$.data[1].country").value("Средиземье"))
                .andExpect(jsonPath("$.data[0].last_name").value("Мороз"))
                .andExpect(jsonPath("$.data[1].last_name").value("Садовый"))
                .andExpect(jsonPath("$.data[0].reg_date").value("1599491415000"))
                .andExpect(jsonPath("$.data[1].reg_date").value("1585819211000"))
                .andExpect(jsonPath("$.data[0].birth_date").value("925502400000"))
                .andExpect(jsonPath("$.data[1].birth_date").value("504910800000"))
                .andExpect(jsonPath("$.data[0].messages_permission").value("ALL"))
                .andExpect(jsonPath("$.data[1].messages_permission").value("ALL"))
                .andExpect(jsonPath("$.data[0].last_online_time").value("1606939887000"))
                .andExpect(jsonPath("$.data[1].last_online_time").value("1599077427000"))
                .andExpect(jsonPath("$.data[0].is_blocked").value("false"))
                .andExpect(jsonPath("$.data[1].is_blocked").value("false"));
    }

    @Test
    @WithUserDetails("dedm@mail.who")
    @Sql(value = {"/Add3Users.sql", "/AddFriendshipFor3.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/ClearFriendshipAfterTest.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getAllFriends2Test() throws Exception {
        this.mockMvc.perform(get("/friends")
                .queryParam("offset", "0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.total").value("1"))
                .andExpect(jsonPath("$.offset").value("0"))
                .andExpect(jsonPath("$.perPage").value("20"))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id").value("9"));
    }

    @Test
    @WithUserDetails("shred@mail.who")
    @Sql(value = {"/Add3Users.sql", "/AddFriendshipFor3.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/ClearFriendshipAfterTest.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getAllFriendsPagesTest() throws Exception {
        this.mockMvc.perform(get("/friends")
                .queryParam("offset", "0")
                .queryParam("itemPerPage", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.total").value("2"))
                .andExpect(jsonPath("$.offset").value("0"))
                .andExpect(jsonPath("$.perPage").value("1"))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id").value("8"));
    }

    @Test
    @WithUserDetails("shred@mail.who")
    @Sql(value = {"/Add3Users.sql", "/AddFriendshipFor3.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/ClearFriendshipAfterTest.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getFriendsQueryTest() throws Exception {
        this.mockMvc.perform(get("/friends")
                .queryParam("name", "дед")
                .queryParam("offset", "0")
                .queryParam("itemPerPage", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.total").value("1"))
                .andExpect(jsonPath("$.offset").value("0"))
                .andExpect(jsonPath("$.perPage").value("10"))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id").value("8"));
    }

    @Test
    @WithUserDetails("shred@mail.who")
    @Sql(value = {"/Add4UsersForRequestTest.sql", "/AddFriendshipRequestsFor4.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/ClearFriendshipAfterTest.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getAllRequestsTest() throws Exception {
        this.mockMvc.perform(get("/friends/request")
                .queryParam("offset", "0")
                .queryParam("itemPerPage", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.total").value("2"))
                .andExpect(jsonPath("$.offset").value("0"))
                .andExpect(jsonPath("$.perPage").value("10"))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].id").value("6"))
                .andExpect(jsonPath("$.data[1].id").value("8"));
    }

    @Test
    @WithUserDetails("shred@mail.who")
    @Sql(value = {"/Add4UsersForRequestTest.sql", "/AddFriendshipRequestsFor4.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/ClearFriendshipAfterTest.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getRequestsQueryTest() throws Exception {
        this.mockMvc.perform(get("/friends/request")
                .queryParam("name", "ELON")
                .queryParam("offset", "0")
                .queryParam("itemPerPage", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.total").value("1"))
                .andExpect(jsonPath("$.offset").value("0"))
                .andExpect(jsonPath("$.perPage").value("10"))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id").value("6"));
    }

    /**
     * create friend request
     */
    @Test
    @WithUserDetails("shred@mail.who")
    @Sql(value = {"/Add3Users.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/ClearFriendshipAfterTest.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void addFriend1Test() throws Exception {
        Long dstPersonId = 7L;
        this.mockMvc.perform(post("/friends/" + dstPersonId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.message").value("ok"));

        Person currentPerson = personRepository.findByEmail("shred@mail.who").get();
        Person dstPerson = personRepository.findById(dstPersonId).get();
        assertTrue(friendshipRepository.findByDstPersonAndSrcPerson(dstPerson, currentPerson).isPresent());
        assertEquals(friendshipRepository.findByDstPersonAndSrcPerson(dstPerson, currentPerson).get()
                .getCode(), FriendStatus.REQUEST.name());
    }

    /**
     * allow friend request
     */
    @Test
    @WithUserDetails("shred@mail.who")
    @Sql(value = {"/Add4UsersForRequestTest.sql", "/AddFriendshipRequestsFor4.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/ClearFriendshipAfterTest.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void addFriend2Test() throws Exception {
        Long dstPersonId = 8L;
        this.mockMvc.perform(post("/friends/" + dstPersonId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.message").value("ok"));

        Person currentPerson = personRepository.findByEmail("shred@mail.who").get();
        Person dstPerson = personRepository.findById(dstPersonId).get();
        assertTrue(friendshipRepository.findByDstPersonAndSrcPerson(dstPerson, currentPerson).isPresent());
        assertTrue(friendshipRepository.findByDstPersonAndSrcPerson(currentPerson, dstPerson).isPresent());
        assertEquals(friendshipRepository.findByDstPersonAndSrcPerson(dstPerson, currentPerson).get()
                .getCode(), FriendStatus.FRIEND.name());
        assertEquals(friendshipRepository.findByDstPersonAndSrcPerson(currentPerson, dstPerson).get()
                .getCode(), FriendStatus.FRIEND.name());
    }

    /**
     * in case you were already declined
     */
    @Test
    @WithUserDetails("shred@mail.who")
    @Sql(value = {"/Add3Users.sql", "/AddFriendshipDeclinedAndBlocked.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/ClearFriendshipAfterTest.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void addFriend3Test() throws Exception {
        Long dstPersonId = 7L;
        this.mockMvc.perform(post("/friends/" + dstPersonId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.message").value("ok"));

        Person currentPerson = personRepository.findByEmail("shred@mail.who").get();
        Person dstPerson = personRepository.findById(dstPersonId).get();
        assertTrue(friendshipRepository.findByDstPersonAndSrcPerson(dstPerson, currentPerson).isPresent());
        assertTrue(friendshipRepository.findByDstPersonAndSrcPerson(currentPerson, dstPerson).isPresent());
        assertEquals(friendshipRepository.findByDstPersonAndSrcPerson(dstPerson, currentPerson).get()
                .getCode(), FriendStatus.SUBSCRIBED.name());
        assertEquals(friendshipRepository.findByDstPersonAndSrcPerson(currentPerson, dstPerson).get()
                .getCode(), FriendStatus.DECLINED.name());
    }

    /**
     * in case you were blocked
     */
    @Test
    @WithUserDetails("shred@mail.who")
    @Sql(value = {"/Add3Users.sql", "/AddFriendshipDeclinedAndBlocked.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/ClearFriendshipAfterTest.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void addFriend4Test() throws Exception {
        Long dstPersonId = 8L;
        this.mockMvc.perform(post("/friends/" + dstPersonId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.message").value("ok"));

        Person currentPerson = personRepository.findByEmail("shred@mail.who").get();
        Person dstPerson = personRepository.findById(dstPersonId).get();
        assertTrue(friendshipRepository.findByDstPersonAndSrcPerson(dstPerson, currentPerson).isEmpty());
        assertTrue(friendshipRepository.findByDstPersonAndSrcPerson(currentPerson, dstPerson).isPresent());
        assertEquals(friendshipRepository.findByDstPersonAndSrcPerson(currentPerson, dstPerson).get()
                .getCode(), FriendStatus.BLOCKED.name());
    }

    /**
     * create friend request when another request already exists
     */
    @Test
    @WithUserDetails("dedm@mail.who")
    @Sql(value = {"/Add4UsersForRequestTest.sql", "/AddFriendshipRequestsFor4.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/ClearFriendshipAfterTest.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void addFriend5Test() throws Exception {
        Long dstPersonId = 9L;
        this.mockMvc.perform(post("/friends/" + dstPersonId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.message").value("ok"));

        Person currentPerson = personRepository.findByEmail("dedm@mail.who").get();
        Person dstPerson = personRepository.findById(dstPersonId).get();
        assertTrue(friendshipRepository.findByDstPersonAndSrcPerson(dstPerson, currentPerson).isPresent());
        assertEquals(friendshipRepository.findByDstPersonAndSrcPerson(dstPerson, currentPerson).get()
                .getCode(), FriendStatus.REQUEST.name());
        assertEquals(friendshipRepository.countByDstPersonAndSrcPerson(dstPerson, currentPerson), 1);
        assertEquals(friendshipRepository.count(), 5);
    }

    /**
     * delete friend request
     */
    @Test
    @WithUserDetails("dedm@mail.who")
    @Sql(value = {"/Add4UsersForRequestTest.sql", "/AddFriendshipRequestsFor4.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/ClearFriendshipAfterTest.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void deleteFriendRequestTest() throws Exception {
        Long dstPersonId = 9L;
        this.mockMvc.perform(delete("/friends/" + dstPersonId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.message").value("ok"));

        Person currentPerson = personRepository.findByEmail("dedm@mail.who").get();
        Person dstPerson = personRepository.findById(dstPersonId).get();
        assertTrue(friendshipRepository.findByDstPersonAndSrcPerson(dstPerson, currentPerson).isEmpty());
        assertTrue(friendshipRepository.findByDstPersonAndSrcPerson(currentPerson, dstPerson).isEmpty());
    }

    /**
     * broke friendship
     */
    @Test
    @WithUserDetails("shred@mail.who")
    @Sql(value = {"/Add4UsersForRequestTest.sql", "/AddFriendshipRequestsFor4.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/ClearFriendshipAfterTest.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void deleteFriendTest() throws Exception {
        Long dstPersonId = 7L;
        this.mockMvc.perform(delete("/friends/" + dstPersonId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.message").value("ok"));

        Person currentPerson = personRepository.findByEmail("shred@mail.who").get();
        Person dstPerson = personRepository.findById(dstPersonId).get();
        assertTrue(friendshipRepository.findByDstPersonAndSrcPerson(dstPerson, currentPerson).isPresent());
        assertTrue(friendshipRepository.findByDstPersonAndSrcPerson(currentPerson, dstPerson).isPresent());
        assertEquals(friendshipRepository.findByDstPersonAndSrcPerson(dstPerson, currentPerson).get()
                .getCode(), FriendStatus.DECLINED.name());
        assertEquals(friendshipRepository.findByDstPersonAndSrcPerson(currentPerson, dstPerson).get()
                .getCode(), FriendStatus.SUBSCRIBED.name());
    }

    /**
     * wrong id
     */
    @Test
    @WithUserDetails("shred@mail.who")
    @Sql(value = {"/Add4UsersForRequestTest.sql", "/AddFriendshipRequestsFor4.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/ClearFriendshipAfterTest.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void deleteFriendErrorTest() throws Exception {
        Long dstPersonId = 5L;
        this.mockMvc.perform(delete("/friends/" + dstPersonId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("invalid_request"))
                .andExpect(jsonPath("$.error_description").isNotEmpty());

    }

    @Test
    @WithUserDetails("dedm@mail.who")
    @Sql(value = {"/Add3Users.sql", "/AddFriendshipFor3.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/ClearFriendshipAfterTest.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void isFriendTest() throws Exception {
        List<Long> list = new ArrayList<Long>();
        list.add(7L);
        list.add(8L);
        list.add(9L);
        list.add(10L);
        ListUserIdsRequest request = new ListUserIdsRequest(list);

        this.mockMvc.perform(post("/is/friends").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].user_id").value("7"))
                .andExpect(jsonPath("$.data[0].status").value("DECLINED"))
                .andExpect(jsonPath("$.data[1].user_id").value("9"))
                .andExpect(jsonPath("$.data[1].status").value("FRIEND"));

    }

    @Test
    @WithUserDetails("shred@mail.who")
    @Sql(value = {"/Add7Users.sql", "/AddFriendshipFor7.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/ClearFriendshipAfterTest.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getRecommendationsTest() throws Exception {
        this.mockMvc.perform(get("/friends/recommendations")
                .queryParam("offset", "0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.total").value("2"))
                .andExpect(jsonPath("$.offset").value("0"))
                .andExpect(jsonPath("$.perPage").value("20"))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].id").value("5"))
                .andExpect(jsonPath("$.data[1].id").value("4"));
    }

    @Test
    @WithUserDetails("kvakru@mail.who")
    @Sql(value = {"/Add7Users.sql", "/AddFriendshipFor7.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/ClearFriendshipAfterTest.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getRecommendationsNoFriendsTest() throws Exception {
        this.mockMvc.perform(get("/friends/recommendations")
                .queryParam("offset", "0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.total").value("5"))
                .andExpect(jsonPath("$.offset").value("0"))
                .andExpect(jsonPath("$.perPage").value("20"))
                .andExpect(jsonPath("$.data", hasSize(5)))
                .andExpect(jsonPath("$.data[0].id").value("6"))
                .andExpect(jsonPath("$.data[1].id").value("5"))
                .andExpect(jsonPath("$.data[2].id").value("4"))
                .andExpect(jsonPath("$.data[3].id").value("8"))
                .andExpect(jsonPath("$.data[4].id").value("9"));
    }
}
