package ru.skillbox.socialnetwork.services.impl;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeDataResponse;
import ru.skillbox.socialnetwork.api.responses.IdResponse;
import ru.skillbox.socialnetwork.api.responses.LinkResponse;
import ru.skillbox.socialnetwork.api.responses.ListUserIdsResponse;
import ru.skillbox.socialnetwork.model.entity.Dialog;
import ru.skillbox.socialnetwork.model.entity.Person;
import ru.skillbox.socialnetwork.model.entity.PersonToDialog;
import ru.skillbox.socialnetwork.repository.DialogRepository;
import ru.skillbox.socialnetwork.repository.PersonRepository;
import ru.skillbox.socialnetwork.repository.PersonToDialogRepository;
import ru.skillbox.socialnetwork.services.AccountService;
import ru.skillbox.socialnetwork.services.DialogService;
import ru.skillbox.socialnetwork.services.exceptions.DialogNotFoundException;
import ru.skillbox.socialnetwork.services.exceptions.PersonNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.Random;

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
    public ErrorTimeDataResponse addUserToDialog(Long dialogId, List<Long> userIds){
        // checking for correct IDs
        for (long id: userIds) {
            personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException(id));
        }

        Dialog dialog = dialogRepository.getOne(dialogId);

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
