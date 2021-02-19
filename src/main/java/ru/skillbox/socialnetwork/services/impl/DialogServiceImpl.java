package ru.skillbox.socialnetwork.services.impl;

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
import ru.skillbox.socialnetwork.model.entity.*;
import ru.skillbox.socialnetwork.model.enums.ReadStatus;
import ru.skillbox.socialnetwork.repository.*;
import ru.skillbox.socialnetwork.security.PersonDetailsService;
import ru.skillbox.socialnetwork.services.DialogService;
import ru.skillbox.socialnetwork.services.exceptions.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DialogServiceImpl implements DialogService {
    private final PersonRepository personRepository;
    private final DialogRepository dialogRepository;
    private final PersonToDialogRepository personToDialogRepository;
    private final PersonDetailsService personDetailsService;
    private final MessageRepository messageRepository;
    private final NotificationsRepository notificationsRepository;
    private final NotificationTypeRepository notificationTypeRepository;

    private Long notType = 5L;

    @Autowired
    public DialogServiceImpl(PersonRepository personRepository, DialogRepository dialogRepository,
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

    @Override
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
            return new ErrorTimeTotalOffsetPerPageListDataResponse("", System.currentTimeMillis(), 0,
                    dialogRequest.getOffset(),
                    dialogRequest.getItemPerPage(),
                    dialogIdsList); // TODO
        }

        List<IdUnreadCountLastMessageResponse> unreadDialogsList = new ArrayList<>();

        // getting paged dialogs response
        int offset = dialogRequest.getOffset();
        int itemPerPage = dialogRequest.getItemPerPage();
        Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage, Sort.by(Sort.Direction.ASC, "id"));
        List<Long> dialogResponseIdList = new ArrayList<>();
        Page<Dialog> dialogPage = dialogRepository.findByIdIn(dialogIdsList, pageable);
        dialogPage.forEach(d -> dialogResponseIdList.add(d.getId()));

        for (Long dialogId : dialogResponseIdList) {
//            // placeholder message
//            LocalDateTime timeMessage = LocalDateTime.ofInstant(Instant
//                            .ofEpochMilli(System.currentTimeMillis()),
//                    TimeZone.getDefault().toZoneId());
//            Message placeholderMessage = new Message();
//            placeholderMessage.setId(1L);
//            placeholderMessage.setReadStatus(ReadStatus.SENT.name());
//            placeholderMessage.setText("this is a placeholder");
//            placeholderMessage.setAuthor(currentUser);
//            placeholderMessage.setDialog(dialogRepository.getOne(dialogId));
//            placeholderMessage.setIsDeleted(0);
//            placeholderMessage.setTime(timeMessage);
//            placeholderMessage.setRecipient(personRepository.findById(10L).get());
            Optional<Message> messageOptional = messageRepository.findTopByDialogIdOrderByTimeDesc(dialogId);
            if (messageOptional.isPresent()) {
                unreadDialogsList.add(new IdUnreadCountLastMessageResponse(dialogId,
                        dialogRepository.findById(dialogId).get().getUnreadCount(),
                        messageToResponse(messageOptional.get())));
            } else {
                unreadDialogsList.add(new IdUnreadCountLastMessageResponse(dialogId, 0, new MessageEntityResponse()));
            }
        }

        return new ErrorTimeTotalOffsetPerPageListDataResponse("", System.currentTimeMillis(), dialogPage.getTotalElements(),
                dialogRequest.getOffset(),
                dialogRequest.getItemPerPage(),
                unreadDialogsList);
    }

    @Override
    public ErrorTimeTotalOffsetPerPageListDataResponse getDialogsLastMessages() {
        return getDialogsLastMessages(new DialogRequest("", 20, 0));
    }

//    @Override
//    public ErrorTimeTotalOffsetPerPageListDataResponse getDialogsList(DialogRequest dialogRequest) {
//        Person currentUser = personDetailsService.getCurrentUser();
//        // find where the user is participant
//        List<PersonToDialog> personToDialogs = personToDialogRepository.findByPerson(currentUser);
//        List<Long> dialogIdsList = new ArrayList<>();
//        for (PersonToDialog personToDialog : personToDialogs) {
//            dialogIdsList.add(personToDialog.getDialog().getId());
//        }
//        // getting paged response
//        int offset = dialogRequest.getOffset();
//        int itemPerPage = dialogRequest.getItemPerPage();
//        Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage, Sort.by(Sort.Direction.ASC, "id"));
//        List<Dialog> dialogResponseList = new ArrayList<>();
//        Page<Dialog> dialogPage = dialogRepository.findByIdIn(dialogIdsList, pageable);
//        dialogPage.forEach(dialogResponseList::add);
//        return new ErrorTimeTotalOffsetPerPageListDataResponse(
//                "",
//                System.currentTimeMillis(),
//                dialogPage.getTotalElements(),
//                offset,
//                itemPerPage,
//                dialogResponseList);
//    }
//
//    @Override
//    public ErrorTimeTotalOffsetPerPageListDataResponse getDialogsList() {
//        return getDialogsList(new DialogRequest("", 20, 0));
//    }

    @Override
    public ErrorTimeDataResponse createDialog(List<Long> userIds) {
        // checking for correct IDs
        for (long id : userIds) {
            personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException(id));
        }
        Person owner = personDetailsService.getCurrentUser();
        if (!userIds.contains(owner.getId())) {
            userIds.add(owner.getId());
        }
        Dialog dialog = new Dialog();
        dialog.setIsDeleted(0);
        dialog.setUnreadCount(0);
        dialog.setOwner(owner);
        dialog.setInviteCode(getRandomString(5));
        dialogRepository.save(dialog);
        for (long id : userIds) {
            PersonToDialog personToDialog = new PersonToDialog();
            personToDialog.setDialog(dialog);
            personToDialog.setPerson(personRepository.findById(id).get());
            personToDialogRepository.save(personToDialog);
        }

        Message message = new Message();
        message.setAuthor(owner);
        message.setRecipient(owner);
        message.setDialog(dialog);
        message.setText("Start messaging");
        message.setTime(LocalDateTime.now());
        message.setReadStatus(ReadStatus.SENT.name());
        message.setIsDeleted(0);
        messageRepository.save(message);

        return new ErrorTimeDataResponse("",
                new IdResponse(dialog.getId()));
    }

    @Override
    public ErrorTimeDataResponse addUsersToDialog(Long dialogId, List<Long> userIds) {
        Dialog dialog = dialogRepository.getOne(dialogId);
        for (long id : userIds) {
            // checking for correct IDs
            Person person = personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException(id));
            // checking if person is already in dialog
            // need to introduce to GlobalExceptionHandler
            if (!personToDialogRepository.findByDialogAndPerson(dialog, person).isEmpty()) {
                throw new CustomException(String.format("Person ID %d is already in dialog!", id));
            }
        }

        for (long id : userIds) {
            PersonToDialog personToDialog = new PersonToDialog();
            personToDialog.setDialog(dialog);
            personToDialog.setPerson(personRepository.findById(id).get());
            personToDialogRepository.save(personToDialog);
        }
        return new ErrorTimeDataResponse("", new ListUserIdsResponse(userIds));
    }

    @Override
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
        return new ErrorTimeDataResponse("", new ListUserIdsResponse(userIds));
    }

    @Override
    public ErrorTimeDataResponse getInviteLink(Long dialogId) {
        Dialog dialog = dialogRepository.findById(dialogId).orElseThrow(() -> new DialogNotFoundException(dialogId));
        String inviteLink = dialog.getInviteCode(); // just code or full URL?
        return new ErrorTimeDataResponse("", new LinkResponse(inviteLink));
    }

    @Override
    public ErrorTimeDataResponse joinByInvite(Long dialogId, LinkRequest inviteLink) {
        Dialog dialog = dialogRepository.findById(dialogId).orElseThrow(() -> new DialogNotFoundException(dialogId));
        List<Long> idsList = new ArrayList<>();
        idsList.add(personDetailsService.getCurrentUser().getId());
        if (inviteLink.getLink().equals(dialog.getInviteCode())) {
            return addUsersToDialog(dialogId, idsList);
        } else {
            return new ErrorTimeDataResponse("", new ErrorErrorDescriptionResponse("incorrect_code"));
        }
    }

    @Override
    public ErrorTimeTotalOffsetPerPageListDataResponse getMessagesByDialogId(Long dialogId, String query, Integer offset, Integer limit) {
        dialogRepository.findById(dialogId).orElseThrow(() -> new DialogNotFoundException(dialogId));
        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<Message> pageMessage = messageRepository.findMessageWithQueryWithPagination(query, dialogId, pageable);
        return new ErrorTimeTotalOffsetPerPageListDataResponse("",
                System.currentTimeMillis(),
                pageMessage.getTotalElements(),
                offset, limit, pageMessage.stream().map(this::messageToResponse).collect(Collectors.toList()));

    }

    @Override
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
        MessageEntityResponse messageEntityResponse = messageToResponse(message);

        notificationsRepository.save(new Notification(
           notificationTypeRepository.findByName("MESSAGE").get(),
           getMillisecondsToLocalDateTime(System.currentTimeMillis()),
           personRepository.findById(recipientId).get(),
           savedMessage.getId(),
           personRepository.findById(recipientId).get().getEmail(),
           0
        ));

        return new ErrorTimeDataResponse("", messageEntityResponse);
    }

    @Override
    public ErrorTimeDataResponse getPersonStatus(Long dialogId, Long personId) {
        dialogRepository.findById(dialogId).orElseThrow(() -> new DialogNotFoundException(dialogId));
        Person person = personRepository.findById(personId).orElseThrow(() -> new PersonNotFoundException(personId));
        OnlineLastActivityResponse response = new OnlineLastActivityResponse(
                person.getIsOnline() == 1,
                person.getLastOnlineTime().atZone(TimeZone.getDefault().toZoneId()).toInstant().toEpochMilli());
        return new ErrorTimeDataResponse("", response);
    }

    @Override
    public ErrorTimeDataResponse setPersonStatus(Long dialogId, Long personId) {
        // MOCK
        dialogRepository.findById(dialogId).orElseThrow(() -> new DialogNotFoundException(dialogId));
        personRepository.findById(personId).orElseThrow(() -> new PersonNotFoundException(personId));

        return new ErrorTimeDataResponse("", new MessageResponse());
    }

    @Override
    public ErrorTimeDataResponse deleteDialog(Long id) {
        if (dialogRepository.findById(id).isEmpty())
            throw new DialogNotFoundException(id);
        dialogRepository.deleteById(id);
        return new ErrorTimeDataResponse("", new IdResponse(id));
    }

    @Override
    public ErrorTimeDataResponse deleteMessage(Long dialogId, Long messageId) {
        dialogRepository.findById(dialogId).orElseThrow(() -> new DialogNotFoundException(dialogId));
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(messageId));
        message.setIsDeleted(1);
        messageRepository.save(message);
        return new ErrorTimeDataResponse("", new MessageIdResponse(messageId));
    }

    @Override
    public ErrorTimeDataResponse recoverMessage(Long dialogId, Long messageId) {
        dialogRepository.findById(dialogId).orElseThrow(() -> new DialogNotFoundException(dialogId));
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(messageId));
        message.setIsDeleted(0);
        messageRepository.save(message);
        return new ErrorTimeDataResponse("", messageToResponse(message));
    }

    @Override
    public ErrorTimeDataResponse markReadMessage(Long dialogId, Long messageId) {
        dialogRepository.findById(dialogId).orElseThrow(() -> new DialogNotFoundException(dialogId));
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(messageId));
        message.setReadStatus(ReadStatus.READ.toString());
        messageRepository.save(message);
        return new ErrorTimeDataResponse("", new MessageResponse());
    }

    @Override
    public ErrorTimeDataResponse changeMessage(Long dialogId, Long messageId, MessageTextRequest messageTextRequest) {
        dialogRepository.findById(dialogId).orElseThrow(() -> new DialogNotFoundException(dialogId));
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(messageId));
        if (messageTextRequest.getMessageText() == null || messageTextRequest.getMessageText().isEmpty()) {
            throw new MessageEmptyException(messageId);
        }
        message.setText(messageTextRequest.getMessageText());
        messageRepository.save(message);
        return new ErrorTimeDataResponse("", messageToResponse(message));
    }

    @Override
    public ErrorTimeDataResponse getNewMessagesCount() {
        Person person = personDetailsService.getCurrentUser();
        Long count = person.getMessages().stream().filter(
                readStatus -> readStatus.getReadStatus().equals(ReadStatus.SENT.toString())
        ).count();

        return new ErrorTimeDataResponse("", new CountResponse(count));
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

    private MessageEntityResponse messageToResponse(Message message) {
        return MessageEntityResponse.builder()
                .id(message.getId())
                .authorId(message.getAuthor().getId())
                .recipientId(message.getRecipient().getId())
                .messageText(message.getText())
                .timestamp(message.getTime().atZone(TimeZone.getDefault().toZoneId()).toInstant().toEpochMilli())
                .readStatus(message.getReadStatus())
                .build();
    }

    private LocalDateTime getMillisecondsToLocalDateTime(long milliseconds) {
        return Instant.ofEpochMilli(milliseconds).atZone(ZoneId.systemDefault()).toLocalDateTime();

    }

}