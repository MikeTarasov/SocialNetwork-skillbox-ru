package ru.skillbox.socialnetwork;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.skillbox.socialnetwork.api.requests.*;
import ru.skillbox.socialnetwork.controllers.DialogController;
import ru.skillbox.socialnetwork.model.entity.Dialog;
import ru.skillbox.socialnetwork.model.entity.Person;
import ru.skillbox.socialnetwork.model.entity.PersonToDialog;
import ru.skillbox.socialnetwork.repository.DialogRepository;
import ru.skillbox.socialnetwork.repository.PersonRepository;
import ru.skillbox.socialnetwork.repository.PersonToDialogRepository;
import ru.skillbox.socialnetwork.services.exceptions.DialogNotFoundException;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithUserDetails("shred@mail.who")
@TestPropertySource("/application-test.properties")
@Sql(value = {"/Add3Users.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/ClearDialogsAfterTest.sql", "/RemoveTestUsers.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class DialogControllerTests {

    private final long currentPersonId = 9L;  // shred@mail.who
    private final long secondId = 8L;
    @Autowired
    private DialogController dialogController;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DialogRepository dialogRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private PersonToDialogRepository personToDialogRepository;


    // intermediate 2 person dialog generator not to repeat
    Dialog generateDialogForTwo(Long firstId, Long secondId) throws Exception {
        List<Long> idList = new ArrayList<>();
        idList.add(firstId);
        idList.add(secondId);
        ListUserIdsRequest request = new ListUserIdsRequest(idList);
        MvcResult result = this.mockMvc.perform(post("/dialogs/").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.id").exists())
                .andReturn();
        // somehow casting Integer to Long is not a trivial thing
        long dialogId = Long.parseLong(JsonPath.read(result.getResponse().getContentAsString(), "$.data.id").toString());
        System.out.println("Generated dialog: " + dialogId);
        return dialogRepository.findById(dialogId).orElseThrow(() -> new DialogNotFoundException(dialogId));
    }

    @Test
    public void createDialogForOne() throws Exception {
        List<Long> idList = new ArrayList<>();
        idList.add(currentPersonId);
        ListUserIdsRequest request = new ListUserIdsRequest(idList);
        this.mockMvc.perform(post("/dialogs/").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.id").exists());

        Person currentPerson = personRepository.findById(currentPersonId).orElseThrow();
        assertTrue(dialogRepository.findByOwner(currentPerson).isPresent());
        Dialog dialog = dialogRepository.findByOwner(currentPerson).get();
        List<PersonToDialog> personToDialogConnections = personToDialogRepository.findByDialog(dialog);
        assertEquals(1, personToDialogConnections.size());
    }

    @Test
    public void createDialogForOne_Error() throws Exception {
        List<Long> idList = new ArrayList<>();
        idList.add(15L);
        ListUserIdsRequest request = new ListUserIdsRequest(idList);
        this.mockMvc.perform(post("/dialogs/").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("invalid_request"))
                .andExpect(jsonPath("$.data.id").doesNotExist());

        assertTrue(dialogRepository.findAll().isEmpty());
        assertTrue(personToDialogRepository.findAll().isEmpty());
    }

    @Test
    public void createDialogsForTwo() throws Exception {
        List<Long> idList = new ArrayList<>();
        idList.add(currentPersonId);
        idList.add(secondId);
        ListUserIdsRequest request = new ListUserIdsRequest(idList);
        this.mockMvc.perform(post("/dialogs/").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.id").exists());

        Person currentPerson = personRepository.findById(currentPersonId).orElseThrow();
        assertTrue(dialogRepository.findByOwner(currentPerson).isPresent());
        Dialog dialog = dialogRepository.findByOwner(currentPerson).get();
        assertEquals(2, personToDialogRepository.findByDialog(dialog).size());
        assertEquals(1, personToDialogRepository.findByPerson(currentPerson).size());
    }

    @Test
    public void createDialogsForThree() throws Exception {
        List<Long> idList = new ArrayList<>();
        idList.add(currentPersonId);
        Long thirdId = 7L;
        idList.add(secondId);
        idList.add(thirdId);
        ListUserIdsRequest request = new ListUserIdsRequest(idList);
        this.mockMvc.perform(post("/dialogs/").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.id").exists());

        Person currentPerson = personRepository.findById(currentPersonId).orElseThrow();
        assertTrue(dialogRepository.findByOwner(currentPerson).isPresent());
        Dialog dialog = dialogRepository.findByOwner(currentPerson).get();
        assertEquals(3, personToDialogRepository.findByDialog(dialog).size());
        assertEquals(1, personToDialogRepository.findByPerson(currentPerson).size());
        Person secondPerson = personRepository.findById(secondId).orElseThrow();
        assertEquals(1, personToDialogRepository.findByPerson(secondPerson).size());
        Person thirdPerson = personRepository.findById(thirdId).orElseThrow();
        assertEquals(1, personToDialogRepository.findByPerson(thirdPerson).size());
    }

    @Test
    public void createDialogsForTwo_Error() throws Exception {
        List<Long> idList = new ArrayList<>();
        idList.add(currentPersonId);
        Long secondId = 15L;
        idList.add(secondId);
        ListUserIdsRequest request = new ListUserIdsRequest(idList);
        this.mockMvc.perform(post("/dialogs/").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("invalid_request"))
                .andExpect(jsonPath("$.data.id").doesNotExist());

        assertTrue(dialogRepository.findAll().isEmpty());
        assertTrue(personToDialogRepository.findAll().isEmpty());
    }

    @Test
    public void createDialog_addTwo_removeTwo() throws Exception {
        List<Long> idList = new ArrayList<>();
        idList.add(currentPersonId);
        ListUserIdsRequest request = new ListUserIdsRequest(idList);

        List<Long> addRemoveList = new ArrayList<>();
        Long thirdId = 7L;
        addRemoveList.add(secondId);
        addRemoveList.add(thirdId);
        ListUserIdsRequest requestAddRemove = new ListUserIdsRequest(addRemoveList);

        // create 1 participant dialog
        this.mockMvc.perform(post("/dialogs/").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.id").exists());

        Person currentPerson = personRepository.findById(currentPersonId).orElseThrow();
        Dialog dialog = dialogRepository.findByOwner(currentPerson).get();
        Long dialogId = dialog.getId();

        // add 2 participants
        this.mockMvc.perform(put(String.format("/dialogs/%s/users", dialogId)).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestAddRemove)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.users_ids").exists());
        assertEquals(3, personToDialogRepository.findByDialog(dialog).size());

        // try to add user already in dialog

        this.mockMvc.perform(put(String.format("/dialogs/%s/users", dialogId)).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ListUserIdsRequest(List.of(thirdId)))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error_description")
                        .value(String.format("Person ID %d is already in dialog!", thirdId)));
        assertEquals(3, personToDialogRepository.findByDialog(dialog).size());

        // remove 2 participants
        this.mockMvc.perform(delete(String.format("/dialogs/%s/users", dialogId)).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestAddRemove)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.users_ids").exists());
        assertEquals(1, personToDialogRepository.findByDialog(dialog).size());
        assertEquals(currentPersonId, personToDialogRepository.findByDialog(dialog).get(0).getPerson().getId());
    }

    @Test
    public void getInviteLinkAndJoin() throws Exception {
        Long firstId = 7L;
        Dialog dialog = generateDialogForTwo(firstId, secondId);
        System.out.println("Generated dialog: " + dialog.getId());
        // getting invite link
        MvcResult resultGetInvite = this.mockMvc.perform(get(String.format("/dialogs/%d/users/invite", dialog.getId()))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.link").exists())
                .andReturn();
        String inviteCode = dialog.getInviteCode();
        String link = JsonPath.read(resultGetInvite.getResponse().getContentAsString(), "$.data.link");
        assertEquals(inviteCode, link);


        List<Long> currentUserInList = new ArrayList<>();
        currentUserInList.add(currentPersonId);
        // remove current user from dialog
        this.mockMvc.perform(delete(String.format("/dialogs/%s/users", dialog.getId())).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ListUserIdsRequest(currentUserInList))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.users_ids").exists());

        // joining by invite link
        LinkRequest linkRequest = new LinkRequest(link);
        MvcResult resultJoin = this.mockMvc.perform(put(String.format("/dialogs/%d/users/join", dialog.getId()))
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(linkRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.users_ids").exists())
                .andReturn();
        Long idInResponse = Long.valueOf(
                JsonPath.read(resultJoin.getResponse().getContentAsString(), "$.data.users_ids[0]").toString());
        assertEquals(currentPersonId, idInResponse);
    }

    @Test
    public void getDialogsEmptyRequest() throws Exception {
        // get all dialogs, no query/pages request
        for (int i = 0; i < 3; i++) {
            generateDialogForTwo(currentPersonId, secondId);
        }

        this.mockMvc.perform((get("/dialogs/")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.total").value("3"));
    }

    @Test
    public void getDialogsPagedRequest() throws Exception {
        // generate 6 dialogs
        for (int i = 0; i < 6; i++) {
            generateDialogForTwo(currentPersonId, secondId);
        }
        DialogRequest dialogRequest = new DialogRequest("", 2, 2);
        this.mockMvc.perform(get("/dialogs/").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dialogRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.total").value(6))
                .andExpect(jsonPath("$.data[*].id", containsInAnyOrder(12, 13)));

    }

    @Test
    @Sql(value = {"/Add3Users.sql", "/AddDialog.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/ClearDialogsAfterTest.sql", "/RemoveTestUsers.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getPersonActivityFalseTest() throws Exception {
        this.mockMvc.perform(get("/dialogs/1/activity/8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.online").value("false"))
                .andExpect(jsonPath("$.data.last_activity").value("1606939887000"));
    }

    @Test
    @Sql(value = {"/Add3Users.sql", "/AddDialog.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/ClearDialogsAfterTest.sql", "/RemoveTestUsers.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getPersonActivityTrueTest() throws Exception {
        this.mockMvc.perform(get("/dialogs/1/activity/9"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.online").value("true"))
                .andExpect(jsonPath("$.data.last_activity").value("1606936287000"));
    }

    @Test
    @Sql(value = {"/Add3Users.sql", "/AddDialog.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/ClearDialogsAfterTest.sql", "/RemoveTestUsers.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getPersonActivityWrongPersonTest() throws Exception {
        this.mockMvc.perform(get("/dialogs/1/activity/10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("invalid_request"))
                .andExpect(jsonPath("$.error_description").value("invalid person ID: 10"))
                .andExpect(jsonPath("$.data.online").doesNotExist());

    }

    @Test
    @Sql(value = {"/Add3Users.sql", "/AddDialog.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/ClearDialogsAfterTest.sql", "/RemoveTestUsers.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getPersonActivityWrongDialogTest() throws Exception {
        this.mockMvc.perform(get("/dialogs/2/activity/9"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("invalid_request"))
                .andExpect(jsonPath("$.error_description").value("invalid dialog ID: 2"))
                .andExpect(jsonPath("$.data.online").doesNotExist());

    }

    @Test
    @Sql(value = {"/Add3Users.sql", "/AddDialog.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/ClearDialogsAfterTest.sql", "/RemoveTestUsers.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void setPersonActivityTrueTest() throws Exception {
        this.mockMvc.perform(post("/dialogs/1/activity/9"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.message").value("ok"));
    }

    @Test
    @Sql(value = {"/Add3Users.sql", "/AddDialog.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/ClearDialogsAfterTest.sql", "/RemoveTestUsers.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void setPersonActivityWrongPersonTest() throws Exception {
        this.mockMvc.perform(post("/dialogs/1/activity/10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("invalid_request"))
                .andExpect(jsonPath("$.error_description").value("invalid person ID: 10"))
                .andExpect(jsonPath("$.data.message").doesNotExist());

    }

    @Test
    @Sql(value = {"/Add3Users.sql", "/AddDialog.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/ClearDialogsAfterTest.sql", "/RemoveTestUsers.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void setPersonActivityWrongDialogTest() throws Exception {
        this.mockMvc.perform(post("/dialogs/2/activity/9"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("invalid_request"))
                .andExpect(jsonPath("$.error_description").value("invalid dialog ID: 2"))
                .andExpect(jsonPath("$.data.message").doesNotExist());

    }

    @Test
    @Sql(value = {"/Add3Users.sql", "/AddDialog.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/ClearDialogsAfterTest.sql", "/RemoveTestUsers.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void deleteDialogTest() throws Exception {
        long dialogId = 1L;
        this.mockMvc.perform(delete("/dialogs/" + dialogId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.id").value(dialogId));
        assertTrue(dialogRepository.findById(dialogId).isEmpty());
    }

    @Test
    @Sql(value = {"/Add3Users.sql", "/AddDialog.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/ClearDialogsAfterTest.sql", "/RemoveTestUsers.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void deleteDialogWrongIdTest() throws Exception {
        long dialogId = 2L;
        this.mockMvc.perform(delete("/dialogs/" + dialogId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("invalid_request"))
                .andExpect(jsonPath("$.error_description").value("invalid dialog ID: " + dialogId));
    }

    @Test
    public void messageAllSuccess() throws Exception {
        Dialog dialog = generateDialogForTwo(currentPersonId, secondId);

        // send new message
        MessageTextRequest messageTextRequest = new MessageTextRequest();
        String testSendMessage = "test message from ID 9L to ID 8L";
        String testModifiedMessage = "MODIFIED message from ID 9L to ID 8L";
        messageTextRequest.setMessageText(testSendMessage);

        MvcResult resultSend = this.mockMvc.perform(post(String.format("/dialogs/%d/messages", dialog.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(messageTextRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.message_text").value(testSendMessage))
                .andExpect(jsonPath("$.data.read_status").value("SENT"))
                .andReturn();

        Long messageId = Long.valueOf(JsonPath.read
                (resultSend.getResponse().getContentAsString(), "$.data.id").toString());

        // modify message
        messageTextRequest.setMessageText(testModifiedMessage);
        this.mockMvc.perform(put(String.format("/dialogs/%d/messages/%d", dialog.getId(), messageId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(messageTextRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.message_text").value(testModifiedMessage))
                .andExpect(jsonPath("$.data.read_status").value("SENT"));

        // mark read
        this.mockMvc.perform(put(String.format("/dialogs/%d/messages/%d/read", dialog.getId(), messageId)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.message").value("ok"));

        // delete message
        this.mockMvc.perform(delete(String.format("/dialogs/%d/messages/%d", dialog.getId(), messageId)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.message_id").value(messageId));

        // restore message
        this.mockMvc.perform(put(String.format("/dialogs/%d/messages/%d/recover", dialog.getId(), messageId)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.id").value(messageId))
                .andExpect(jsonPath("$.data.message_text").value(testModifiedMessage))
        ;
    }

    @Test
    public void deleteMessageError() throws Exception {
        // try deleting message from non-existing dialog
        this.mockMvc.perform(delete(String.format("/dialogs/%d/messages/%d", 500, 999)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("invalid_request"))
                .andExpect(jsonPath("$.error_description").value("invalid dialog ID: 500"))
                .andReturn();
        // try deleting non-existing message from existing dialog
        Dialog dialog = generateDialogForTwo(currentPersonId, secondId);
        this.mockMvc.perform(delete(String.format("/dialogs/%d/messages/%d", dialog.getId(), 999)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("invalid_request"))
                .andExpect(jsonPath("$.error_description").value("invalid message ID: 999"))
                .andReturn();
    }

    @Test
    public void sendMessageError() throws Exception {
        Dialog dialog = generateDialogForTwo(currentPersonId, secondId);
        // try sending null message
        MessageTextRequest messageTextRequest = new MessageTextRequest();
        messageTextRequest.setMessageText(null);
        this.mockMvc.perform(post(String.format("/dialogs/%d/messages", dialog.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(messageTextRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("invalid_request"))
                .andExpect(jsonPath("$.error_description").value("Can't send empty message!"))
                .andReturn();
        // try sending empty message
        messageTextRequest.setMessageText("");
        this.mockMvc.perform(post(String.format("/dialogs/%d/messages", dialog.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(messageTextRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("invalid_request"))
                .andExpect(jsonPath("$.error_description").value("Can't send empty message!"))
                .andReturn();



        // try sending message to non-existing dialog
        this.mockMvc.perform(post(String.format("/dialogs/%d/messages", 500))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(messageTextRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("invalid_request"))
                .andExpect(jsonPath("$.error_description").value("invalid dialog ID: 500"))
                .andReturn();
    }

    @Test
    public void changeMessageError() throws Exception{
        Dialog dialog = generateDialogForTwo(currentPersonId, secondId);

        // send message
        MessageTextRequest messageTextRequest = new MessageTextRequest("Correct message!");
        MvcResult resultSend = this.mockMvc.perform(post(String.format("/dialogs/%d/messages", dialog.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(messageTextRequest)))
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.message_text").value("Correct message!"))
                .andExpect(jsonPath("$.data.read_status").value("SENT"))
                .andReturn();

        Long messageId = Long.valueOf(JsonPath.read
                (resultSend.getResponse().getContentAsString(), "$.data.id").toString());

        // null message
        messageTextRequest.setMessageText(null);
        this.mockMvc.perform(put(String.format("/dialogs/%d/messages/%d", dialog.getId(), messageId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(messageTextRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("invalid_request"))
                .andExpect(jsonPath("$.error_description").value("Can't set empty message! Message ID: " + messageId))
                .andReturn();

        // empty message
        messageTextRequest.setMessageText("");
        this.mockMvc.perform(put(String.format("/dialogs/%d/messages/%d", dialog.getId(), messageId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(messageTextRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("invalid_request"))
                .andExpect(jsonPath("$.error_description").value("Can't set empty message! Message ID: " + messageId))
                .andReturn();
    }

    @Test
    @Sql(value = {"/Add3Users.sql", "/Add3DialogsWithMessages.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/ClearDialogsAfterTest.sql", "/RemoveTestUsers.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getNewMessagesCount() throws Exception {
        this.mockMvc.perform(get("/dialogs/unreaded"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.count").value("5"));
    }

}
