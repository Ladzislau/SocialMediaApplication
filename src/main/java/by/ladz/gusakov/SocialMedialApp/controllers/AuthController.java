package by.ladz.gusakov.SocialMedialApp.controllers;

import by.ladz.gusakov.SocialMedialApp.dto.AuthenticationDTO;
import by.ladz.gusakov.SocialMedialApp.dto.PersonDTO;
import by.ladz.gusakov.SocialMedialApp.exceptions.ExceptionUtils;
import by.ladz.gusakov.SocialMedialApp.models.Person;
import by.ladz.gusakov.SocialMedialApp.security.JWTUtil;
import by.ladz.gusakov.SocialMedialApp.services.PeopleService;
import by.ladz.gusakov.SocialMedialApp.services.RegistrationService;
import by.ladz.gusakov.SocialMedialApp.exceptions.PersonNotCreatedException;
import by.ladz.gusakov.SocialMedialApp.util.PersonValidator;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationProvider authenticationProvider;

    private final ModelMapper modelMapper;

    private final PeopleService peopleService;

    private final JWTUtil jwtUtil;

    private final PersonValidator personValidator;

    private final RegistrationService registrationService;

    @Autowired
    public AuthController(AuthenticationProvider authenticationProvider, ModelMapper modelMapper, PeopleService peopleService, JWTUtil jwtUtil, PersonValidator personValidator, RegistrationService registrationService) {
        this.authenticationProvider = authenticationProvider;
        this.modelMapper = modelMapper;
        this.peopleService = peopleService;
        this.jwtUtil = jwtUtil;
        this.personValidator = personValidator;
        this.registrationService = registrationService;
    }

    @PostMapping("/registration")
    public ResponseEntity<Map<String, String>> performRegistration(@RequestBody @Valid PersonDTO personDTO,
                                                                   BindingResult bindingResult) throws PersonNotCreatedException {
        Person person = convertToPerson(personDTO);
        personValidator.validate(person, bindingResult);
        String errorMessage = ExceptionUtils.generateErrorMessage(bindingResult);
        if(errorMessage != null){
            throw new PersonNotCreatedException(errorMessage);
        }
        registrationService.register(person);

        String token = jwtUtil.generateToken(person.getUsername());
        return ResponseEntity.ok(Map.of("jwt-token", token));
    }

    @PatchMapping("/login")
    public ResponseEntity<Map<String, String>> performLogin(@RequestBody AuthenticationDTO authenticationDTO){
        UsernamePasswordAuthenticationToken authInputToken = new UsernamePasswordAuthenticationToken(
                authenticationDTO.getUsername(), authenticationDTO.getPassword());
        try {
            authenticationProvider.authenticate(authInputToken);
        } catch (BadCredentialsException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message","Неправильное имя или пароль"));
        }
        Person person = peopleService.findByUsernameOrEmail(authenticationDTO.getUsername()).get();
        String token = jwtUtil.generateToken(person.getUsername());
        return ResponseEntity.ok(Map.of("jwt-token", token));
    }

    private Person convertToPerson(PersonDTO personDTO){
        return modelMapper.map(personDTO, Person.class);
    }

}
