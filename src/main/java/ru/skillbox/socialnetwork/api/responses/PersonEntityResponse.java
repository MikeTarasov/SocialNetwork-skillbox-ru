package ru.skillbox.socialnetwork.api.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

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
    private long regDate;
    @JsonProperty("birth_date")
    private long birthDate;
    private String email;
    private String phone;
    private String photo;
    private String about;
    private String city;
    private String country;
    @JsonProperty("messages_permission")
    private String messagesPermission;
    @JsonProperty("last_online_time")
    private long lastOnlineTime;
    @JsonProperty("is_blocked")
    private boolean isBlocked;
    private String token;


    // Friends ->  token = null !!!
    public PersonEntityResponse(long id, String firstName, String lastName, long regDate,
                                long birthDate,
                                String email, String phone, String photo, String about,
                                String city, String country, String messagesPermission,
                                long lastOnlineTime, boolean isBlocked) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.regDate = regDate;
        this.birthDate = birthDate;
        this.email = email;
        this.phone = phone;
        this.photo = photo;
        this.about = about;
        this.city = city;
        this.country = country;
        this.messagesPermission = messagesPermission;
        this.lastOnlineTime = lastOnlineTime;
        this.isBlocked = isBlocked;
    }

    public PersonEntityResponse(long id, String firstName, String lastName, LocalDateTime regDate,
                                LocalDateTime birthDate, String email, String phone, String photo, String about,
                                String city, String country, String messagesPermission,
                                LocalDateTime lastOnlineTime, boolean isBlocked, String token) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        if (regDate != null) this.regDate = regDate.toEpochSecond(ZoneOffset.UTC);
        if (birthDate != null) this.birthDate = birthDate.toEpochSecond(ZoneOffset.UTC);
        ;
        this.email = email;
        this.phone = phone;
        this.photo = photo;
        this.about = about;
        this.city = city;
        this.country = country;
        this.messagesPermission = messagesPermission;
        if (lastOnlineTime != null) this.lastOnlineTime = lastOnlineTime.toEpochSecond(ZoneOffset.UTC);
        ;
        this.isBlocked = isBlocked;
        this.token = token;

    }
}