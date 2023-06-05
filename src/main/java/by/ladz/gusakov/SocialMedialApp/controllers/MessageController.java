package by.ladz.gusakov.SocialMedialApp.controllers;

import by.ladz.gusakov.SocialMedialApp.dto.UserMessageDTO;
import by.ladz.gusakov.SocialMedialApp.exceptions.*;
import by.ladz.gusakov.SocialMedialApp.models.Chat;
import by.ladz.gusakov.SocialMedialApp.models.Person;
import by.ladz.gusakov.SocialMedialApp.models.UserMessage;
import by.ladz.gusakov.SocialMedialApp.services.UserMessageService;
import by.ladz.gusakov.SocialMedialApp.services.PeopleService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final UserMessageService userMessageService;

    private final PeopleService peopleService;

    private final ModelMapper modelMapper;


    @Autowired
    public MessageController(UserMessageService userMessageService, PeopleService peopleService, ModelMapper modelMapper) {
        this.userMessageService = userMessageService;
        this.peopleService = peopleService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/chat")
    public ResponseEntity<Chat> getChat(@RequestParam("withUser") String username) throws PersonNotFoundException, PersonNotAuthenticatedException, ChatNotFoundedException {
        Optional<Person> person = peopleService.findByUsername(username);

        if(person.isEmpty())
            throw new PersonNotFoundException("Невозможно открыть чат! Пользователя " + username + " не существует");

        Chat chat = userMessageService.getChatWithPerson(person.get());

        return ResponseEntity.ok(chat);
    }

    @PostMapping()
    public ResponseEntity<Map<String, String>> sendMessage(@RequestBody @Valid UserMessageDTO userMessageDTO, BindingResult bindingResult)
            throws PersonNotAuthenticatedException, NotFriendException, PersonNotFoundException, MessageException {
        String errorMessage = ExceptionUtils.generateErrorMessage(bindingResult);
        if(errorMessage != null){
            throw new MessageException(errorMessage);
        }
        UserMessage message = convertToUserMessage(userMessageDTO);


        userMessageService.sendMessage(message);

        return ResponseEntity.ok(Map.of("message", "Сообщение успешно отправлено"));
    }

    private UserMessage convertToUserMessage(UserMessageDTO messageDTO) throws PersonNotFoundException {
        UserMessage message = modelMapper.map(messageDTO, UserMessage.class);
        Optional<Person> recipient = peopleService.findByUsername(messageDTO.getRecipientName());
        if(recipient.isEmpty()){
            throw new PersonNotFoundException("Невозможно отправить сообщение! Пользователя "
                    + messageDTO.getRecipientName() + " не существует");
        }
        message.setRecipient(recipient.get());
        return message;
    }

}
