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
    private final PersonRepository personRepository;
    @Value("${notification.text.length}")
    private int notificationTextLength;

    public NotificationsService(NotificationsRepository notificationsRepository,
                                NotificationSettingsRepository notificationSettingsRepository,
                                PostRepository postRepository,
                                PostCommentRepository postCommentRepository,
                                MessageRepository messageRepository,
                                FriendshipRepository friendshipRepository,
                                PersonDetailsService personDetailsService, PersonRepository personRepository) {
        this.notificationsRepository = notificationsRepository;
        this.notificationSettingsRepository = notificationSettingsRepository;
        this.postRepository = postRepository;
        this.postCommentRepository = postCommentRepository;
        this.messageRepository = messageRepository;
        this.friendshipRepository = friendshipRepository;
        this.personDetailsService = personDetailsService;
        this.personRepository = personRepository;
    }

    private List<NotificationBaseResponse> convertToNotificationResponse(List<Notification> notifications, Person person) {
        List<NotificationBaseResponse> result = new ArrayList<>();

        List<NotificationSettings> notificationSettingsList = notificationSettingsRepository
                .findByPersonNSAndEnable(person, 1);
        List<Long> enabledSettings = notificationSettingsList.stream()
                .map(ns -> ns.getNotificationType().getId())
                .collect(Collectors.toList());

        for (Notification notification : notifications) {

            long typeId = notification.getType().getId();
            if (enabledSettings.contains(typeId)) {

                long entityId = notification.getEntityId();
                String info = "";

                switch ((int) notification.getType().getId()) {
                    case 2:

                    case 3:
                        Optional<PostComment> commentToPostOptional = postCommentRepository.findById(entityId);
                        if (commentToPostOptional.isEmpty()) break;
                        PostComment commentToPost = commentToPostOptional.get();
                        info = "New Comment '".concat(getInfo(commentToPost.getCommentText()))
                                .concat(" from user ").concat(personRepository.findById(commentToPost.getPerson()
                                        .getId()).get().getFirstName());
                        break;
                    case 4:
                        Optional<Friendship> friendRequestOptional = friendshipRepository.findById(entityId);
                        if (friendRequestOptional.isEmpty()) break;
                        Friendship friendRequest = friendRequestOptional.get();
                        info = "User ".concat(personRepository.findById(friendRequest.getSrcPerson().getId())
                                .get().getFirstName().concat(" ")
                                .concat(personRepository.findById(friendRequest.getSrcPerson().getId())
                                        .get().getLastName()).concat(" offers friendship"));
                        break;
                    case 5:
                        Optional<Message> optionalMessage = messageRepository.findById(entityId);
                        if (optionalMessage.isEmpty()) break;
                        Message message = optionalMessage.get();
                        info = "New message '".concat(getInfo(message.getText()))
                                .concat(" from user ")
                                .concat(personRepository.findById(message.getAuthor().getId()).get().getFirstName());
                        break;
                    case 6:
                        if (!person.getEmail().equals(personRepository.findById(entityId).get().getEmail())) {
                            info = "User ".concat(personRepository.findById(entityId).get().getFirstName())
                                    .concat(" ")
                                    .concat(personRepository.findById(entityId).get().getLastName())
                                    .concat(" celebrates his/her birthday!");
                        }
                        break;
                }
                result.add(new NotificationBaseResponse(
                        notification.getId(),
                        typeId,
                        notification.getTimeStamp(),
                        notification.getEntityId(),
                        info));
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

    public ResponseEntity<?> putApiNotifications(Long id, Boolean all) {

        Person person = personDetailsService.getCurrentUser();

        if (!all || all == null) {
            setIsRead(id);
        } else if (all) {
            notificationsRepository.findByPersonNotificationAndIsRead(person, 0, null)
                    .forEach(notification -> setIsRead(notification.getId()));
        }

        return ResponseEntity.status(200).body(new ErrorTimeTotalOffsetPerPageListDataResponse(
                "",
                System.currentTimeMillis(),
                notificationsRepository.countNotificationByPersonNotification(person),
                0,
                20,
                convertToNotificationResponse(
                        notificationsRepository
                                .findByPersonNotificationAndIsRead(person, 0,
                                        PageRequest.of(0, 20)), person)
        ));
    }

    private String getInfo(String text) {
        if (text.length() >= 10) {
            return text.substring(0, 9).concat("...'");
        } else {
            return text;
        }
    }
}
