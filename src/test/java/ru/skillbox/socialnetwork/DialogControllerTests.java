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
import ru.skillbox.socialnetwork.api.requests.LinkRequest;
import ru.skillbox.socialnetwork.api.requests.ListUserIdsRequest;
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
@Sql(value = {"/AddUsersForDialogs.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/ClearDialogsAfterTest.sql","/RemoveTestUsers.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class DialogControllerTests {

    private final long currentPersonId = 9L;  // shred@mail.who
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
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.id").exists())
                .andReturn();
        // somehow casting Integer to Long is not a trivial thing
        Long dialogId = Long.valueOf(JsonPath.read(result.getResponse().getContentAsString(), "$.data.id").toString());
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
        Long secondId = 8L;
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
        Long secondId = 8L;
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
        Long secondId = 7L;
        Long thirdId = 8L;
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
                .andExpect(jsonPath("$.data.user_ids").exists());
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
                .andExpect(jsonPath("$.data.user_ids").exists());
        assertEquals(1, personToDialogRepository.findByDialog(dialog).size());
        assertEquals(currentPersonId, personToDialogRepository.findByDialog(dialog).get(0).getPerson().getId());
    }

    @Test
    public void getInviteLinkAndJoin() throws Exception{
        Long firstId = 7L;
        Long secondId = 8L;
        Dialog dialog = generateDialogForTwo(firstId, secondId);
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

        // joining by invite link
        LinkRequest linkRequest = new LinkRequest(link);
        MvcResult resultJoin = this.mockMvc.perform(put(String.format("/dialogs/%d/users/join", dialog.getId()))
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(linkRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.user_ids").exists())
                .andReturn()
                ;
        Long idInResponse = Long.valueOf(
                JsonPath.read(resultJoin.getResponse().getContentAsString(), "$.data.user_ids[0]").toString());
        assertEquals(currentPersonId, idInResponse);
    }

    @Test
    public void  getDialogs() throws Exception{
        // all dialogs, no query/pages etc
        Long secondId = 8L;
        Dialog dialog = generateDialogForTwo(currentPersonId, secondId);
        MvcResult result = this.mockMvc.perform((get("/dialogs/")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andReturn();
        Long resultDialogId = Long.valueOf(
                JsonPath.read(result.getResponse().getContentAsString(), "$.data[0].id").toString());
        assertEquals(dialog.getId(), resultDialogId);
    }
}
