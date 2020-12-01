package ru.skillbox.socialnetwork.security;


import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.model.entity.Person;
import ru.skillbox.socialnetwork.model.repositiry.PersonRepository;

@Service
class PersonDetailsService implements UserDetailsService {

  private final PersonRepository personRepository;

  public PersonDetailsService(PersonRepository personRepository) {
    this.personRepository = personRepository;
  }

  @Override
  public PersonDetails loadUserByUsername(String email) throws UsernameNotFoundException {

    Person per = personRepository.findByEmail(email);
    if (per == null) {
      throw new UsernameNotFoundException(" - User with : " + email + " not found");
    }
    return PersonDetails.fromUser(per);
  }
}
