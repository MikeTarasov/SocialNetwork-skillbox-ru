package ru.skillbox.socialnetwork.config.security;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.exceptions.PersonNotFoundException;
import ru.skillbox.socialnetwork.exceptions.UnauthorizedException;
import ru.skillbox.socialnetwork.model.entities.Person;
import ru.skillbox.socialnetwork.repositories.PersonRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PersonDetailsService implements UserDetailsService {

    private final PersonRepository personRepository;

    public PersonDetailsService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public PersonDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<Person> per = personRepository.findByEmail(email);
        if (per.isEmpty()) {
            throw new UnauthorizedException(email);
        }
        return PersonDetails.fromUser(per.get());
    }

    public Person getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            throw new SecurityException("Session is not authorized");
        }

        String email = auth.getName();

        Optional<Person> per = personRepository.findByEmail(email);

        if (per.isEmpty()) {
            throw new UnauthorizedException(email);
        }

        return per.get();
    }

    public void updateLastOnline(String username) {
        Person person = personRepository.findByEmail(username).orElseThrow(() -> new PersonNotFoundException(username));
        person.setLastOnlineTime(LocalDateTime.now());
        personRepository.save(person);
    }
}