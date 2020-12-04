package ru.skillbox.socialnetwork.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.model.entity.Person;
import ru.skillbox.socialnetwork.repository.PersonRepository;

@Service
public class AccountService {

    private final PersonRepository personRepository;

    @Autowired
    public AccountService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    Person getCurrentUser() {
        return null;
    }
}
