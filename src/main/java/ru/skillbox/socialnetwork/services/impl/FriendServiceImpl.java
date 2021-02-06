package ru.skillbox.socialnetwork.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeTotalOffsetPerPageListDataResponse;
import ru.skillbox.socialnetwork.api.responses.PersonEntityResponse;
import ru.skillbox.socialnetwork.model.entity.Friendship;
import ru.skillbox.socialnetwork.model.entity.Person;
import ru.skillbox.socialnetwork.model.enums.FriendStatus;
import ru.skillbox.socialnetwork.repository.FriendshipRepository;
import ru.skillbox.socialnetwork.security.PersonDetailsService;
import ru.skillbox.socialnetwork.services.FriendService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

@Service
public class FriendServiceImpl implements FriendService {

    private final FriendshipRepository friendshipRepository;
    private final PersonDetailsService personDetailsService;

    @Autowired
    public FriendServiceImpl(FriendshipRepository friendshipRepository, PersonDetailsService personDetailsService) {
        this.friendshipRepository = friendshipRepository;
        this.personDetailsService = personDetailsService;
    }

    @Override
    public ErrorTimeTotalOffsetPerPageListDataResponse getFriends(String name, Integer offset, Integer itemPerPage, FriendStatus friendStatus) {
        Person currentPerson = personDetailsService.getCurrentUser();
        Pageable paging = PageRequest.of(offset / itemPerPage,
                itemPerPage,
                Sort.by(Sort.Direction.ASC, "srcPerson.lastName"));

        Page<Friendship> friendPage;
        if (name == null || name.isEmpty())
            friendPage = friendshipRepository.findByDstPersonAndCode(currentPerson, friendStatus.name(), paging);
        else
            friendPage = friendshipRepository.findByDstPersonAndDstPersonNameAndCode(currentPerson, name, friendStatus.name(), paging);

        return new ErrorTimeTotalOffsetPerPageListDataResponse(
                friendPage.getTotalElements(),
                offset,
                itemPerPage,
                convertFriendshipPageToPersonList(friendPage));
    }

    /**
     * Helper
     * Converting Page<Friendship> to List<PersonEntityResponse>
     */
    private List<PersonEntityResponse> convertFriendshipPageToPersonList(Page<Friendship> friendships) {
        List<PersonEntityResponse> personResponseList = new ArrayList<>();
        friendships.forEach(friendship -> personResponseList.add(convertPersonToResponse(friendship.getSrcPerson())));
        return personResponseList;
    }
    /**
     * Helper for converting Person entity to API response
     *
     * @param person
     * @return PersonEntityResponse
     */

    private PersonEntityResponse convertPersonToResponse(Person person) {
        LocalDateTime birthDate = person.getBirthDate();
        LocalDateTime lastOnlineTime = person.getLastOnlineTime();

        return PersonEntityResponse.builder()
                .id(person.getId())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .regDate(person.getRegDate().atZone(TimeZone.getDefault().toZoneId()).toInstant().toEpochMilli())
                .birthDate(birthDate == null ? null :
                        birthDate.atZone(TimeZone.getDefault().toZoneId()).toInstant().toEpochMilli())
                .email(person.getEmail())
                .phone(person.getPhone())
                .photo(person.getPhoto())
                .about(person.getAbout())
                .city(person.getCity())
                .country(person.getCountry())
                .messagesPermission(person.getMessagePermission())
                .lastOnlineTime(lastOnlineTime == null ? null :
                        lastOnlineTime.atZone(TimeZone.getDefault().toZoneId()).toInstant().toEpochMilli())
                .isBlocked(person.getIsBlocked() == 1)
                .build();
    }
}
