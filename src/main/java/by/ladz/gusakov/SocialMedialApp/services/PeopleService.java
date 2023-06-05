package by.ladz.gusakov.SocialMedialApp.services;

import by.ladz.gusakov.SocialMedialApp.models.Person;
import by.ladz.gusakov.SocialMedialApp.repositories.PeopleRepository;
import by.ladz.gusakov.SocialMedialApp.security.PersonDetails;
import by.ladz.gusakov.SocialMedialApp.exceptions.PersonNotAuthenticatedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PeopleService {

    private final PeopleRepository peopleRepository;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    public Optional<Person> findByUsername(String username){
        return peopleRepository.findByUsername(username);
    }

    public Optional<Person> findByEmail(String email){
        return peopleRepository.findByEmail(email);
    }

    public Optional<Person> findByUsernameOrEmail(String usernameOrEmail){
        Optional<Person> foundedByUsername = peopleRepository.findByUsername(usernameOrEmail);
        if(foundedByUsername.isEmpty()){
            return peopleRepository.findByEmail(usernameOrEmail);
        }
        return foundedByUsername;
    }

    public Person getCurrentUser() throws PersonNotAuthenticatedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        Optional<Person> creatorOptional = findByUsername(personDetails.getUsername());
        if(creatorOptional.isEmpty()){
            throw new PersonNotAuthenticatedException("Ошибка! Пользователь не аутентифицирован");
        }
        return creatorOptional.get();
    }


}
