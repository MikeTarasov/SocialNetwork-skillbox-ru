package ru.skillbox.socialnetwork.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.skillbox.socialnetwork.api.requests.EmailPassPassFirstNameLastNameCodeRequest;
import ru.skillbox.socialnetwork.api.requests.EmailRequest;
import ru.skillbox.socialnetwork.api.requests.NotificationTypeEnableRequest;
import ru.skillbox.socialnetwork.api.requests.TokenPasswordRequest;
import ru.skillbox.socialnetwork.api.responses.ErrorErrorDescriptionResponse;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeDataResponse;
import ru.skillbox.socialnetwork.api.responses.MessageResponse;
import ru.skillbox.socialnetwork.model.entity.Person;
import ru.skillbox.socialnetwork.repository.PersonRepository;

import java.util.Optional;

@Service
public class AccountService {

    private final PersonRepository personRepository;
    private final EmailSenderService emailSenderService;
    private final BCryptPasswordEncoder encoder;

    @Autowired
    public AccountService(PersonRepository personRepository,
                          EmailSenderService emailSenderService,
                          BCryptPasswordEncoder encoder) {
        this.personRepository = personRepository;
        this.emailSenderService = emailSenderService;
        this.encoder = encoder;
    }

    public Person getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            throw new SecurityException("Session is not authorized");
        }

        String email = auth.getName();

        Optional<Person> per = personRepository.findByEmail(email);

        if (per.isEmpty()) {
            throw new UsernameNotFoundException(" - User with : " + email + " not found");
        }

        return per.get();
    }

    public Optional<Person> findPersonByEmail(String email) {
        return personRepository.findByEmail(email);
    }

    private boolean isEmailCorrect(String email) {
        return email.toLowerCase()
                .replaceAll("(^([a-z0-9_\\.-]+)@([a-z0-9_\\.-]+)\\.([a-z\\.]{2,6})$)", "")
                .equals("");
    }

    private boolean isNameCorrect(String name) {
        return name.toLowerCase()
                .replaceAll("(^[a-z0-9-]+$)", "").equals("");
    }

    private boolean savePerson(EmailPassPassFirstNameLastNameCodeRequest requestBody) {
        personRepository.save(new Person(requestBody));
        return personRepository.findByEmail(requestBody.getEmail()).isPresent();
    }

    private void changePassword(Person person, String password) {
        person.setPassword(password);
        personRepository.save(person);
    }

    private void setConfirmationCode(Person person, String code) {
        person.setConfirmationCode(code);
        personRepository.save(person);
    }

    private void changeEmail(Person person, String email) {
        person.setEmail(email);
        person.setConfirmationCode("");
        personRepository.save(person);
    }

    public ResponseEntity<?> postApiAccountRegister(EmailPassPassFirstNameLastNameCodeRequest requestBody) {
        StringBuilder errors = new StringBuilder();

        String email = requestBody.getEmail();
        if (!isEmailCorrect(email)) {
            errors.append(" Wrong email! ");
        }
        if (findPersonByEmail(email).isPresent()) {
            errors.append(" This email is already registered! ");
        }
        if (!requestBody.getPasswd1().equals(requestBody.getPasswd2())) {
            errors.append(" Passwords not equals! ");
        }

        if (!isNameCorrect(requestBody.getFirstName()) ||
                !isNameCorrect(requestBody.getLastName())) {
            errors.append(" Firstname or last name is incorrect! ");
        }

        if (!errors.toString().equals("")) {
            return ResponseEntity.status(200).body(new ErrorErrorDescriptionResponse(errors.toString()));
        }

        if (savePerson(requestBody)) {
            return ResponseEntity.status(200)
                    .body(new ErrorTimeDataResponse("", new MessageResponse()));
        }

        return ResponseEntity.status(200).body(new ErrorErrorDescriptionResponse("Can't save user!"));
    }

    public ResponseEntity<?> putApiAccountPasswordRecovery(EmailRequest requestBody) {
        Optional<Person> optionalPerson = findPersonByEmail(requestBody.getEmail());

        if (optionalPerson.isEmpty()) {
            return ResponseEntity.status(200).body(new ErrorErrorDescriptionResponse("This email is not registered!"));
        }

        Person person = optionalPerson.get();
        String confirmationCode = encoder.encode(Long.toString(System.currentTimeMillis()))
                .substring(10).replaceAll("\\W", "");

        if (emailSenderService.sendEmailChangePassword(person, confirmationCode)) {
            setConfirmationCode(person, confirmationCode);
            return ResponseEntity.status(200)
                    .body(new ErrorTimeDataResponse("", new MessageResponse()));
        }

        return ResponseEntity.status(200).body(new ErrorErrorDescriptionResponse("Error sending email"));
    }

    public ResponseEntity<?> putApiAccountPasswordSet(@RequestBody TokenPasswordRequest requestBody) {

        Person person = getCurrentUser();

        if (!person.getConfirmationCode().equals(requestBody.getToken())) {
            return ResponseEntity.status(200).body(new ErrorErrorDescriptionResponse("Code is expired!"));
        }

        changePassword(person, requestBody.getPassword());

        return ResponseEntity.status(200)
                .body(new ErrorTimeDataResponse("", new MessageResponse()));
    }

    public ResponseEntity<?> putApiAccountEmail(@RequestBody EmailRequest requestBody) {

        changeEmail(getCurrentUser(), requestBody.getEmail());

        return ResponseEntity.status(200)
                .body(new ErrorTimeDataResponse("", new MessageResponse()));
    }

    public ResponseEntity<?> putApiAccountNotifications(
            @RequestBody NotificationTypeEnableRequest requestBody) {

        Person person = getCurrentUser();
        String notificationType = requestBody.getNotificationType();
        boolean enable = requestBody.isEnable();
        //TODO realize this method

        return ResponseEntity.status(200)
                .body(new ErrorTimeDataResponse("", new MessageResponse()));
    }
}