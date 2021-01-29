package ru.skillbox.socialnetwork.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.api.responses.*;
import ru.skillbox.socialnetwork.model.entity.*;
import ru.skillbox.socialnetwork.repository.*;
import ru.skillbox.socialnetwork.security.PersonDetailsService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificationsService {

    private final NotificationsRepository notificationsRepository;
    private final NotificationSettingsRepository notificationSettingsRepository;
    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;
    private final MessageRepository messageRepository;
    private final FriendshipRepository friendshipRepository;
    private final PersonDetailsService personDetailsService;
    @Value("${notification.text.length}")
    private int notificationTextLength;

    public NotificationsService(NotificationsRepository notificationsRepository,
                                NotificationSettingsRepository notificationSettingsRepository,
                                PostRepository postRepository,
                                PostCommentRepository postCommentRepository,
                                MessageRepository messageRepository,
                                FriendshipRepository friendshipRepository,
                                PersonDetailsService personDetailsService) {
        this.notificationsRepository = notificationsRepository;
        this.notificationSettingsRepository = notificationSettingsRepository;
        this.postRepository = postRepository;
        this.postCommentRepository = postCommentRepository;
        this.messageRepository = messageRepository;
        this.friendshipRepository = friendshipRepository;
        this.personDetailsService = personDetailsService;
    }

    private List<NotificationBaseResponse> convertToNotificationResponse(List<Notification> notifications, Person person) {
        List<NotificationBaseResponse> result = new ArrayList<>();

        List<Long> listTypeIdsEnableSettings = notificationSettingsRepository
                .findByPersonNSAndEnable(person, 1)
                .stream().map(value -> value.getNotificationType().getId()).collect(Collectors.toList());

        for (Notification notification : notifications) {

            long typeId = notification.getType().getId();
            if (listTypeIdsEnableSettings.contains(typeId)) {

                long id = notification.getId();
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
                    case "COMMENT_COMMENT":
                        Optional<PostComment> optionalComment = postCommentRepository.findById(entityId);
                        if (optionalComment.isEmpty()) break;
                        PostComment comment = optionalComment.get();
                        sentTime = comment.getTimestamp();
                        info = "New Comment ".concat(comment.getCommentText().substring(0, notificationTextLength))
                                .concat(" from user ").concat(comment.getPerson().getFirstName());
                        break;
                    case "FRIEND_REQUEST":
                        Optional<Friendship> optionalFriendship = friendshipRepository.findById(entityId);
                        if (optionalFriendship.isEmpty()) break;
                        Friendship friendship = optionalFriendship.get();
                        sentTime = System.currentTimeMillis();
                        info = "User ".concat(friendship.getSrcPerson().getFirstName()).concat(" ")
                                .concat(friendship.getSrcPerson().getLastName()).concat(" offers friendship");
                        break;
                    case "MESSAGE":
                        Optional<Message> optionalMessage = messageRepository.findById(entityId);
                        if (optionalMessage.isEmpty()) break;
                        Message message = optionalMessage.get();
                        sentTime = message.getTimestamp();
                        info = "New message ".concat(message.getText()).substring(0, notificationTextLength)
                                .concat(" from user ").concat(message.getAuthor().getFirstName());
                        break;
                    case "FRIEND_BIRTHDAY":
                        //TODO нет в апи, но есть во фронте!!! src/store/profile/notifications.js
                        break;
                }
                result.add(new NotificationBaseResponse(id, typeId, sentTime, entityId, info));
            }
        }
        return result;
    }

    private boolean setIsRead(long id) {
        Optional<Notification> optionalNotification = notificationsRepository.findById(id);
        if (optionalNotification.isEmpty()) {
            return false;
        }
        Notification notification = optionalNotification.get();
        notification.setIsRead(1);
        notificationsRepository.save(notification);
        return true;
    }

    public ResponseEntity<?> getApiNotifications(Integer offset, Integer itemPerPage) {
        Person person = personDetailsService.getCurrentUser();

        if (offset == null || itemPerPage == null) {
            offset = 0;
            itemPerPage = 20;
        }

        return ResponseEntity.status(200).body(new ErrorTimeTotalOffsetPerPageListDataResponse(
                "",
                System.currentTimeMillis(),
                notificationsRepository.countNotificationByPersonNotification(person),
                offset,
                itemPerPage,
                convertToNotificationResponse(
                        notificationsRepository
                                .findByPersonNotificationAndIsRead(person, 0,
                                        PageRequest.of((offset / itemPerPage), itemPerPage)), person)
        ));
    }

    public ResponseEntity<?> putApiNotifications(long id, boolean all) {
        if (!all && !setIsRead(id)) {
            return ResponseEntity.status(400).body(new ErrorErrorDescriptionResponse("Notification not found"));

        } else {
            Person person = personDetailsService.getCurrentUser();
            notificationsRepository.findByPersonNotificationAndIsRead(person, 0, null)
                    .forEach(notification -> setIsRead(notification.getId()));
        }
        //TODO что должно быть в ответе??????
        return ResponseEntity.status(200).body(new ErrorTimeDataResponse("", new MessageResponse()));
    }
}
