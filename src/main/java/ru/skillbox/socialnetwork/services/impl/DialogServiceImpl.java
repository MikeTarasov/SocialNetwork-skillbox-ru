package ru.skillbox.socialnetwork.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.api.requests.DialogRequest;
import ru.skillbox.socialnetwork.api.requests.LinkRequest;
import ru.skillbox.socialnetwork.api.requests.MessageRequest;
import ru.skillbox.socialnetwork.api.responses.*;
import ru.skillbox.socialnetwork.model.entity.Dialog;
import ru.skillbox.socialnetwork.model.entity.Message;
import ru.skillbox.socialnetwork.model.entity.Person;
import ru.skillbox.socialnetwork.model.entity.PersonToDialog;
import ru.skillbox.socialnetwork.model.enums.ReadStatus;
import ru.skillbox.socialnetwork.repository.DialogRepository;
import ru.skillbox.socialnetwork.repository.MessageRepository;
import ru.skillbox.socialnetwork.repository.PersonRepository;
import ru.skillbox.socialnetwork.repository.PersonToDialogRepository;
import ru.skillbox.socialnetwork.services.AccountService;
import ru.skillbox.socialnetwork.services.DialogService;
import ru.skillbox.socialnetwork.services.exceptions.CustomException;
import ru.skillbox.socialnetwork.services.exceptions.DialogNotFoundException;
import ru.skillbox.socialnetwork.services.exceptions.PersonNotFoundException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

@Service
public class DialogServiceImpl implements DialogService {
    private final PersonRepository personRepository;
    private final DialogRepository dialogRepository;
    private final PersonToDialogRepository personToDialogRepository;
    private final AccountService accountService;
    private final MessageRepository messageRepository;

    @Autowired
    public DialogServiceImpl(PersonRepository personRepository, DialogRepository dialogRepository, PersonToDialogRepository personToDialogRepository, AccountService accountService, MessageRepository messageRepository) {
        this.personRepository = personRepository;
        this.dialogRepository = dialogRepository;
        this.personToDialogRepository = personToDialogRepository;
        this.accountService = accountService;
        this.messageRepository = messageRepository;
    }

    @Override
    public ErrorTimeTotalOffsetPerPageListDataResponse getDialogsList(DialogRequest dialogRequest){
        Person currentUser = accountService.getCurrentUser();
        // find where the user is participant
        List<PersonToDialog> personToDialogs = personToDialogRepository.findByPerson(currentUser);
        List<Long> dialogIdsList = new ArrayList<>();
        for (PersonToDialog personToDialog: personToDialogs){
            dialogIdsList.add(personToDialog.getDialog().getId());
        }
        // getting paged response
        int offset = dialogRequest.getOffset();
        int itemPerPage = dialogRequest.getItemPerPage();
        Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage, Sort.by(Sort.Direction.ASC, "id"));
        List<Dialog> dialogResponseList = new ArrayList<>();
        Page<Dialog> dialogPage = dialogRepository.findByIdIn(dialogIdsList, pageable);
        dialogPage.forEach(dialogResponseList::add);
        return new ErrorTimeTotalOffsetPerPageListDataResponse(
                "",
                System.currentTimeMillis(),
                dialogPage.getTotalElements(),
                offset,
                itemPerPage,
                dialogResponseList);
    }

    @Override
    public ErrorTimeTotalOffsetPerPageListDataResponse getDialogsList() {
        return getDialogsList(new DialogRequest("", 20, 0));
    }

    @Override
    public ErrorTimeDataResponse createDialog(List<Long> userIds) {
        // checking for correct IDs
        for (long id: userIds) {
            personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException(id));
        }
        Person owner = accountService.getCurrentUser();
        Dialog dialog = new Dialog();
        dialog.setIsDeleted(0);
        dialog.setUnreadCount(0);
        dialog.setOwner(owner);
        dialog.setInviteCode(getRandomString(5));
        dialogRepository.save(dialog);
        for (long id: userIds) {
            PersonToDialog personToDialog = new PersonToDialog();
            personToDialog.setDialog(dialog);
            personToDialog.setPerson(personRepository.findById(id).get());
            personToDialogRepository.save(personToDialog);
        }

        return new ErrorTimeDataResponse("",
                new IdResponse(dialog.getId()));
    }

    @Override
    public ErrorTimeDataResponse addUsersToDialog(Long dialogId, List<Long> userIds){
        Dialog dialog = dialogRepository.getOne(dialogId);
        for (long id: userIds) {
            // checking for correct IDs
            Person person = personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException(id));
            // checking if person is already in dialog
            // need to introduce to GlobalExceptionHandler
            if (!personToDialogRepository.findByDialogAndPerson(dialog, person).isEmpty()){
                throw new CustomException(String.format("Person ID %d is already in dialog!", id));
            }
        }

            for (long id: userIds) {
                PersonToDialog personToDialog = new PersonToDialog();
                personToDialog.setDialog(dialog);
                personToDialog.setPerson(personRepository.findById(id).get());
                personToDialogRepository.save(personToDialog);
            }
        return new ErrorTimeDataResponse("", new ListUserIdsResponse(userIds));
    }

    @Override
    public ErrorTimeDataResponse deleteUsersFromDialog(Long dialogId, List<Long> userIds){
        // checking for correct IDs
        for (long id: userIds) {
            personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException(id));
        }

        Dialog dialog = dialogRepository.findById(dialogId).orElseThrow(() -> new DialogNotFoundException(dialogId));

        // getting list of users in dialog
        //TODO: question - do we need to check if person is in dialog? Just ignore or this is covered by frontend)?
        // Possibly - reply contains only users that were removed
        List<PersonToDialog> personsToDialog = personToDialogRepository.findByDialog(dialog);
        // going through user list
        for (PersonToDialog personToDialog: personsToDialog) {
            if (userIds.contains(personToDialog.getPerson().getId())){
                personToDialogRepository.delete(personToDialog);
            }
        }
        return new ErrorTimeDataResponse("", new ListUserIdsResponse(userIds));
    }

    @Override
    public ErrorTimeDataResponse getInviteLink(Long dialogId){
        Dialog dialog = dialogRepository.findById(dialogId).orElseThrow(() -> new DialogNotFoundException(dialogId));
        String inviteLink = dialog.getInviteCode(); // just code or full URL?
        return new ErrorTimeDataResponse("", new LinkResponse(inviteLink));
    }

    @Override
    public ErrorTimeDataResponse joinByInvite(Long dialogId, LinkRequest inviteLink) {
        Dialog dialog = dialogRepository.findById(dialogId).orElseThrow(() -> new DialogNotFoundException(dialogId));
        List<Long> idsList = new ArrayList<>();
        idsList.add(accountService.getCurrentUser().getId());
        if (inviteLink.getLink().equals(dialog.getInviteCode())){
            return addUsersToDialog(dialogId, idsList);
        }
        else {
            return new ErrorTimeDataResponse("", new ErrorErrorDescriptionResponse("incorrect_code"));
        }
    }
    @Override
    public ErrorTimeDataResponse getMessagesById(Long id, String query, Integer offset, Integer limit){
        Dialog dialog = dialogRepository.findById(id).orElseThrow(() -> new DialogNotFoundException(id));
        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<Message> pageMessage = messageRepository.findMessageWithQueryWithPagination(query,id,pageable);
        return new ErrorTimeDataResponse("", pageMessage);
    }
    @Override
    public ErrorTimeDataResponse sendMessage(Long id, MessageRequest messageRequest){
        Dialog dialog = dialogRepository.findById(id).orElseThrow(() -> new DialogNotFoundException(id));
        Message message = new Message();
        LocalDateTime timeMessage = LocalDateTime.ofInstant(Instant
                        .ofEpochMilli(messageRequest.getTime()),
                         TimeZone.getDefault().toZoneId());
        message.setAuthor(personRepository.findById(messageRequest.getAuthorId()).get());
        message.setDialog(dialogRepository.findById(id).get());
        message.setText(messageRequest.getMessageText());
        message.setTime(timeMessage);
        message.setReadStatus(ReadStatus.SENT.name());
        message.setIsDeleted(0);
        messageRepository.save(message);
        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setMessage(messageRequest.getMessageText());
        return new ErrorTimeDataResponse("", messageResponse);
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
    public ErrorTimeDataResponse deleteDialog(Long id) {
        if (dialogRepository.findById(id).isEmpty())
            throw new DialogNotFoundException(id);
        dialogRepository.deleteById(id);
        return new ErrorTimeDataResponse("", new IdResponse(id));
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


}
