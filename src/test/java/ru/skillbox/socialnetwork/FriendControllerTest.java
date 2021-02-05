package ru.skillbox.socialnetwork;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import ru.skillbox.socialnetwork.controllers.FriendController;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
public class FriendControllerTest {

    @Autowired
    private FriendController friendController;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithUserDetails("shred@mail.who")
    @Sql(value = {"/Add3Users.sql", "/AddFriendshipFor3.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/ClearFriendshipAfterTest.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getAllFriends1Test() throws Exception {
        this.mockMvc.perform(get("/friends/request")
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
        this.mockMvc.perform(get("/friends/request")
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
        this.mockMvc.perform(get("/friends/request")
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
        this.mockMvc.perform(get("/friends/request")
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
}
