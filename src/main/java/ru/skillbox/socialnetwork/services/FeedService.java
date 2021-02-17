package ru.skillbox.socialnetwork.services;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.api.responses.CommentEntityResponse;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeTotalOffsetPerPageListDataResponse;
import ru.skillbox.socialnetwork.api.responses.PersonEntityResponse;
import ru.skillbox.socialnetwork.api.responses.PostEntityResponse;
import ru.skillbox.socialnetwork.model.entity.Friendship;
import ru.skillbox.socialnetwork.model.entity.Notification;
import ru.skillbox.socialnetwork.model.entity.Person;
import ru.skillbox.socialnetwork.model.entity.Post;
import ru.skillbox.socialnetwork.repository.FriendshipRepository;
import ru.skillbox.socialnetwork.repository.NotificationTypeRepository;
import ru.skillbox.socialnetwork.repository.NotificationsRepository;
import ru.skillbox.socialnetwork.repository.PostCommentRepository;
import ru.skillbox.socialnetwork.repository.PostRepository;
import ru.skillbox.socialnetwork.security.PersonDetailsService;

@Service
public class FeedService {

  private final PostRepository postRepository;
  private final PostCommentRepository postCommentRepository;
  private final NotificationsRepository notificationsRepository;
  private final NotificationTypeRepository notificationTypeRepository;
  private final PersonDetailsService personDetailsService;
  private final FriendshipRepository friendshipRepository;

  @Autowired
  public FeedService(PostRepository postRepository,
      PostCommentRepository postCommentRepository,
      NotificationsRepository notificationsRepository,
      NotificationTypeRepository notificationTypeRepository,
      PersonDetailsService personDetailsService, FriendshipRepository friendshipRepository) {
    this.postRepository = postRepository;
    this.postCommentRepository = postCommentRepository;
    this.notificationsRepository = notificationsRepository;
    this.notificationTypeRepository = notificationTypeRepository;
    this.personDetailsService = personDetailsService;
    this.friendshipRepository = friendshipRepository;
  }


  public ResponseEntity<?> getFeed(String name, int offset, int itemPerPage) {

    Page<Post> allPosts = postRepository.findAll(getPageable(offset, itemPerPage));

    if (allPosts.isEmpty()) {
      return ResponseEntity.status(HttpStatus.OK)
          .body(new ErrorTimeTotalOffsetPerPageListDataResponse());
    }

    List<PostEntityResponse> listPostsEntityResponses = allPosts.stream()
        .map(this::getPostEntityResponse)
        .sorted(Comparator.comparing(PostEntityResponse::getTime).reversed())
        .collect(Collectors.toList());

    if (!getBirthdayNotification().isEmpty()) {
      for (Person person : getBirthdayNotification()) {
        notificationsRepository.save(new Notification(
            notificationTypeRepository.findById(6L).get(),
            getMillisecondsToLocalDateTime(System.currentTimeMillis()),
            personDetailsService.getCurrentUser(),
            person.getId(),
            personDetailsService.getCurrentUser().getEmail(),
            0
        ));
      }
    }

    deleteOldNotifications();

    return ResponseEntity.status(HttpStatus.OK).body(
        getErrorTimeOffsetPerPageListDataResponse(listPostsEntityResponses, offset, itemPerPage));

  }

  private PostEntityResponse getPostEntityResponse(Post post) {
    return PostEntityResponse.builder()
        .id(post.getId())
        .time(post.getTimestamp())
        .author(PersonEntityResponse.getResponseEntity(post.getAuthor()))
        .title(post.getTitle())
        .postText(post.getPostText())
        .isBlocked(post.isBlocked())
        .likes(post.getLikes().size())
        .comments(CommentEntityResponse
            .getCommentEntityResponseList(post.getComments(), postCommentRepository))
        .build();
  }

  private ErrorTimeTotalOffsetPerPageListDataResponse getErrorTimeOffsetPerPageListDataResponse(
      List<PostEntityResponse> listPostsEntityResponses, int offset, int itemPerPage) {
    return ErrorTimeTotalOffsetPerPageListDataResponse.builder()
        .error("")
        .timestamp(System.currentTimeMillis())
        .total(listPostsEntityResponses.size())
        .offset(offset)
        .perPage(itemPerPage)
        .data(listPostsEntityResponses)
        .build();
  }

  private Pageable getPageable(int offset, int itemPerPage) {
    return PageRequest.of(offset / itemPerPage, itemPerPage);
  }

  private List<Person> getBirthdayNotification() {
    List<Person> friends = new ArrayList<>();
    Person me = personDetailsService.getCurrentUser();
    List<Friendship> getFriends = friendshipRepository.findByDstPersonOrSrcPerson(me, me);
    for (Friendship friend : getFriends) {

      if (friend.getSrcPerson().equals(me)
          && friend.getDstPerson().getBirthDate().getDayOfMonth() == LocalDateTime.now()
          .getDayOfMonth()
          && friend.getDstPerson().getBirthDate().getMonthValue() == LocalDateTime.now()
          .getMonthValue()) {
        friends.add(friend.getDstPerson());
      } else if (friend.getDstPerson().equals(me)
          && friend.getSrcPerson().getBirthDate().getDayOfMonth() == LocalDateTime.now()
          .getDayOfMonth()
          && friend.getSrcPerson().getBirthDate().getMonthValue() == LocalDateTime.now()
          .getMonthValue()) {
        friends.add(friend.getSrcPerson());
      }
    }
    return friends;
  }

  private LocalDateTime getMillisecondsToLocalDateTime(long milliseconds) {
    return Instant.ofEpochMilli(milliseconds).atZone(ZoneId.systemDefault()).toLocalDateTime();

  }

  private long getTimeStamp(LocalDateTime time) {
    return time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
  }

  private void deleteOldNotifications() {
    List<Notification> notifications = notificationsRepository.findAll();
    for (Notification notification : notifications) {
      if (notification.getIsRead() != 0
          && getTimeStamp(LocalDateTime.now()) - notification.getTimeStamp() > 5000) {
        notificationsRepository.delete(notification);
      }
    }
  }
}
