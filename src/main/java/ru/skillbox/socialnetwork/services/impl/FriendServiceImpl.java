package ru.skillbox.socialnetwork.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeTotalOffsetPerPageListDataResponse;
import ru.skillbox.socialnetwork.api.responses.PersonEntityResponse;
import ru.skillbox.socialnetwork.api.responses.UserIdStatusResponse;
import ru.skillbox.socialnetwork.model.entity.Friendship;
import ru.skillbox.socialnetwork.model.entity.Person;
import ru.skillbox.socialnetwork.model.enums.FriendStatus;
import ru.skillbox.socialnetwork.repository.FriendshipRepository;
import ru.skillbox.socialnetwork.repository.PersonRepository;
import ru.skillbox.socialnetwork.security.PersonDetailsService;
import ru.skillbox.socialnetwork.services.FriendService;
import ru.skillbox.socialnetwork.services.exceptions.PersonNotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
public class FriendServiceImpl implements FriendService {

    private final FriendshipRepository friendshipRepository;
    private final PersonRepository personRepository;
    private final PersonDetailsService personDetailsService;
    private final ProfileServiceImpl profileService;

    @Autowired
    public FriendServiceImpl(FriendshipRepository friendshipRepository,
                             PersonRepository personRepository,
                             PersonDetailsService personDetailsService,
                             ProfileServiceImpl profileService) {
        this.friendshipRepository = friendshipRepository;
        this.personRepository = personRepository;
        this.personDetailsService = personDetailsService;
        this.profileService = profileService;
    }

    @Override
    public ErrorTimeTotalOffsetPerPageListDataResponse getFriends(String name, Integer offset, Integer itemPerPage,
                                                                  FriendStatus friendStatus) {
        Person currentPerson = personDetailsService.getCurrentUser();
        Pageable paging = PageRequest.of(offset / itemPerPage,
                itemPerPage,
                Sort.by(Sort.Direction.ASC, "srcPerson.lastName"));

        Page<Friendship> friendPage;
        if (name == null || name.isEmpty())
            friendPage = friendshipRepository.findByDstPersonAndCode(currentPerson, friendStatus.name(), paging);
        else
            friendPage = friendshipRepository
                    .findByDstPersonAndSrcNameAndCode(currentPerson, name, friendStatus.name(), paging);

        return new ErrorTimeTotalOffsetPerPageListDataResponse(
                friendPage.getTotalElements(),
                offset,
                itemPerPage,
                convertFriendshipPageToPersonList(friendPage));
    }

    @Override
    public void addFriend(Long dstPersonId) {
        Person currentPerson = personDetailsService.getCurrentUser();
        Person dstPerson = personRepository.findById(dstPersonId).orElseThrow(() -> new PersonNotFoundException(dstPersonId));
        Friendship friendshipOut = new Friendship();
        friendshipOut.setDstPerson(dstPerson);
        friendshipOut.setSrcPerson(currentPerson);
        if (friendshipRepository.findByDstPersonAndSrcPerson(currentPerson, dstPerson).isEmpty()) {
            friendshipOut.setCode(FriendStatus.REQUEST.name());
        } else {
            Friendship friendshipIn = friendshipRepository.findByDstPersonAndSrcPerson(currentPerson, dstPerson).get();
            if (friendshipIn.getCode().equals(FriendStatus.REQUEST.name())) {
                friendshipIn.setCode(FriendStatus.FRIEND.name());
                friendshipOut.setCode(FriendStatus.FRIEND.name());
                friendshipRepository.save(friendshipIn);
            } else if (friendshipIn.getCode().equals(FriendStatus.DECLINED.name())) {
                friendshipOut.setCode(FriendStatus.SUBSCRIBED.name());
            } else if (friendshipIn.getCode().equals(FriendStatus.BLOCKED.name())) {
                return;
            }
        }
        friendshipRepository.save(friendshipOut);
    }

    @Override
    public void deleteFriend(Long dstPersonId) {
        Person currentPerson = personDetailsService.getCurrentUser();
        Person dstPerson = personRepository.findById(dstPersonId).orElseThrow(() -> new PersonNotFoundException(dstPersonId));
        if (friendshipRepository.findByDstPersonAndSrcPerson(dstPerson, currentPerson).isEmpty())
            return;
        Friendship friendshipOut = friendshipRepository.findByDstPersonAndSrcPerson(dstPerson, currentPerson).get();

        if (friendshipOut.getCode().equals(FriendStatus.REQUEST.name()) || friendshipOut.getCode().equals(FriendStatus.SUBSCRIBED.name())) {
            friendshipRepository.delete(friendshipOut);
        } else if (friendshipOut.getCode().equals(FriendStatus.FRIEND.name())) {
            friendshipOut.setCode(FriendStatus.DECLINED.name());
            friendshipRepository.save(friendshipOut);
            if (friendshipRepository.findByDstPersonAndSrcPerson(currentPerson, dstPerson).isPresent()) {
                Friendship friendshipIn = friendshipRepository.findByDstPersonAndSrcPerson(currentPerson, dstPerson).get();
                friendshipIn.setCode(FriendStatus.SUBSCRIBED.name());
                friendshipRepository.save(friendshipIn);
            }
        }
    }

    @Override
    public List<UserIdStatusResponse> isFriend(List<Long> userIds) {
        Person currentPerson = personDetailsService.getCurrentUser();
        List<UserIdStatusResponse> response = new ArrayList<>();
        for (Long id : userIds) {
            if (personRepository.findById(id).isEmpty())
                continue;
            Person dstPerson = personRepository.findById(id).get();
            if (friendshipRepository.findByDstPersonAndSrcPerson(personRepository.findById(id).get(), currentPerson).isEmpty())
                continue;
            UserIdStatusResponse status = new UserIdStatusResponse();
            status.setUserId(id);
            status.setStatus(FriendStatus.valueOf(
                    friendshipRepository.findByDstPersonAndSrcPerson(personRepository.findById(id).get(), currentPerson)
                            .get().getCode()));
            response.add(status);
        }
        return response;
    }

    @Override
    public ErrorTimeTotalOffsetPerPageListDataResponse getRecommendations(Integer offset, Integer itemPerPage) {
        Person currentPerson = personDetailsService.getCurrentUser();
        Pageable paging = PageRequest.of(offset / itemPerPage,
                itemPerPage,
                Sort.by(Sort.Direction.ASC, "dstPerson.lastName"));

        List<Person> friends = friendshipRepository.findBySrcPersonAndCode(currentPerson, FriendStatus.FRIEND.name());
        List<Person> known = friendshipRepository.findBySrcPerson(currentPerson);
        known.add(currentPerson);
        Page<Person> recommendedPersons = null;
        if (!friends.isEmpty()) {
            recommendedPersons = friendshipRepository.findNewRecs(friends, known, paging);
        }
        if (recommendedPersons == null || recommendedPersons.isEmpty()) {
            paging = PageRequest.of(offset / itemPerPage,
                    itemPerPage,
                    Sort.by(Sort.Direction.ASC, "lastName"));
            recommendedPersons = personRepository.findRandomRecs(known, paging);
        }

        return new ErrorTimeTotalOffsetPerPageListDataResponse(
                recommendedPersons.getTotalElements(),
                offset,
                itemPerPage,
                convertPersonPageToList(recommendedPersons));
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
     * Helper
     * Converting Page<Person> to List<PersonEntityResponse>
     */
    private List<PersonEntityResponse> convertPersonPageToList(Page<Person> page) {
        List<PersonEntityResponse> personResponseList = new ArrayList<>();
        page.forEach(person -> personResponseList.add(convertPersonToResponse(person)));
        return personResponseList;
    }

    /**
     * Helper for converting Person entity to API response
     *
     * @param person
     * @return PersonEntityResponse
     */

    private PersonEntityResponse convertPersonToResponse(Person person) {
        return profileService.convertPersonToResponse(person);
    }
}
