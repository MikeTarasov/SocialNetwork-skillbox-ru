package ru.skillbox.socialnetwork.services;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

  public Person getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    if (auth == null) {
      throw new SecurityException("Session is not authorized");
    }

    String email = auth.getName();

    Optional<Person> per = personRepository.findByEmail(email);

    if (per.isEmpty()) {
      throw new UsernameNotFoundException(" - User with : " + email + " not found");
    }

    return per.get();
  }
}
