package ru.skillbox.socialnetwork.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.api.requests.EmailPassPassFirstNameLastNameCodeRequest;
import ru.skillbox.socialnetwork.api.requests.EmailRequest;
import ru.skillbox.socialnetwork.api.requests.NotificationTypeEnableRequest;
import ru.skillbox.socialnetwork.api.requests.TokenPasswordRequest;
import ru.skillbox.socialnetwork.api.responses.*;
import ru.skillbox.socialnetwork.model.entity.NotificationSettings;
import ru.skillbox.socialnetwork.model.entity.NotificationType;
import ru.skillbox.socialnetwork.model.entity.Person;
import ru.skillbox.socialnetwork.repository.NotificationSettingsRepository;
import ru.skillbox.socialnetwork.repository.NotificationTypeRepository;
import ru.skillbox.socialnetwork.repository.PersonRepository;
import ru.skillbox.socialnetwork.security.PersonDetailsService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private final PersonRepository personRepository;
    private final NotificationSettingsRepository notificationSettingsRepository;
    private final NotificationTypeRepository notificationTypeRepository;
    private final EmailSenderService emailSenderService;
    private final BCryptPasswordEncoder encoder;
    private final PersonDetailsService personDetailsService;

    @Autowired
    public AccountService(PersonRepository personRepository,
                          NotificationSettingsRepository notificationSettingsRepository,
                          NotificationTypeRepository notificationTypeRepository,
                          EmailSenderService emailSenderService,
                          BCryptPasswordEncoder encoder, PersonDetailsService personDetailsService) {
        this.personRepository = personRepository;
        this.notificationSettingsRepository = notificationSettingsRepository;
        this.notificationTypeRepository = notificationTypeRepository;
        this.emailSenderService = emailSenderService;
        this.encoder = encoder;
        this.personDetailsService = personDetailsService;
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
                .replaceAll("(^[a-zа-яё0-9-]+$)", "").equals("");
    }

    private boolean savePerson(EmailPassPassFirstNameLastNameCodeRequest requestBody) {
        personRepository.save(new Person(
                requestBody.getEmail(),
                encoder.encode(requestBody.getPasswd1()),
                requestBody.getFirstName(),
                requestBody.getLastName(),
                LocalDateTime.now()));
        return personRepository.findByEmail(requestBody.getEmail()).isPresent();
    }

    private void changePassword(Person person, String password) {
        person.setPassword(encoder.encode(password));
        person.setConfirmationCode(null);
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
            return ResponseEntity.status(400).body(new ErrorErrorDescriptionResponse(errors.toString()));
        }

        if (savePerson(requestBody)) {
            return ResponseEntity.status(200)
                    .body(new ErrorTimeDataResponse("", new MessageResponse()));
        }

        return ResponseEntity.status(400).body(new ErrorErrorDescriptionResponse("Can't save user!"));
    }

    public ResponseEntity<?> putApiAccountPasswordRecovery(EmailRequest requestBody) {
        Optional<Person> optionalPerson = findPersonByEmail(requestBody.getEmail());

        if (optionalPerson.isEmpty()) {
            return ResponseEntity.status(400).body(new ErrorErrorDescriptionResponse("This email is not registered!"));
        }

        Person person = optionalPerson.get();
        String confirmationCode = encoder.encode(Long.toString(System.currentTimeMillis()))
                .substring(10).replaceAll("\\W", "");

        if (emailSenderService.sendEmailChangePassword(person, confirmationCode)) {
            setConfirmationCode(person, confirmationCode);
            return ResponseEntity.status(200)
                    .body(new ErrorTimeDataResponse("", new MessageResponse()));
        }

        return ResponseEntity.status(400).body(new ErrorErrorDescriptionResponse("Error sending email"));
    }

    public ResponseEntity<?> putApiAccountPasswordSet(TokenPasswordRequest requestBody) {

        Person person = personDetailsService.getCurrentUser();

        if (person.getConfirmationCode() == null || !person.getConfirmationCode().equals(requestBody.getToken())) {
            return ResponseEntity.status(400).body(new ErrorErrorDescriptionResponse("Code is expired!"));
        }

        changePassword(person, requestBody.getPassword());

        return ResponseEntity.status(200)
                .body(new ErrorTimeDataResponse("", new MessageResponse()));
    }

    public ResponseEntity<?> putApiAccountEmail(EmailRequest requestBody) {

        if (!isEmailCorrect(requestBody.getEmail())) {
            return ResponseEntity.status(400).body(new ErrorErrorDescriptionResponse("Email is not valid!"));
        }

        changeEmail(personDetailsService.getCurrentUser(), requestBody.getEmail());

        return ResponseEntity.status(200)
                .body(new ErrorTimeDataResponse("", new MessageResponse()));
    }

    public ResponseEntity<?> putApiAccountNotifications(NotificationTypeEnableRequest requestBody) {

        Optional<NotificationType> notificationType =
                notificationTypeRepository.findByName(requestBody.getNotificationType());
        if (notificationType.isEmpty()) {
            return ResponseEntity.status(400).body(new ErrorErrorDescriptionResponse("Wrong notification type"));
        }

        Person person = personDetailsService.getCurrentUser();
        boolean isEnable = requestBody.isEnable();

        Optional<NotificationSettings> notificationSettingsOptional = notificationSettingsRepository
                .findByPersonNSAndNotificationTypeId(person, notificationType.get().getId());

        if (notificationSettingsOptional.isPresent()) {
            NotificationSettings notificationSettings = notificationSettingsOptional.get();
            notificationSettings.setEnable(isEnable);
            notificationSettingsRepository.save(notificationSettings);
        } else {
            notificationSettingsRepository.save(new NotificationSettings(notificationType.get(), person, isEnable));
        }

        return ResponseEntity.status(200)
                .body(new ErrorTimeDataResponse("", new MessageResponse()));
    }

    public ResponseEntity<?> getApiAccountNotifications() {
        List<EnableTypeResponse> result = new ArrayList<>();

        for (NotificationSettings setting :
                notificationSettingsRepository.findByPersonNS(personDetailsService.getCurrentUser())) {

            result.add(new EnableTypeResponse(setting.getIsEnable(), setting.getNotificationType().getName()));
        }

        return ResponseEntity.status(200).body(new ErrorTimeListDataResponse(result));
    }
}