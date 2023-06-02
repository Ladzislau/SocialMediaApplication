package by.ladz.gusakov.SocialMedialApp.controllers;

import by.ladz.gusakov.SocialMedialApp.dto.AuthenticationDTO;
import by.ladz.gusakov.SocialMedialApp.dto.PersonDTO;
import by.ladz.gusakov.SocialMedialApp.models.Person;
import by.ladz.gusakov.SocialMedialApp.security.JWTUtil;
import by.ladz.gusakov.SocialMedialApp.services.PeopleService;
import by.ladz.gusakov.SocialMedialApp.services.RegistrationService;
import by.ladz.gusakov.SocialMedialApp.util.PersonErrorResponse;
import by.ladz.gusakov.SocialMedialApp.util.PersonNotCreatedException;
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
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
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

    @PostMapping("/login")
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

    @PostMapping("/registration")
    public ResponseEntity<Map<String, String>> performRegistration(@RequestBody @Valid PersonDTO personDTO,
                                                                   BindingResult bindingResult) {
        Person person = convertToPerson(personDTO);
        personValidator.validate(person, bindingResult);
        if(bindingResult.hasErrors()){
            StringBuilder errorMessage = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error :
                    errors) {
                errorMessage.append(error.getField())
                        .append(" – ").append(error.getDefaultMessage())
                        .append("; ");
            }
            throw new PersonNotCreatedException(errorMessage.toString());
        }
        registrationService.register(person);

        String token = jwtUtil.generateToken(person.getUsername());
        return ResponseEntity.ok(Map.of("jwt-token", token));
    }

    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotCreatedException e){
        PersonErrorResponse response = new PersonErrorResponse(
                e.getMessage(),
                System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private Person convertToPerson(PersonDTO personDTO){
        return modelMapper.map(personDTO, Person.class);
    }

}
