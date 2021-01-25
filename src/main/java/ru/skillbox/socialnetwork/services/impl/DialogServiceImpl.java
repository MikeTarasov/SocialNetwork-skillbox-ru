package ru.skillbox.socialnetwork.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.api.requests.LinkRequest;
import ru.skillbox.socialnetwork.api.responses.*;
import ru.skillbox.socialnetwork.model.entity.Dialog;
import ru.skillbox.socialnetwork.model.entity.Person;
import ru.skillbox.socialnetwork.model.entity.PersonToDialog;
import ru.skillbox.socialnetwork.repository.DialogRepository;
import ru.skillbox.socialnetwork.repository.PersonRepository;
import ru.skillbox.socialnetwork.repository.PersonToDialogRepository;
import ru.skillbox.socialnetwork.services.AccountService;
import ru.skillbox.socialnetwork.services.DialogService;
import ru.skillbox.socialnetwork.services.exceptions.CustomException;
import ru.skillbox.socialnetwork.services.exceptions.DialogNotFoundException;
import ru.skillbox.socialnetwork.services.exceptions.PersonNotFoundException;

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

    @Autowired
    public DialogServiceImpl(PersonRepository personRepository, DialogRepository dialogRepository, PersonToDialogRepository personToDialogRepository, AccountService accountService) {
        this.personRepository = personRepository;
        this.dialogRepository = dialogRepository;
        this.personToDialogRepository = personToDialogRepository;
        this.accountService = accountService;
    }

    @Override
    public ErrorTimeTotalOffsetPerPageListDataResponse getDialogsList(String query, Integer offset, Integer itemPerPage){
        Person currentUser = accountService.getCurrentUser();
        // find where the user is participant
        List<PersonToDialog> personToDialogs = personToDialogRepository.findByPerson(currentUser);
        List<Dialog> dialogList = new ArrayList<>();
        for (PersonToDialog personToDialog: personToDialogs){
            dialogList.add(personToDialog.getDialog());
        }
        return new ErrorTimeTotalOffsetPerPageListDataResponse(
                "", 111, dialogList.size(), offset, itemPerPage, dialogList);
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
    public ErrorTimeDataResponse getPersonStatus(Long dialogId, Long personId) {
        dialogRepository.findById(dialogId).orElseThrow(() -> new DialogNotFoundException(dialogId));
        Person person = personRepository.findById(personId).orElseThrow(() -> new PersonNotFoundException(personId));
        OnlineLastActivityResponse response = new OnlineLastActivityResponse(
                person.getIsOnline() == 1,
                person.getLastOnlineTime().atZone(TimeZone.getDefault().toZoneId()).toInstant().toEpochMilli());
        return new ErrorTimeDataResponse("", response);
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
