package by.ladz.gusakov.SocialMedialApp.service;

import by.ladz.gusakov.SocialMedialApp.model.Person;
import by.ladz.gusakov.SocialMedialApp.repository.PeopleRepository;
import by.ladz.gusakov.SocialMedialApp.security.PersonDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PersonDetailsService implements UserDetailsService {

    private final PeopleRepository peopleRepository;

    public PersonDetailsService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Person> foundedByUsername = peopleRepository.findByUsername(username);
        if(foundedByUsername.isEmpty()){
            Optional<Person> foundedByEmail = peopleRepository.findByEmail(username);
            if(foundedByEmail.isEmpty()) {
                throw new UsernameNotFoundException("Пользователь с данным именем или электронной почтой не найден!");
            }
            return new PersonDetails(foundedByEmail.get());
        }
        return new PersonDetails(foundedByUsername.get());
    }
}
