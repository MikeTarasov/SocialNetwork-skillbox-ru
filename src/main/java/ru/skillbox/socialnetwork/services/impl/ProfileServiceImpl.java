package ru.skillbox.socialnetwork.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import ru.skillbox.socialnetwork.repository.PostRepository;
import ru.skillbox.socialnetwork.services.AccountService;
import ru.skillbox.socialnetwork.services.ProfileService;
import ru.skillbox.socialnetwork.services.exceptions.PersonNotFoundException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

@Service
public class ProfileServiceImpl implements ProfileService {
    @Value("${db.timezone}")
    private String timezone;
    private final PersonRepository personRepository;
    private final AccountService accountService;
    private final PostRepository postRepository;

    @Autowired
    public ProfileServiceImpl(PersonRepository personRepository, AccountService accountService, PostRepository postRepository) {
        this.personRepository = personRepository;
        this.accountService = accountService;
        this.postRepository = postRepository;
    }

    @Override
    public ErrorTimeDataResponse getUser(long id) {
        Person person = personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException(id));
        return new ErrorTimeDataResponse(
                "",
                LocalDateTime.now().atZone(ZoneId.of(timezone)).toEpochSecond(),
                convertPersonToResponse(person)
        );
    }

    private Person getCurrentUserAsPerson() {
        return accountService.getCurrentUser();
    }

    public ErrorTimeDataResponse getCurrentUser(){
        return new ErrorTimeDataResponse("", convertPersonToResponse(getCurrentUserAsPerson()));
    }


    @Override
    public ErrorTimeDataResponse updateCurrentUser(PersonEditRequest personEditRequest) {
        Person person = getCurrentUserAsPerson();

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
        return new ErrorTimeDataResponse("", convertPersonToResponse(getCurrentUserAsPerson()));
    }

    @Override
    public ErrorTimeDataResponse deleteCurrentUser() {
        Person person = getCurrentUserAsPerson();
        person.setIsDeleted(1);
        personRepository.save(person);
        return new ErrorTimeDataResponse("", new MessageResponse());
    }


    @Override
    public ErrorTimeDataResponse setBlockUserById(long id, int block) {
        Person person = getCurrentUserAsPerson();
        person.setIsBlocked(block);
        personRepository.save(person);
        return new ErrorTimeDataResponse("", new MessageResponse());
    }

    @Override
    public ErrorTimeTotalOffsetPerPageListDataResponse getWallPosts(long personId, int offset, int itemPerPage) {
        Person person = personRepository.findById(personId).orElseThrow(() -> new PersonNotFoundException(personId));
        Pageable paging = PageRequest.of( offset / itemPerPage, itemPerPage, Sort.by(Sort.Direction.DESC, "time"));

        Page<Post> posts = postRepository.findByAuthorAndTimeBeforeAndIsBlockedAndIsDeleted(person, LocalDateTime.now(), 0, 0,  paging);
        return new ErrorTimeTotalOffsetPerPageListDataResponse(
                "",
                LocalDateTime.now().atZone(ZoneId.of(timezone)).toEpochSecond(),
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
            publishDate = dateToPublish.atZone(ZoneId.of(timezone)).toEpochSecond();
        }
        else {
            dateToPublish = LocalDateTime.ofInstant(Instant.ofEpochMilli(publishDate), TimeZone.getDefault().toZoneId());
        }

        Post post = Post.builder()
                .postText(requestBody.getPostText())
                //.author(accountService.getCurrentUser())
                .author(personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException(id)))   // временная реализация, пока не работает авторизация(accountService)
                .time(dateToPublish)
                .isBlocked(0)
                .isDeleted(0)
                .title(requestBody.getTitle())
                .build();
        postRepository.save(post);
        return new ErrorTimeDataResponse("", convertPostToPostResponse(post));
    }

    @Override
    public ErrorTimeTotalOffsetPerPageListDataResponse search(String firstName, String lastName, int ageFrom, int ageTo, int offset, int itemPerPage) {
        Pageable paging = PageRequest.of( offset / itemPerPage, itemPerPage);
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        if (ageTo > 0)
            startDate = LocalDateTime.now().minusYears(ageTo);
        if (ageFrom > 0)
            endDate = LocalDateTime.now().minusYears(ageFrom);
        Page<Person> personPage = personRepository.findPersons(firstName, lastName, startDate, endDate, paging);
        return new ErrorTimeTotalOffsetPerPageListDataResponse(
                "",
                LocalDateTime.now().atZone(ZoneId.of(timezone)).toEpochSecond(),
                personPage.getTotalElements(),
                offset,
                itemPerPage,
                convertPersonPageToList(personPage));
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

    /**
     * Helper
     * Converting Page<Post> to List<PostEntityResponse>
     */
    private List<PostEntityResponse> convertPostPageToList(Page<Post> page) {
        List<PostEntityResponse> postResponseList = new ArrayList<>();
        page.forEach(post -> {
            postResponseList.add(convertPostToPostResponse(post)
            );
        });
        return postResponseList;
    }

    /**
     * Helper
     * Converting Page<Person> to List<PersonEntityResponse>
     */
    private List<PersonEntityResponse> convertPersonPageToList(Page<Person> page) {
        List<PersonEntityResponse> personResponseList = new ArrayList<>();
        page.forEach(person -> {
            personResponseList.add(convertPersonToResponse(person)
            );
        });
        return personResponseList;
    }

    /**
     * Helper
     * Converting Post to PostEntityResponse
     */

    private PostEntityResponse convertPostToPostResponse(Post post) {
        return PostEntityResponse.builder()
                .id(post.getId())
                .time(post.getTime().atZone(ZoneId.of(timezone)).toEpochSecond())
                .title(post.getTitle())
                .author(convertPersonToResponse(post.getAuthor()))
                .postText(post.getPostText())
                .isBlocked(post.getIsBlocked() == 1)
                .likes(11)       // Mock  TODO: link likes count to Post
                .type("POSTED")     // Mock
                .comments(convertCommentsToCommentResponseList(post.getComments()))
                .build();
    }

    /**
     * Helper
     * Converting List<PostComment> to List<CommentEntityResponse>
     */
    private List<CommentEntityResponse> convertCommentsToCommentResponseList(List<PostComment> comments) {
        List<CommentEntityResponse> postComments = new ArrayList<>();
        if (comments != null) {
            comments.forEach(comment -> {
                postComments.add(
                        CommentEntityResponse.builder()
                                .id(comment.getId())
                                .authorId(comment.getPerson().getId())
                                .commentText(comment.getCommentText())
                                .isBlocked(comment.getIsBlocked())
                                .parentId(comment.getParentId() == null ? 0 : comment.getParentId())
                                .postId(comment.getPost().getId())
                                .time(comment.getTime().atZone(ZoneId.of(timezone)).toEpochSecond())
                                .build()
                );
            });
        }
        return  postComments;
    }
}
