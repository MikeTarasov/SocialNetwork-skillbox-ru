package ru.skillbox.socialnetwork.api.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.skillbox.socialnetwork.model.entities.Person;
import ru.skillbox.socialnetwork.services.ConvertTimeService;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class PersonEntityResponse {

    private long id;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("reg_date")
    private Long regDate;
    @JsonProperty("birth_date")
    private Long birthDate;
    private String email;
    private String phone;
    private String photo;
    private String about;
    private String city;
    private String country;
    @JsonProperty("messages_permission")
    private String messagesPermission;
    @JsonProperty("last_online_time")
    private Long lastOnlineTime;
    @JsonProperty("is_blocked")
    private boolean isBlocked;
    private String token;


    public PersonEntityResponse(Person person, String token) {
        this(person);
        this.token = token;
    }

    public PersonEntityResponse(Person person) {
        this.id = person.getId();
        this.firstName = person.getFirstName();
        this.lastName = person.getLastName();
        if (person.getRegDate() != null) this.regDate = ConvertTimeService.getTimestamp(person.getRegDate());
        if (person.getBirthDate() != null) this.birthDate = ConvertTimeService.getTimestamp(person.getBirthDate());
        this.email = person.getEmail();
        this.phone = person.getPhone();
        this.photo = person.getPhoto();
        this.about = person.getAbout();
        this.city = person.getCity();
        this.country = person.getCountry();
        this.messagesPermission = person.getMessagePermission();
        if (person.getLastOnlineTime() != null) {
            this.lastOnlineTime = ConvertTimeService.getTimestamp(person.getLastOnlineTime());
        }
        this.isBlocked = person.isBlocked();
    }
}