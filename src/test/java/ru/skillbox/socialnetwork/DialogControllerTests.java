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
import ru.skillbox.socialnetwork.controllers.DialogController;
import ru.skillbox.socialnetwork.model.entity.Dialog;
import ru.skillbox.socialnetwork.model.entity.Person;
import ru.skillbox.socialnetwork.repository.DialogRepository;
import ru.skillbox.socialnetwork.repository.PersonRepository;
import ru.skillbox.socialnetwork.repository.PersonToDialogRepository;
import ru.skillbox.socialnetwork.services.exceptions.PersonNotFoundException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithUserDetails("shred@mail.who")
@TestPropertySource("/application-test.properties")
@Sql(value = {"/AddUsersForDialogs.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/ClearDialogsAfterTest.sql","/RemoveTestUsers.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class DialogControllerTests {

    private final long currentUserId = 9L;  // shred@mail.who
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

    @Test
    public void createOneDialog() throws Exception {
        List<Long> idList = new ArrayList<>();
        idList.add(currentUserId);
        ListUserIdsRequest request = new ListUserIdsRequest(idList);
        this.mockMvc.perform(post("/api/v1/dialogs/").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.id").exists());

        List<Dialog> dialogs = dialogRepository.findByOwner(personRepository.findById(currentUserId)
                .orElseThrow(() -> new PersonNotFoundException(currentUserId)));
        assertEquals(1, dialogs.size());
        Person currentUser = personRepository.findById(currentUserId)
                .orElseThrow(() -> new PersonNotFoundException(currentUserId));
        assertTrue(personToDialogRepository.findByPerson(currentUser).isPresent());
        assertTrue(dialogs.contains(personToDialogRepository.findByPerson(currentUser).get().getDialog()));
    }

    @Test
    public void createTwoDialogs() throws Exception {
        List<Long> idList = new ArrayList<>();
        idList.add(currentUserId);
        Long secondId = 8L;
        idList.add(secondId);
        ListUserIdsRequest request = new ListUserIdsRequest(idList);
        this.mockMvc.perform(post("/api/v1/dialogs/").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.id").exists());

        Person currentUser = personRepository.findById(currentUserId)
                .orElseThrow(() -> new PersonNotFoundException(currentUserId));
        Person secondUser = personRepository.findById(secondId)
                .orElseThrow(() -> new PersonNotFoundException(secondId));
        List<Dialog> dialogs = dialogRepository.findByOwner(personRepository.findById(currentUserId)
                .orElseThrow(() -> new PersonNotFoundException(currentUserId)));
        assertEquals(2, dialogs.size());
        assertTrue(personToDialogRepository.findByPerson(currentUser).isPresent());
        assertTrue(personToDialogRepository.findByPerson(secondUser).isPresent());
        assertTrue(dialogs.contains(personToDialogRepository.findByPerson(currentUser).get().getDialog()));
        assertTrue(dialogs.contains(personToDialogRepository.findByPerson(secondUser).get().getDialog()));
    }

    @Test
    public void createTwoDialogsError() throws Exception {
        List<Long> idList = new ArrayList<>();
        idList.add(currentUserId);
        Long secondId = 15L;
        idList.add(secondId);
        ListUserIdsRequest request = new ListUserIdsRequest(idList);
        this.mockMvc.perform(post("/api/v1/dialogs/").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("invalid_request"))
                .andExpect(jsonPath("$.data.id").doesNotExist());

        List<Dialog> dialogs = dialogRepository.findByOwner(personRepository.findById(currentUserId)
                                                                .orElseThrow(() -> new PersonNotFoundException(currentUserId)));
        assertEquals(0, dialogs.size());
    }
}
