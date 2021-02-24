package ru.skillbox.socialnetwork;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.skillbox.socialnetwork.model.entity.*;
import ru.skillbox.socialnetwork.repository.*;
import ru.skillbox.socialnetwork.security.JwtTokenProvider;
import ru.skillbox.socialnetwork.security.PersonDetailsService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
class NotificationsControllerTest {

    private int offset = 0;
    private int itemPerPage = 20;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private NotificationsRepository notificationsRepository;
    @Autowired
    private NotificationTypeRepository notificationTypeRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private PersonDetailsService personDetailsService;
    @Autowired
    private NotificationSettingsRepository notificationSettingsRepository;
    @Autowired
    private PostCommentRepository postCommentRepository;
    @Autowired
    private FriendshipRepository friendshipRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @WithUserDetails("dedm@mail.who")
    @Sql(value = {"/AddNotificationsForTestGet.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/ClearNotificationsForTestGet.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testGetNotification() throws Exception {

        Person person = personDetailsService.getCurrentUser();

        List<NotificationBaseResponse> d = convertToNotificationResponse(
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
                    setIsRead(notification.getId());
                }

        Assertions.assertEquals(notificationsRepository.findById(list.get(0).getId()).get().getIsRead(), 1);
    }

    private List<NotificationBaseResponse> convertToNotificationResponse(List<Notification> notifications, Person person) {
        List<NotificationBaseResponse> result = new ArrayList<>();

        List<NotificationSettings> notificationSettingsList = notificationSettingsRepository
                .findByPersonNSAndEnable(person, 1);
        List<Long> enabledSettings = notificationSettingsList.stream()
                .map(ns -> ns.getNotificationType().getId())
                .collect(Collectors.toList());

        for (Notification notification : notifications) {

            long typeId = notification.getType().getId();
            if (enabledSettings.contains(typeId)) {

                long entityId = notification.getEntityId();
                String info = "";

                switch ((int) notification.getType().getId()) {
                    case 2:

                    case 3:
                        Optional<PostComment> commentToPostOptional = postCommentRepository.findById(entityId);
                        if (commentToPostOptional.isEmpty()) break;
                        PostComment commentToPost = commentToPostOptional.get();
                        info = "New Comment '".concat(commentToPost.getCommentText().substring(0, 6))
                                .concat("...' from user ").concat(personRepository.findById(commentToPost.getPerson()
                                        .getId()).get().getFirstName());
                        break;
                    case 4:
                        Optional<Friendship> friendRequestOptional = friendshipRepository.findById(entityId);
                        if (friendRequestOptional.isEmpty()) break;
                        Friendship friendRequest = friendRequestOptional.get();
                        info = "User ".concat(personRepository.findById(friendRequest.getSrcPerson().getId())
                                .get().getFirstName().concat(" ")
                                .concat(personRepository.findById(friendRequest.getSrcPerson().getId())
                                        .get().getLastName()).concat(" offers friendship"));
                        break;
                    case 5:
                        Optional<Message> optionalMessage = messageRepository.findById(entityId);
                        if (optionalMessage.isEmpty()) break;
                        Message message = optionalMessage.get();
                        info = "New message '".concat(message.getText().substring(0, 2))
                                .concat(" from user ")
                                .concat(personRepository.findById(message.getAuthor().getId()).get().getFirstName());
                        break;
                    case 6:
                        if (!person.equals(personRepository.findById(8L).get())) {
                            info = "User ".concat(personRepository.findById(8L).get().getFirstName())
                                    .concat(" ")
                                    .concat(personRepository.findById(8L).get().getLastName())
                                    .concat(" celebrates his/her birthday!");
                        }
                        break;
                }
                result.add(new NotificationBaseResponse(
                        notification.getId(),
                        typeId,
                        notification.getTimeStamp(),
                        notification.getEntityId(),
                        info));
            }
        }
        return result;
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
    private boolean setIsRead(long id) {
        Optional<Notification> optionalNotification = notificationsRepository.findById(id);
        if (optionalNotification.isEmpty()) {
            return false;
        }
        Notification notification = optionalNotification.get();
        notification.setIsRead(1);
        notificationsRepository.save(notification);
        return true;
    }

    }


