package ru.skillbox.socialnetwork.services.impl;

import com.sun.el.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.api.requests.PersonEditRequest;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeDataResponse;
import ru.skillbox.socialnetwork.api.responses.MessageResponse;
import ru.skillbox.socialnetwork.api.responses.PersonEntityResponse;
import ru.skillbox.socialnetwork.model.entity.Person;
import ru.skillbox.socialnetwork.repository.PersonRepository;
import ru.skillbox.socialnetwork.services.AccountService;
import ru.skillbox.socialnetwork.services.ProfileService;
import ru.skillbox.socialnetwork.services.exceptions.PersonNotFoundException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Service
public class ProfileServiceImpl implements ProfileService {
    @Value("${db.timezone}")
    private String timezone;
    private final PersonRepository personRepository;
    private final AccountService accountService;

    @Autowired
    public ProfileServiceImpl(PersonRepository personRepository, AccountService accountService) {
        this.personRepository = personRepository;
        this.accountService = accountService;
    }

    @Override
    public ErrorTimeDataResponse getUser(long id) {
        Person person = personRepository.findById((int) id).orElseThrow(() -> new PersonNotFoundException(id));
        return new ErrorTimeDataResponse(
                "",
                LocalDateTime.now().atZone(ZoneId.of(timezone)).toEpochSecond(),
                (PersonEntityResponse) convertPersonToResponse(person)
        );
    }

    @Override
    public ErrorTimeDataResponse getCurrentUser() {
        return getUser(getCurrentUserId());
    }

    public int getCurrentUserId() {
        return (int) accountService.getCurrentUser().getId();
    }

    @Override
    public ErrorTimeDataResponse updateCurrentUser(PersonEditRequest personEditRequest) {
        Person person = personRepository.findById(getCurrentUserId()).orElseThrow(()
                -> new PersonNotFoundException(getCurrentUserId()));

        if (personEditRequest.getFirstName() != null) {
            person.setFirstName(personEditRequest.getFirstName());
        }

        if (personEditRequest.getLastName() != null) {
            person.setLastName(personEditRequest.getLastName());
        }
        if (personEditRequest.getBirthDate() != 0) {
            LocalDateTime birthDate =
                    LocalDateTime.ofEpochSecond(personEditRequest.getBirthDate(), 0, ZoneOffset.ofHours(3));
            person.setBirthDate(birthDate);
        }
        if (personEditRequest.getPhone() != null) {
            person.setPhone(personEditRequest.getPhone());
        }
        if (personEditRequest.getPhotoId() != 0) {
            person.setPhoto(String.valueOf(personEditRequest.getPhotoId()));
        }
        if (personEditRequest.getAbout() != null) {
            person.setAbout(personEditRequest.getAbout());
        }
        if (personEditRequest.getTownId() != 0) {
            person.setCity(String.valueOf(personEditRequest.getTownId()));
        }
        if (personEditRequest.getCountryId() != 0) {
            person.setCountry(String.valueOf(personEditRequest.getCountryId()));
        }
        if (personEditRequest.getMessagesPermission() != null) {
            person.setMessagePermission(personEditRequest.getMessagesPermission().toString());
        }
        personRepository.save(person);
        return new ErrorTimeDataResponse("", new MessageResponse());
    }

    @Override
    public ErrorTimeDataResponse deleteCurrentUser() {
        Person person = personRepository.findById(getCurrentUserId()).orElseThrow(()
                -> new PersonNotFoundException(getCurrentUserId()));
        person.setIsDeleted(1);
        personRepository.save(person);
        return new ErrorTimeDataResponse("", new MessageResponse());
    }


    @Override
    public ErrorTimeDataResponse setBlockUserById(long id, int block) {
        Person person = personRepository.findById(getCurrentUserId()).orElseThrow(()
                -> new PersonNotFoundException(id));
        person.setIsBlocked(block);
        personRepository.save(person);
        return new ErrorTimeDataResponse("", new MessageResponse());
    }


    /**
     * Helper for converting Person entity to API response
     *
     * @param person
     * @return PersonEntityResponse
     */
    private PersonEntityResponse convertPersonToResponse(Person person) {

        return PersonEntityResponse.builder()
                .id(person.getId())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .regDate(person.getRegDate().atZone(ZoneId.of(timezone)).toEpochSecond())
                .birthDate(person.getBirthDate().atZone(ZoneId.of(timezone)).toEpochSecond())
                .email(person.getEmail())
                .phone(person.getPhone())
                .photo(person.getPhoto())
                .about(person.getAbout())
                .city(person.getCity())
                .country(person.getCountry())
                .messagesPermission(person.getMessagePermission())
                .lastOnlineTime(person.getLastOnlineTime().atZone(ZoneId.of(timezone)).toEpochSecond())
                .isBlocked(person.getIsBlocked() == 1)
                .build();
    }
}
