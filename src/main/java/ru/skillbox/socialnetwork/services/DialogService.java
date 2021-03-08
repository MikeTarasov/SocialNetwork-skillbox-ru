package ru.skillbox.socialnetwork.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.api.requests.DialogRequest;
import ru.skillbox.socialnetwork.api.requests.LinkRequest;
import ru.skillbox.socialnetwork.api.requests.MessageTextRequest;
import ru.skillbox.socialnetwork.api.responses.*;
import ru.skillbox.socialnetwork.config.security.PersonDetailsService;
import ru.skillbox.socialnetwork.exceptions.*;
import ru.skillbox.socialnetwork.model.entities.*;
import ru.skillbox.socialnetwork.model.enums.ReadStatus;
import ru.skillbox.socialnetwork.repositories.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DialogService {
    private final PersonRepository personRepository;
    private final DialogRepository dialogRepository;
    private final PersonToDialogRepository personToDialogRepository;
    private final PersonDetailsService personDetailsService;
    private final MessageRepository messageRepository;
    private final NotificationsRepository notificationsRepository;
    private final NotificationTypeRepository notificationTypeRepository;

    @Autowired
    public DialogService(PersonRepository personRepository, DialogRepository dialogRepository,
                         PersonToDialogRepository personToDialogRepository, PersonDetailsService personDetailsService,
                         MessageRepository messageRepository, NotificationsRepository notificationsRepository,
                         NotificationTypeRepository notificationTypeRepository) {
        this.personRepository = personRepository;
        this.dialogRepository = dialogRepository;
        this.personToDialogRepository = personToDialogRepository;
        this.personDetailsService = personDetailsService;
        this.messageRepository = messageRepository;
        this.notificationsRepository = notificationsRepository;
        this.notificationTypeRepository = notificationTypeRepository;
    }


    public ErrorTimeTotalOffsetPerPageListDataResponse getDialogsLastMessages(DialogRequest dialogRequest) {
        Person currentUser = personDetailsService.getCurrentUser();
        // find where the user is participant
        List<PersonToDialog> personToDialogs = personToDialogRepository.findByPerson(currentUser);
        List<Long> dialogIdsList = new ArrayList<>();
        for (PersonToDialog personToDialog : personToDialogs) {
            dialogIdsList.add(personToDialog.getDialog().getId());
        }

        // if no dialogs
        if (dialogIdsList.isEmpty()) {
            return new ErrorTimeTotalOffsetPerPageListDataResponse(0, dialogRequest.getOffset(),
                    dialogRequest.getItemPerPage(), dialogIdsList);
        }

        List<IdUnreadCountLastMessageResponse> unreadDialogsList = new ArrayList<>();

        // getting paged dialogs response
        int offset = dialogRequest.getOffset();
        int itemPerPage = dialogRequest.getItemPerPage();
        Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage, Sort.by(Sort.Direction.DESC, "unreadCount"));
        List<Long> dialogResponseIdList = new ArrayList<>();
        Page<Dialog> dialogPage = dialogRepository.findByIdIn(dialogIdsList, pageable);
        dialogPage.forEach(d -> dialogResponseIdList.add(d.getId()));

        for (Long dialogId : dialogResponseIdList) {
            Optional<Message> messageOptional = messageRepository.findTopByDialogIdOrderByTimeDesc(dialogId);
            if (messageOptional.isPresent()) {
                unreadDialogsList.add(new IdUnreadCountLastMessageResponse(dialogId,
                        messageOptional.get().getRecipient().getId() == currentUser.getId() ? dialogRepository.findById(dialogId).get().getUnreadCount() : 0,     //unreadCount возвращаем только если пользователь является получателем последнего сообщения
                        messageToResponse(messageOptional.get(), currentUser.getId())));
            } else {
                unreadDialogsList.add(new IdUnreadCountLastMessageResponse(dialogId, 0, new MessageEntityResponse()));
            }
        }

        return new ErrorTimeTotalOffsetPerPageListDataResponse(dialogPage.getTotalElements(), dialogRequest.getOffset(),
                dialogRequest.getItemPerPage(), unreadDialogsList);
    }


    public ErrorTimeTotalOffsetPerPageListDataResponse getDialogsLastMessages() {
        return getDialogsLastMessages(new DialogRequest("", 20, 0));
    }


    public ErrorTimeDataResponse createDialog(List<Long> userIds) {
        Person owner = personDetailsService.getCurrentUser();
        // checking for correct IDs
        for (long id : userIds) {
            if (owner.getId() == id)
                throw new CustomExceptionBadRequest("it's not a good idea talking itself");
            personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException(id));
        }

        Person secondPerson = null;
        for (long userId : userIds) {
            if (userId != owner.getId()) {
                secondPerson = personRepository.findById(userId).get();
            }
        }
        // логика только для двух пользователей в диалоге пользователя, похоже фронт иначе и не умеет. Компаньен всегда в userIds.get(0)
        List<PersonToDialog> PersonToDialogList = personToDialogRepository.findByPerson(owner);
        for (PersonToDialog personToDialog : PersonToDialogList) {
            Dialog tmpDialog = personToDialog.getDialog();
            if (!personToDialogRepository.findByDialogAndPerson(tmpDialog, secondPerson).isEmpty())
                return new ErrorTimeDataResponse(new IdResponse(tmpDialog.getId()));
        }

        Dialog dialog = new Dialog();
        dialog.setIsDeleted(0);
        dialog.setUnreadCount(1);
        dialog.setOwner(owner);
        dialog.setInviteCode(getRandomString(5));
        dialogRepository.save(dialog);
        PersonToDialog personToDialog1 = new PersonToDialog();
        personToDialog1.setDialog(dialog);
        personToDialog1.setPerson(secondPerson);
        personToDialogRepository.save(personToDialog1);
        PersonToDialog personToDialog2 = new PersonToDialog();
        personToDialog2.setDialog(dialog);
        personToDialog2.setPerson(owner);
        personToDialogRepository.save(personToDialog2);


        Message message = new Message();
        message.setAuthor(owner);
        message.setRecipient(secondPerson);
        message.setDialog(dialog);
        message.setText("Start messaging");
        message.setTime(LocalDateTime.now());
        message.setReadStatus(ReadStatus.SENT.name());
        message.setIsDeleted(0);
        messageRepository.save(message);

        return new ErrorTimeDataResponse(new IdResponse(dialog.getId()));
    }

    /**
     * метод не используется
     */

    public ErrorTimeDataResponse addUsersToDialog(Long dialogId, List<Long> userIds) {
        Dialog dialog = dialogRepository.getOne(dialogId);
        for (long id : userIds) {
            // checking for correct IDs
            Person person = personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException(id));
            // checking if person is already in dialog
            // need to introduce to GlobalExceptionHandler
            if (personToDialogRepository.findByDialogAndPerson(dialog, person).isPresent()) {
                throw new CustomException(String.format("Person ID %d is already in dialog!", id));
            }
        }

        for (long id : userIds) {
            PersonToDialog personToDialog = new PersonToDialog();
            personToDialog.setDialog(dialog);
            personToDialog.setPerson(personRepository.findById(id).get());
            personToDialogRepository.save(personToDialog);
        }
        return new ErrorTimeDataResponse(new ListUserIdsResponse(userIds));
    }

    /**
     * метод не используется
     */

    public ErrorTimeDataResponse deleteUsersFromDialog(Long dialogId, List<Long> userIds) {
        // checking for correct IDs
        for (long id : userIds) {
            personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException(id));
        }

        Dialog dialog = dialogRepository.findById(dialogId).orElseThrow(() -> new DialogNotFoundException(dialogId));

        // getting list of users in dialog
        //TODO: question - do we need to check if person is in dialog? Just ignore or this is covered by frontend)?
        // Possibly - reply contains only users that were removed
        List<PersonToDialog> personsToDialog = personToDialogRepository.findByDialog(dialog);
        // going through user list
        for (PersonToDialog personToDialog : personsToDialog) {
            if (userIds.contains(personToDialog.getPerson().getId())) {
                personToDialogRepository.delete(personToDialog);
            }
        }
        return new ErrorTimeDataResponse(new ListUserIdsResponse(userIds));
    }

    /**
     * метод не используется
     */

    public ErrorTimeDataResponse getInviteLink(Long dialogId) {
        Dialog dialog = dialogRepository.findById(dialogId).orElseThrow(() -> new DialogNotFoundException(dialogId));
        String inviteLink = dialog.getInviteCode(); //TODO just code or full URL?
        return new ErrorTimeDataResponse(new LinkResponse(inviteLink));
    }

    /**
     * метод не используется
     */

    public ErrorTimeDataResponse joinByInvite(Long dialogId, LinkRequest inviteLink) {
        Dialog dialog = dialogRepository.findById(dialogId).orElseThrow(() -> new DialogNotFoundException(dialogId));
        List<Long> idsList = new ArrayList<>();
        idsList.add(personDetailsService.getCurrentUser().getId());
        if (inviteLink.getLink().equals(dialog.getInviteCode())) {
            return addUsersToDialog(dialogId, idsList);
        } else {
            return new ErrorTimeDataResponse(new ErrorErrorDescriptionResponse("incorrect_code")); //TODO ?????????????????
        }
    }


    public ErrorTimeTotalOffsetPerPageListDataResponse getMessagesByDialogId(Long dialogId, String query, Integer offset, Integer limit) {
        dialogRepository.findById(dialogId).orElseThrow(() -> new DialogNotFoundException(dialogId));
        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<Message> pageMessage = messageRepository.findMessageWithQueryWithPagination(query, dialogId, pageable);
        // marking message as read if current user is recipient
        boolean unreadFound = false;
        for (Message message : pageMessage) {
            if (message.getReadStatus().equals(ReadStatus.SENT.toString())
                    && message.getRecipient() == personDetailsService.getCurrentUser()) {
                message.setReadStatus(ReadStatus.READ.toString());
//                message.setText(message.getText() + "[READ]");
                unreadFound = true;
                messageRepository.save(message);
            }
        }
        if (unreadFound)
            dialogRepository.resetUnreadCountById(dialogId);
        long currentUserId = personDetailsService.getCurrentUser().getId();
        return new ErrorTimeTotalOffsetPerPageListDataResponse(pageMessage.getTotalElements(), offset, limit,
                pageMessage.stream().map(message -> messageToResponse(message, currentUserId))
                        .collect(Collectors.toList()));

    }


    public ErrorTimeDataResponse sendMessage(Long dialogId, MessageTextRequest messageTextRequest) {
        Long recipientId = null;
        Dialog dialog = dialogRepository.findById(dialogId).orElseThrow(() -> new DialogNotFoundException(dialogId));
        if (messageTextRequest.getMessageText() == null || messageTextRequest.getMessageText().isEmpty()) {
            throw new MessageEmptyException();
        }
        Message message = new Message();
        LocalDateTime timeMessage = LocalDateTime.ofInstant(Instant
                        .ofEpochMilli(System.currentTimeMillis()),
                TimeZone.getDefault().toZoneId());
        long authorId = personDetailsService.getCurrentUser().getId();
        message.setAuthor(personRepository.findById(authorId)
                .orElseThrow(() -> new PersonNotFoundException(authorId)));

        // logic works for 2 persons in dialog: if person not author, then recipient
        List<PersonToDialog> personToDialogList = personToDialogRepository.findByDialog(dialog);
        for (PersonToDialog p2d : personToDialogList) {
            if (p2d.getPerson().getId() != authorId) {
                recipientId = p2d.getPerson().getId();
            }
        }

        Long finalRecipientId = recipientId; // lambda workaround
        message.setRecipient(personRepository.findById(recipientId).orElseThrow(() -> new PersonNotFoundException(finalRecipientId)));
        message.setDialog(dialogRepository.findById(dialogId).get());
        message.setText(messageTextRequest.getMessageText());
        message.setTime(timeMessage);
        message.setReadStatus(ReadStatus.SENT.name());
        message.setIsDeleted(0);
        Message savedMessage = messageRepository.save(message);
        MessageEntityResponse messageEntityResponse = messageToResponse(message, authorId);
        dialogRepository.incrementUnreadCountById(dialogId);

        notificationsRepository.save(new Notification(
                notificationTypeRepository.findByName("MESSAGE").get(),
                getMillisecondsToLocalDateTime(System.currentTimeMillis()),
                personRepository.findById(recipientId).get(),
                savedMessage.getId(),
                personRepository.findById(recipientId).get().getEmail(),
                0
        ));

        return new ErrorTimeDataResponse(messageEntityResponse);
    }


    public ErrorTimeDataResponse getPersonStatus(Long dialogId, Long personId) {
        dialogRepository.findById(dialogId).orElseThrow(() -> new DialogNotFoundException(dialogId));
        Person person = personRepository.findById(personId).orElseThrow(() -> new PersonNotFoundException(personId));
        OnlineLastActivityResponse response = new OnlineLastActivityResponse(
                person.getIsOnline() == 1,
                person.getLastOnlineTime().atZone(TimeZone.getDefault().toZoneId()).toInstant().toEpochMilli());
        return new ErrorTimeDataResponse(response);
    }

    /**
     * метод не используется
     */

    public ErrorTimeDataResponse setPersonStatus(Long dialogId, Long personId) {
        // MOCK
        dialogRepository.findById(dialogId).orElseThrow(() -> new DialogNotFoundException(dialogId));
        personRepository.findById(personId).orElseThrow(() -> new PersonNotFoundException(personId));

        return new ErrorTimeDataResponse(new MessageResponse());
    }

    /**
     * метод не используется
     */

    public ErrorTimeDataResponse deleteDialog(Long id) {
        if (dialogRepository.findById(id).isEmpty())
            throw new DialogNotFoundException(id);
        dialogRepository.deleteById(id);
        return new ErrorTimeDataResponse(new IdResponse(id));
    }

    /**
     * метод не используется?
     */

    public ErrorTimeDataResponse deleteMessage(Long dialogId, Long messageId) {
        dialogRepository.findById(dialogId).orElseThrow(() -> new DialogNotFoundException(dialogId));
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(messageId));
        message.setIsDeleted(1);
        messageRepository.save(message);
        return new ErrorTimeDataResponse(new MessageIdResponse(messageId));
    }

    /**
     * метод не используется
     */

    public ErrorTimeDataResponse recoverMessage(Long dialogId, Long messageId) {
        dialogRepository.findById(dialogId).orElseThrow(() -> new DialogNotFoundException(dialogId));
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(messageId));
        message.setIsDeleted(0);
        messageRepository.save(message);
        long currentUserId = personDetailsService.getCurrentUser().getId();
        return new ErrorTimeDataResponse(messageToResponse(message, currentUserId));
    }

    /**
     * метод не используется
     */

    public ErrorTimeDataResponse markReadMessage(Long dialogId, Long messageId) {
        dialogRepository.findById(dialogId).orElseThrow(() -> new DialogNotFoundException(dialogId));
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(messageId));
        message.setReadStatus(ReadStatus.READ.toString());
        messageRepository.save(message);
        return new ErrorTimeDataResponse(new MessageResponse());
    }

    /**
     * метод не используется
     */

    public ErrorTimeDataResponse changeMessage(Long dialogId, Long messageId, MessageTextRequest messageTextRequest) {
        dialogRepository.findById(dialogId).orElseThrow(() -> new DialogNotFoundException(dialogId));
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(messageId));
        if (messageTextRequest.getMessageText() == null || messageTextRequest.getMessageText().isEmpty()) {
            throw new MessageEmptyException(messageId);
        }
        message.setText(messageTextRequest.getMessageText());
        messageRepository.save(message);
        long currentUserId = personDetailsService.getCurrentUser().getId();
        return new ErrorTimeDataResponse(messageToResponse(message, currentUserId));
    }


    public ErrorTimeDataResponse getNewMessagesCount() {
        Person person = personDetailsService.getCurrentUser();
        Long count = person.getMessages().stream().filter(
                readStatus -> readStatus.getReadStatus().equals(ReadStatus.SENT.toString())
        ).count();

        return new ErrorTimeDataResponse(new CountResponse(count));
    }

    private String getRandomString(int length) {
        int leftLimit = 48; // '0'
        int rightLimit = 122; // 'z'
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private MessageEntityResponse messageToResponse(Message message, long currentPersonId) {
        //Person recipient = message.getRecipient();
        Person recipient = message.getAuthor().getId() == currentPersonId ? message.getRecipient() : message.getAuthor(); //костыль
        return MessageEntityResponse.builder()
                .id(message.getId())
                .isSentByMe(message.getAuthor().getId() == currentPersonId)
                .authorId(message.getAuthor().getId())
                .recipient(PersonEntityResponse.builder()
                        .email(recipient.getEmail())
                        .firstName(recipient.getFirstName())
                        .lastName(recipient.getLastName())
                        .id(recipient.getId())
                        .photo(recipient.getPhoto())
                        .lastOnlineTime(recipient.getLastOnlineTime().atZone(TimeZone.getDefault().toZoneId()).toInstant().toEpochMilli())
                        .isBlocked(recipient.isBlocked())
                        .build()
                )
                .messageText(message.getText())
                .timestamp(message.getTime().atZone(TimeZone.getDefault().toZoneId()).toInstant().toEpochMilli())
                .readStatus(message.getReadStatus())
                .build();
    }

    private LocalDateTime getMillisecondsToLocalDateTime(long milliseconds) {
        return Instant.ofEpochMilli(milliseconds).atZone(ZoneId.systemDefault()).toLocalDateTime();

    }

}