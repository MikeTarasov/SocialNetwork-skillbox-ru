package ru.skillbox.socialnetwork.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeDataResponse;
import ru.skillbox.socialnetwork.api.responses.PersonEntityResponse;
import ru.skillbox.socialnetwork.model.entity.Person;
import ru.skillbox.socialnetwork.repository.PersonRepository;
import ru.skillbox.socialnetwork.services.ProfileService;
import ru.skillbox.socialnetwork.services.exceptions.PersonNotFoundException;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class ProfileServiceImpl implements ProfileService {
    @Value("${db.timezone}")
    private String timezone;
    private final PersonRepository personRepository;

    @Autowired
    public ProfileServiceImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public ErrorTimeDataResponse getUser(int id) {
        Person person = personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException(id));

        return new ErrorTimeDataResponse(
                "",
                LocalDateTime.now().atZone(ZoneId.of(timezone)).toEpochSecond(),
                (PersonEntityResponse) convertPersonToResponse(person)
        );
    }



    /**
     *
     * Helper for converting Person entity to API response
     * @param person
     * @return PersonEntityResponse
     *
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
