package ru.skillbox.socialnetwork.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.api.requests.PersonEditRequest;
import ru.skillbox.socialnetwork.api.requests.TitlePostTextRequest;
import ru.skillbox.socialnetwork.api.responses.*;
import ru.skillbox.socialnetwork.model.entity.Person;
import ru.skillbox.socialnetwork.model.entity.Post;
import ru.skillbox.socialnetwork.model.entity.PostComment;
import ru.skillbox.socialnetwork.repository.PersonRepository;
import ru.skillbox.socialnetwork.repository.PostCommentRepository;
import ru.skillbox.socialnetwork.repository.PostRepository;
import ru.skillbox.socialnetwork.security.PersonDetailsService;
import ru.skillbox.socialnetwork.services.ConvertTimeService;
import ru.skillbox.socialnetwork.services.ProfileService;
import ru.skillbox.socialnetwork.services.exceptions.PersonNotFoundException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final PersonRepository personRepository;
    private final PersonDetailsService personDetailsService;
    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;

    @Autowired
    public ProfileServiceImpl(PersonRepository personRepository,
                              PersonDetailsService personDetailsService,
                              PostRepository postRepository,
                              PostCommentRepository postCommentRepository) {
        this.personRepository = personRepository;
        this.personDetailsService = personDetailsService;
        this.postRepository = postRepository;
        this.postCommentRepository = postCommentRepository;
    }

    @Override
    public ErrorTimeDataResponse getUser(long id) {
        Person person = personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException(id));
        return new ErrorTimeDataResponse(
                "",
                convertPersonToResponse(person)
        );
    }

    public ErrorTimeDataResponse getCurrentUser() {
        return new ErrorTimeDataResponse("", convertPersonToResponse(personDetailsService.getCurrentUser()));
    }


    @Override
    public ErrorTimeDataResponse updateCurrentUser(PersonEditRequest personEditRequest) {
        Person person = personDetailsService.getCurrentUser();

        if (personEditRequest.getFirstName() != null) {
            person.setFirstName(personEditRequest.getFirstName());
        }

        if (personEditRequest.getLastName() != null) {
            person.setLastName(personEditRequest.getLastName());
        }
        if (personEditRequest.getBirthDate() != null) {
            person.setBirthDate(LocalDateTime.of(
                    LocalDate.parse(personEditRequest.getBirthDate().substring(0, 10), DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    LocalTime.of(0, 0, 0)));
        }
        if (personEditRequest.getPhone() != null) {
            person.setPhone(personEditRequest.getPhone());
        }
        if (personEditRequest.getAbout() != null) {
            person.setAbout(personEditRequest.getAbout());
        }
        if (personEditRequest.getTownId() != null) {
            person.setCity(personEditRequest.getTownId());
        }
        if (personEditRequest.getCountryId() != null) {
            person.setCountry(personEditRequest.getCountryId());
        }
        if (personEditRequest.getMessagesPermission() != null) {
            person.setMessagePermission(personEditRequest.getMessagesPermission().toString());
        }
        personRepository.save(person);
        return new ErrorTimeDataResponse("", convertPersonToResponse(person));
    }

    @Override
    public ErrorTimeDataResponse deleteCurrentUser() {
        Person person = personDetailsService.getCurrentUser();

        //на фронте криво работает удаление - временно удаляем юзера целиком
        person.setIsDeleted(1);
        personRepository.save(person);
        //может вызвать SQL-error -> следить за работоспособностью каскадного удаления!!!!!
//        personRepository.delete(person);

        return new ErrorTimeDataResponse("", new MessageResponse());
    }


    @Override
    public ErrorTimeDataResponse setBlockUserById(long id, int block) {
//        Person person = personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException(id));
//        person.setIsBlocked(block);
//        personRepository.save(person);

        //пока блокировку юзеров отключаем, до момента адекватной реализации данного функционала

        return new ErrorTimeDataResponse("", new MessageResponse());
    }

    @Override
    public ErrorTimeTotalOffsetPerPageListDataResponse getWallPosts(long personId, int offset, int itemPerPage) {

        Person person = personRepository.findById(personId).orElseThrow(() -> new PersonNotFoundException(personId));
        Pageable paging = PageRequest.of(offset / itemPerPage, itemPerPage, Sort.by(Sort.Direction.DESC, "time"));

        Page<Post> posts = postRepository
                .findByAuthorAndTimeBeforeAndIsBlockedAndIsDeleted(person, LocalDateTime.now(), 0, 0, paging);
        return new ErrorTimeTotalOffsetPerPageListDataResponse(
                "",
                System.currentTimeMillis(),
                posts.getTotalElements(),
                offset,
                itemPerPage,
                convertPostPageToList(posts));
    }

    @Override
    public ErrorTimeDataResponse putPostOnWall(long id, Long publishDate, TitlePostTextRequest requestBody) {
        LocalDateTime dateToPublish;
        if (publishDate == null) {
            dateToPublish = LocalDateTime.now();
        } else {
            dateToPublish = Instant.ofEpochMilli(publishDate).atZone(TimeZone.getDefault().toZoneId()).toLocalDateTime();
        }

        Post post = Post.builder()
                .postText(requestBody.getPostText())
                .author(personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException(id)))
                .time(dateToPublish)
                .isBlocked(0)
                .isDeleted(0)
                .title(requestBody.getTitle())
                .likes(new ArrayList<>())
                .build();
        postRepository.save(post);
        return new ErrorTimeDataResponse("", convertPostToPostResponse(post, null));
    }

    @Override
    public ErrorTimeTotalOffsetPerPageListDataResponse search(
            String firstName, String lastName, String city, String country, Integer ageFrom, Integer ageTo,
            Integer offset, Integer itemPerPage) {

        if (offset == null || itemPerPage == null) {
            offset = 0;
            itemPerPage = 20;
        }

        firstName = convertNullString(firstName);
        lastName = convertNullString(lastName);
        city = convertNullString(city);
        country = convertNullString(country);

        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        if (ageTo > 0)
            startDate = LocalDateTime.now().minusYears(ageTo);
        if (ageFrom > 0)
            endDate = LocalDateTime.now().minusYears(ageFrom);

        Pageable paging = PageRequest.of(offset / itemPerPage, itemPerPage);
        Page<Person> personPage = personRepository.findPersons(firstName, lastName, city, country, startDate, endDate,
                personDetailsService.getCurrentUser().getId(), paging);

        return new ErrorTimeTotalOffsetPerPageListDataResponse(
                "",
                System.currentTimeMillis(),
                personPage.getTotalElements(),
                offset,
                itemPerPage,
                convertPersonPageToList(personPage));
    }

    private String convertNullString(String s) {
        if (s == null) return "";
        return "%".concat(s).concat("%");
    }

    /**
     * Helper for converting Person entity to API response
     *
     * @param person
     * @return PersonEntityResponse
     */
    PersonEntityResponse convertPersonToResponse(Person person) {
        return new PersonEntityResponse(person);
    }

    /**
     * Helper
     * Converting Page<Post> to List<PostEntityResponse>
     */
    private List<PostEntityResponse> convertPostPageToList(Page<Post> page) {
        List<PostEntityResponse> postResponseList = new ArrayList<>();
        page.forEach(post -> postResponseList.add(convertPostToPostResponse(post, "POSTED")));
        return postResponseList;
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
     * Helper
     * Converting Post to PostEntityResponse
     */

    private PostEntityResponse convertPostToPostResponse(Post post, String type) {
        return PostEntityResponse.builder()
                .id(post.getId())
                .time(ConvertTimeService.getTimestamp(post.getTime()))
                .title(post.getTitle())
                .author(convertPersonToResponse(post.getAuthor()))
                .postText(post.getPostText())
                .isBlocked(post.getIsBlocked() == 1)
                .likes(post.getLikes().size())
                .type(type)     // Mock
                .comments(convertCommentsToCommentResponseList(postCommentRepository.getCommentsByPostId(post.getId())))
                .build();
    }

    /**
     * Helper
     * Converting List<PostComment> to List<CommentEntityResponse>
     */
    private List<CommentEntityResponse> convertCommentsToCommentResponseList(List<PostComment> comments) {
        if (comments == null) comments = new ArrayList<>();
        return CommentEntityResponse.getCommentEntityResponseList(comments, postCommentRepository);
    }
}
