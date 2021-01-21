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
    Dialog generateDialogForTwo(Long secondId) throws Exception {
        List<Long> idList = new ArrayList<>();
        idList.add(currentPersonId);
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
    public void createDialog_addTwo() throws Exception {
        List<Long> idList = new ArrayList<>();
        idList.add(currentPersonId);
        ListUserIdsRequest request = new ListUserIdsRequest(idList);

        List<Long> idsToAdd = new ArrayList<>();
        Long secondId = 7L;
        Long thirdId = 8L;
        idsToAdd.add(secondId);
        idsToAdd.add(thirdId);
        ListUserIdsRequest requestAdd = new ListUserIdsRequest(idsToAdd);

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

        this.mockMvc.perform(put(String.format("/dialogs/%s/users", dialogId)).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestAdd)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.user_ids").exists());
        assertEquals(3, personToDialogRepository.findByDialog(dialog).size());
    }

    @Test
    public void getInviteLink() throws Exception{
        Long secondId = 8L;
        Dialog dialog = generateDialogForTwo(secondId);
        MvcResult result = this.mockMvc.perform(get(String.format("/dialogs/%d/users/invite", dialog.getId()))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.link").exists())
                .andReturn();
        String inviteCode = dialog.getInviteCode();
        String link = JsonPath.read(result.getResponse().getContentAsString(), "$.data.link");
        assertEquals(inviteCode, link);
    }
}
