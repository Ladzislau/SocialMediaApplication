package by.ladz.gusakov.SocialMedialApp.util;

import by.ladz.gusakov.SocialMedialApp.models.Person;
import by.ladz.gusakov.SocialMedialApp.services.PeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PersonValidator implements Validator {

    private final PeopleService peopleService;

    @Autowired
    public PersonValidator(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Person person = (Person) target;
        if(peopleService.findByUsername(person.getUsername()).isPresent()){
            errors.rejectValue("username", "","Данное имя пользователя уже используется");
        }
        if(peopleService.findByEmail(person.getEmail()).isPresent()){
            errors.rejectValue("email", "", "Данный адрес электронной почты уже занят");
        }
    }
}