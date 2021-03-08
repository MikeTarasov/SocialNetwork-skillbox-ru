package ru.skillbox.socialnetwork;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.skillbox.socialnetwork.config.security.PersonDetailsService;
import ru.skillbox.socialnetwork.repositories.FriendshipRepository;
import ru.skillbox.socialnetwork.repositories.NotificationsRepository;
import ru.skillbox.socialnetwork.services.NotificationsService;

@SpringBootTest
@AutoConfigureMockMvc
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


//    @Test
//    @WithUserDetails("dedm@mail.who")
//    @Sql(value = {"/AddNotificationsForTestGet.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//    @Sql(value = {"/ClearNotificationsForTestGet.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
//    void testGetNotification() throws Exception {
//
//        mvc.perform(MockMvcRequestBuilders
//                .get("/notifications/")
//                .param("offset", String.valueOf(0))
//                .param("itemPerPage", String.valueOf(20)))
//
//                .andExpect(authenticated())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.error").value(""))
//                .andExpect(jsonPath("$.total").value(String.valueOf(3)))
//                .andExpect(jsonPath("$.offset").value(String.valueOf(0)))
//                .andExpect(jsonPath("$.perPage").value(String.valueOf(20)))
//                .andExpect(jsonPath("$.data[:1].type_id").value(3))
//                .andExpect(jsonPath("$.data[1].event_type").value("FRIEND_REQUEST"))
//                .andExpect(jsonPath("$.data[0].event_type").value("COMMENT_COMMENT"));
//    }

//    @Test
//    @WithUserDetails("dedm@mail.who")
//    @Sql(value = {"/AddNotificationsForTestGet.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//    @Sql(value = {"/ClearNotificationsForTestGet.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
//    void checkQuery() {
//        Person person = personDetailsService.getCurrentUser();
//
//        List<Friendship> friends = friendshipRepository.findByDstPersonOrSrcPerson(person, person);
//
//        Assertions.assertEquals(1, friends.size());
//    }

//    @Test
//    @WithUserDetails("dedm@mail.who")
//    @Sql(value = {"/AddNotificationsForTestGet.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//    @Sql(value = {"/ClearNotificationsForTestGet.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
//    void checkQueryForPut() {
//        Person person = personDetailsService.getCurrentUser();
//
//        List<Notification> list = notificationsRepository.findByPersonNotificationAndIsRead(person, 0, null);
//        for (Notification notification : list) {
//            notificationsService.setIsRead(notification.getId());
//        }
//
//        Assertions.assertEquals(notificationsRepository.findById(list.get(0).getId()).get().getIsRead(), 1);
//    }


//    @Test
//    @WithUserDetails("dedm@mail.who")
//    @Sql(value = {"/AddNotificationsForTestGet.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//    @Sql(value = {"/ClearNotificationsForTestGet.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
//    void checkBirthday() {
//        List<Person> friends = new ArrayList<>();
//        Person me = personDetailsService.getCurrentUser();
//        List<Friendship> getFriends = friendshipRepository.findByDstPersonOrSrcPerson(me, me);
//        if (!getFriends.isEmpty()) {
//            for (Friendship friend : getFriends) {
//                if (friend.getSrcPerson().getEmail().equals(me.getEmail())
//                        && friend.getDstPerson().getBirthDate().getDayOfMonth() == 21
//                        && friend.getDstPerson().getBirthDate().getMonthValue() == 6) {
//                    friends.add(friend.getDstPerson());
//                } else if (friend.getDstPerson().getEmail().equals(me.getEmail())
//                        && friend.getSrcPerson().getBirthDate().getDayOfMonth() == 21
//                        && friend.getSrcPerson().getBirthDate().getMonthValue() == 6) {
//                    friends.add(friend.getDstPerson());
//                }
//            }
//        }
//        Assertions.assertEquals(1, friends.size());
//    }
}