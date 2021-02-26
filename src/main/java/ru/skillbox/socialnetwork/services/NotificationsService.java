package ru.skillbox.socialnetwork.services;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeTotalOffsetPerPageListDataResponse;
import ru.skillbox.socialnetwork.api.responses.NotificationBaseResponse;
import ru.skillbox.socialnetwork.api.responses.PersonEntityResponse;
import ru.skillbox.socialnetwork.model.entity.*;
import ru.skillbox.socialnetwork.repository.*;
import ru.skillbox.socialnetwork.security.PersonDetailsService;
import ru.skillbox.socialnetwork.services.exceptions.PersonNotFoundException;

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

    public List<NotificationBaseResponse> convertToNotificationResponse(List<Notification> notifications, Person person) {
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
                Person author = null;

                switch ((int) notification.getType().getId()) {
                    case 2:
                    case 3:
                        Optional<PostComment> commentToPostOptional = postCommentRepository.findById(entityId);
                        if (commentToPostOptional.isEmpty()) break;
                        PostComment commentToPost = commentToPostOptional.get();
                        info = getInfo(commentToPost.getCommentText());
                        author = commentToPost.getPerson();
                        break;
                    case 4:
                        Optional<Friendship> friendRequestOptional = friendshipRepository.findById(entityId);
                        if (friendRequestOptional.isEmpty()) break;
                        author = friendRequestOptional.get().getSrcPerson();
                        info = "Напишите ему!";
                        break;
                    case 5:
                        Optional<Message> optionalMessage = messageRepository.findById(entityId);
                        if (optionalMessage.isEmpty()) break;
                        Message message = optionalMessage.get();
                        author = message.getAuthor();
                        info =  getInfo(message.getText());
                        break;
                    case 6:
                        Optional<Person> optionalPerson = personRepository.findById(entityId);
                        if (optionalPerson.isEmpty()) break;
                        author = personRepository.findById(entityId).orElseThrow(() -> new PersonNotFoundException(entityId));
//                        if (!person.getEmail().equals(optionalPerson.get().getEmail())) {
//                            info = "User ".concat(optionalPerson.get().getFirstName())
//                                    .concat(" ")
//                                    .concat(optionalPerson.get().getLastName())
//                                    .concat(" celebrates his/her birthday!");
//                        }
                        info = "Поздравьте его!";
                        break;
                    default:
                        author = personRepository.findById(2L).get(); // TODO: Создать сервисного пользователя для отправки уведомлений когда у события не заполнен автор
                        break;
                }

                PersonEntityResponse authorResponse = PersonEntityResponse.builder()
                        .id(author.getId())
                        .photo(author.getPhoto())
                        .firstName(author.getFirstName())
                        .lastName(author.getLastName())
                        .build();

                result.add(new NotificationBaseResponse(
                        notification.getId(),
                        typeId,
                        notification.getType().getName(),
                        notification.getTimeStamp(),
                        notification.getEntityId(),
                        info,
                       authorResponse
                       ));
            }
        }
        return result;
    }

    public void setIsRead(long id) {
        Optional<Notification> optionalNotification = notificationsRepository.findById(id);
        if (optionalNotification.isEmpty()) {
            return;
        }
        Notification notification = optionalNotification.get();
        notification.setIsRead(1);
        notificationsRepository.save(notification);
    }

    public ResponseEntity<ErrorTimeTotalOffsetPerPageListDataResponse> getApiNotifications(Integer offset, Integer itemPerPage) {
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

        if (all == null || !all) {
            setIsRead(id);
        } else {
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
