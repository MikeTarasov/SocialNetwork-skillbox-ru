package ru.skillbox.socialnetwork.services;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeTotalOffsetPerPageListDataResponse;
import ru.skillbox.socialnetwork.api.responses.NotificationBaseResponse;
import ru.skillbox.socialnetwork.model.entity.Notification;
import ru.skillbox.socialnetwork.model.entity.Person;
import ru.skillbox.socialnetwork.model.entity.Post;
import ru.skillbox.socialnetwork.repository.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationsService {

    private final NotificationsRepository notificationsRepository;
    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;
    private final MessageRepository messageRepository;
    private final FriendshipRepository friendshipRepository;
    private final AccountService accountService;

    public NotificationsService(NotificationsRepository notificationsRepository,
                                PostRepository postRepository,
                                PostCommentRepository postCommentRepository,
                                MessageRepository messageRepository,
                                FriendshipRepository friendshipRepository,
                                AccountService accountService) {
        this.notificationsRepository = notificationsRepository;
        this.postRepository = postRepository;
        this.postCommentRepository = postCommentRepository;
        this.messageRepository = messageRepository;
        this.friendshipRepository = friendshipRepository;
        this.accountService = accountService;
    }

    public ResponseEntity<?> getApiNotifications(int offset, int itemPerPage) {
        Person person = accountService.getCurrentUser();

        return ResponseEntity.status(200).body(new ErrorTimeTotalOffsetPerPageListDataResponse(
                "",
                System.currentTimeMillis(),
                notificationsRepository.countNotificationByPersonNotification(person),
                offset,
                itemPerPage,
                convertToNotificationResponse(
                        notificationsRepository
                                .findByPersonNotification(person, PageRequest.of((offset / itemPerPage), itemPerPage)))
        ));
    }

    public ResponseEntity<?> putApiNotifications(long id, boolean all) {
        return ResponseEntity.status(200).body(null);
    }

    private List<NotificationBaseResponse> convertToNotificationResponse(List<Notification> notifications) {
        List<NotificationBaseResponse> result = new ArrayList<>();
        for (Notification notification : notifications) {
            long id = notification.getId();
            long typeId = notification.getType().getId();
            long sentTime = 0L;
            long entityId = notification.getEntityId();
            String info = "";
            switch (notification.getType().getName().toUpperCase()) {
                case "POST":
                    Optional<Post> optionalPost = postRepository.findById(entityId);
                    if (optionalPost.isEmpty()) break;
                    Post post = optionalPost.get();
                    sentTime = post.getTimestamp();
                    info = "New post ".concat(post.getTitle()).concat(" from user ")
                            .concat(post.getAuthor().getFirstName());
                    break;
                case "POST_COMMENT":
                    break;
                case "COMMENT_COMMENT":
                    break;
                case "FRIEND_REQUEST":
                    break;
                case "MESSAGE":
                    break;
            }
            result.add(new NotificationBaseResponse(id, typeId, sentTime, entityId, info));
        }
        return result;
    }
}
