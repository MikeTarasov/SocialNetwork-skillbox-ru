package ru.skillbox.socialnetwork;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeTotalOffsetPerPageListDataResponse;
import ru.skillbox.socialnetwork.api.responses.NotificationBaseResponse;
import ru.skillbox.socialnetwork.model.entity.Friendship;
import ru.skillbox.socialnetwork.model.entity.Notification;
import ru.skillbox.socialnetwork.model.entity.Person;
import ru.skillbox.socialnetwork.repository.FriendshipRepository;
import ru.skillbox.socialnetwork.repository.NotificationsRepository;
import ru.skillbox.socialnetwork.security.PersonDetailsService;
import ru.skillbox.socialnetwork.services.NotificationsService;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
class NotificationsControllerTest {

    private final int offset = 0;
    private final int itemPerPage = 20;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private NotificationsRepository notificationsRepository;
    @Autowired
    private PersonDetailsService personDetailsService;
    @Autowired
    private FriendshipRepository friendshipRepository;
    @Autowired
    private NotificationsService notificationsService;


    @Test
    @WithUserDetails("dedm@mail.who")
    @Sql(value = {"/AddNotificationsForTestGet.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/ClearNotificationsForTestGet.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testGetNotification() throws Exception {

        Person person = personDetailsService.getCurrentUser();

        List<NotificationBaseResponse> d = notificationsService.convertToNotificationResponse(
                notificationsRepository
                        .findByPersonNotificationAndIsRead(person, 0,
                                PageRequest.of(offset, itemPerPage)), person);

        ErrorTimeTotalOffsetPerPageListDataResponse response = new ErrorTimeTotalOffsetPerPageListDataResponse(
                "",
                System.currentTimeMillis(),
                notificationsRepository.countByPersonNotificationAndIsRead(person, 0),
                offset,
                itemPerPage,
                d
        );

        mvc.perform(MockMvcRequestBuilders
                .get("/notifications/")
                .param("offset", String.valueOf(0))
                .param("itemPerPage", String.valueOf(20)))

                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.total")
                        .value(String.valueOf(response.getTotal())))
                .andExpect(jsonPath("$.offset")
                        .value(String.valueOf(response.getOffset())))
                .andExpect(jsonPath("$.perPage")
                        .value(String.valueOf(response.getPerPage())))
                .andExpect(jsonPath("$.data[:1].type_id")
                        .value(3));
    }

    @Test
    @WithUserDetails("dedm@mail.who")
    @Sql(value = {"/AddNotificationsForTestGet.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/ClearNotificationsForTestGet.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void checkQuery() {
        Person person = personDetailsService.getCurrentUser();

        List<Friendship> friends = friendshipRepository.findByDstPersonOrSrcPerson(person, person);

        Assertions.assertEquals(1, friends.size());
    }

    @Test
    @WithUserDetails("dedm@mail.who")
    @Sql(value = {"/AddNotificationsForTestGet.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/ClearNotificationsForTestGet.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void checkQueryForPut() {
        Person person = personDetailsService.getCurrentUser();

        List<Notification> list = notificationsRepository.findByPersonNotificationAndIsRead(person, 0, null);
        for (Notification notification : list) {
            notificationsService.setIsRead(notification.getId());
        }

        Assertions.assertEquals(notificationsRepository.findById(list.get(0).getId()).get().getIsRead(), 1);
    }


    @Test
    @WithUserDetails("dedm@mail.who")
    @Sql(value = {"/AddNotificationsForTestGet.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/ClearNotificationsForTestGet.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void checkBirthday() {
        List<Person> friends = new ArrayList<>();
        Person me = personDetailsService.getCurrentUser();
        List<Friendship> getFriends = friendshipRepository.findByDstPersonOrSrcPerson(me, me);
        if (!getFriends.isEmpty()) {
            for (Friendship friend : getFriends) {
                if (friend.getSrcPerson().getEmail().equals(me.getEmail())
                        && friend.getDstPerson().getBirthDate().getDayOfMonth() == 21
                        && friend.getDstPerson().getBirthDate().getMonthValue() == 6) {
                    friends.add(friend.getDstPerson());
                } else if (friend.getDstPerson().getEmail().equals(me.getEmail())
                        && friend.getSrcPerson().getBirthDate().getDayOfMonth() == 21
                        && friend.getSrcPerson().getBirthDate().getMonthValue() == 6) {
                    friends.add(friend.getDstPerson());
                }
            }
        }
        Assertions.assertEquals(1, friends.size());
    }
}