package ru.skillbox.socialnetwork.security;


import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.model.entity.Person;
import ru.skillbox.socialnetwork.repository.PersonRepository;

@Service
class PersonDetailsService implements UserDetailsService {

  private final PersonRepository personRepository;

  public PersonDetailsService(PersonRepository personRepository) {
    this.personRepository = personRepository;
  }

  @Override
  public PersonDetails loadUserByUsername(String email) throws UsernameNotFoundException {

    Optional<Person> per = personRepository.findByEmail(email);

    if (per.isEmpty()) {
      throw new UsernameNotFoundException(" - User with : " + email + " not found");
    }
    return PersonDetails.fromUser(per.get());
  }
}
