package ru.skillbox.socialnetwork;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.skillbox.socialnetwork.api.requests.*;
import ru.skillbox.socialnetwork.model.entity.NotificationSettings;
import ru.skillbox.socialnetwork.model.entity.NotificationType;
import ru.skillbox.socialnetwork.model.entity.Person;
import ru.skillbox.socialnetwork.repository.NotificationSettingsRepository;
import ru.skillbox.socialnetwork.repository.NotificationTypeRepository;
import ru.skillbox.socialnetwork.repository.PersonRepository;
import ru.skillbox.socialnetwork.security.JwtTokenProvider;
import ru.skillbox.socialnetwork.services.AccountService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@TestPropertySource("/application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = SocialNetworkApplication.class)
@AutoConfigureMockMvc
public class AccountControllerTests {

    private final String email = "test@test.gmail";
    private final String password = "testPassword";
    private final String firstName = "testFirstName";
    private final String lastName = "testLastName";
    private final String token = "testTokentestTokentestTokentestTokentestTokentestTokentestTokentestTokentestToken";
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final Person testPerson = new Person(email, encoder.encode(password), firstName, lastName, LocalDateTime.now());

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private NotificationSettingsRepository notificationSettingsRepository;

    @Autowired
    private NotificationTypeRepository notificationTypeRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountService accountService;

    private void delete(String email) {
        Optional<Person> optionalPerson = personRepository.findByEmail(email);
        optionalPerson.ifPresent(person -> personRepository.delete(person));
    }

    private void deleteOptional(boolean isPresent) {
        Optional<Person> optionalPerson = personRepository.findByEmail(email);
        if (isPresent) assertTrue(optionalPerson.isPresent());
        optionalPerson.ifPresent(person -> personRepository.delete(person));
    }

    private void save(String email, Person person) {
        delete(email);
        if (personRepository.findByEmail(email).isEmpty()) personRepository.save(person);
    }

    private String auth() {
        return jwtTokenProvider.getAuthentication(email, password);
    }

    private void clearContext() {
        SecurityContextHolder.clearContext();
    }


    private void expectOK(ResultActions resultActions) throws Exception {
        resultActions
                .andExpect(status().is(200))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data.message").value("ok"));
    }

    private void expectError(ResultActions resultActions, String description) throws Exception {
        resultActions
                .andExpect(status().is(400))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("invalid_request"))
                .andExpect(jsonPath("$.error_description").value(description));
    }


    @Test
    void testPostApiAccountRegister_200() throws Exception {
        delete(email);

        EmailPassPassFirstNameLastNameCodeRequest request = new EmailPassPassFirstNameLastNameCodeRequest(
                email, password, password, firstName, lastName, "1234");

        expectOK(mvc.perform(MockMvcRequestBuilders
                .post("/account/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))));

        assertTrue(personRepository.findByEmail(email).isPresent());

        deleteOptional(true);
    }

    @Test
    void testPostApiAccountRegister_WrongPass() throws Exception {
        delete(email);

        EmailPassPassFirstNameLastNameCodeRequest request = new EmailPassPassFirstNameLastNameCodeRequest(
                email, password, "password", firstName, lastName, "1234");

        expectError(mvc.perform(MockMvcRequestBuilders
                .post("/account/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))), " Passwords not equals! ");

        assertFalse(personRepository.findByEmail(email).isPresent());

        deleteOptional(false);
    }

    @Test
    void testPostApiAccountRegister_WrongEmail() throws Exception {
        delete(email);

        EmailPassPassFirstNameLastNameCodeRequest request = new EmailPassPassFirstNameLastNameCodeRequest(
                "email", password, password, firstName, lastName, "1234");

        expectError(mvc.perform(MockMvcRequestBuilders
                .post("/account/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))), " Wrong email! ");

        assertFalse(personRepository.findByEmail(email).isPresent());

        deleteOptional(false);
    }

    @Test
    void testPostApiAccountRegister_WrongName() throws Exception {
        delete(email);

        EmailPassPassFirstNameLastNameCodeRequest request = new EmailPassPassFirstNameLastNameCodeRequest(
                email, password, password, "firstName*/-+-/*", lastName, "1234");

        expectError(mvc.perform(MockMvcRequestBuilders
                .post("/account/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))), " Firstname or last name is incorrect! ");

        assertFalse(personRepository.findByEmail(email).isPresent());

        deleteOptional(false);
    }

    @Test
    void testPostApiAccountRegister_EmailIsRegistered() throws Exception {
        save(email, testPerson);

        EmailPassPassFirstNameLastNameCodeRequest request = new EmailPassPassFirstNameLastNameCodeRequest(
                email, password, password, firstName, lastName, "1234");

        expectError(mvc.perform(MockMvcRequestBuilders
                .post("/account/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))), " This email is already registered! ");

        deleteOptional(false);
    }

    @Test
    void testPutApiAccountPasswordRecovery_Security_200() throws Exception {
        String email1 = "test@gmail.com"; //TODO insert real email for test!
        Person test = new Person(email1, password, firstName, lastName, LocalDateTime.now());
        save(email1, test);

        EmailRequest request = new EmailRequest(email1);

        expectOK(mvc.perform(MockMvcRequestBuilders
                .put("/account/password/recovery")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))));

        delete(email1);
    }

    @Test
    void testPutApiAccountPasswordRecovery_EmailNotRegistered() throws Exception {
        delete(email);

        EmailRequest request = new EmailRequest(email);

        expectError(mvc.perform(MockMvcRequestBuilders
                .put("/account/password/recovery")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))), "This email is not registered!");

        delete(email);
    }

    @Test
    void testPutApiAccountPasswordSet_Security_OK() throws Exception {
        delete(email);
        testPerson.setConfirmationCode(token);
        personRepository.save(testPerson);
        String jwtToken = auth();
        TokenPasswordRequest requestBody = new TokenPasswordRequest(jwtToken, new Password(password));

        expectOK(mvc.perform(MockMvcRequestBuilders
                .put("/account/password/set")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .content(objectMapper.writeValueAsString(requestBody))));

        Person person = personRepository.findByEmail(email).orElse(new Person());
        assertTrue(encoder.matches((password), person.getPassword()));
        assertNull(person.getConfirmationCode());

        delete(email);
        clearContext();
    }

    @Test
    void testPutApiAccountPasswordSet_Security_401() throws Exception {
        TokenPasswordRequest requestBody = new TokenPasswordRequest(token, new Password(password + 1));

        mvc.perform(MockMvcRequestBuilders
                .put("/account/password/set")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))

                .andExpect(status().is(401));
    }

    @Test
    void testPutApiAccountPasswordSet_BadToken() throws Exception {
        save(email, testPerson);
        String jwtToken = auth();

        TokenPasswordRequest requestBody = new TokenPasswordRequest(null, new Password(password));

        expectError(mvc.perform(MockMvcRequestBuilders
                .put("/account/password/set")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .content(objectMapper.writeValueAsString(requestBody))), "Bad token!");

        Person person = personRepository.findByEmail(email).orElse(new Person());
        assertTrue(encoder.matches(password, person.getPassword()));

        delete(email);
        clearContext();
    }

    @Test
    void testPutApiAccountEmail_Security_OK() throws Exception {
        save(email, testPerson);
        String jwtToken = auth();

        EmailRequest requestBody = new EmailRequest(email);

        expectOK(mvc.perform(MockMvcRequestBuilders
                .put("/account/email")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .content(objectMapper.writeValueAsString(requestBody))));

        Optional<Person> person = personRepository.findByEmail(email);
        assertTrue(person.isPresent());

        clearContext();
        delete(email);
    }

    @Test
    void testPutApiAccountEmail_Security_401() throws Exception {
        EmailRequest requestBody = new EmailRequest(1 + email);

        mvc.perform(MockMvcRequestBuilders
                .put("/account/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))

                .andExpect(status().is(401));
    }

    @Test
    void testPutApiAccountEmail_WrongEmail() throws Exception {
        save(email, testPerson);
        String jwtToken = auth();

        EmailRequest request = new EmailRequest(email + 1);

        expectError(mvc.perform(MockMvcRequestBuilders
                .put("/account/email")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .content(objectMapper.writeValueAsString(request))), "Email is not valid!");

        Optional<Person> person = personRepository.findByEmail(email);
        assertTrue(person.isPresent());

        clearContext();
        delete(email);
    }

    @Test
    @Sql(value = {"/AddNotificationTypes.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/RemoveNotificationTypes.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testPutApiAccountNotifications200() throws Exception {
        save(email, testPerson);
        String jwtToken = auth();
        Optional<Person> personOptional = personRepository.findByEmail(email);
        assertTrue(personOptional.isPresent());
        Person person = personOptional.get();
        accountService.setDefaultNotifySettings(person);

        NotificationTypeEnableRequest request = new NotificationTypeEnableRequest("POST", false);

        expectOK(mvc.perform(MockMvcRequestBuilders
                .put("/account/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .content(objectMapper.writeValueAsString(request))));

        Optional<NotificationType> notificationType = notificationTypeRepository.findByName("POST");

        assertTrue(notificationType.isPresent());
        NotificationSettings notificationSetting =
                notificationSettingsRepository.findByPersonNSAndNotificationType(person, notificationType.get());
        assertFalse(notificationSetting.getIsEnable());

        clearContext();
        delete(email);
    }

    @Test
    void testPutApiAccountNotifications400() throws Exception {
        save(email, testPerson);
        String jwtToken = auth();

        NotificationTypeEnableRequest request = new NotificationTypeEnableRequest("test", true);

        expectError(mvc.perform(MockMvcRequestBuilders
                .put("/account/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .content(objectMapper.writeValueAsString(request))), "Wrong notification type");

        clearContext();
        delete(email);
    }

    @Test
    void testGetApiAccountNotifications200() throws Exception {
        save(email, testPerson);
        String jwtToken = auth();
        Optional<Person> personOptional = personRepository.findByEmail(email);
        assertTrue(personOptional.isPresent());
        Person person = personOptional.get();
        accountService.setDefaultNotifySettings(person);

        mvc.perform(MockMvcRequestBuilders
                .get("/account/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken))

                .andExpect(status().is(200))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.data").isArray());

        clearContext();
        delete(email);
    }
}