package by.ladz.gusakov.SocialMedialApp.controller;

import by.ladz.gusakov.SocialMedialApp.dto.RelationshipDTO;
import by.ladz.gusakov.SocialMedialApp.exception.*;
import by.ladz.gusakov.SocialMedialApp.model.Person;
import by.ladz.gusakov.SocialMedialApp.service.FriendRequestService;
import by.ladz.gusakov.SocialMedialApp.service.PeopleService;
import by.ladz.gusakov.SocialMedialApp.util.ExceptionUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/following")
public class RelationshipController {

    private final FriendRequestService friendRequestService;
    private final PeopleService peopleService;

    @Autowired
    public RelationshipController(FriendRequestService friendRequestService, PeopleService peopleService) {
        this.friendRequestService = friendRequestService;
        this.peopleService = peopleService;
    }

    @PostMapping("/follow")
    public ResponseEntity<Map<String, String>> follow(@RequestBody @Valid RelationshipDTO relationshipDTO, BindingResult bindingResult)
            throws PersonNotFoundException, PersonNotAuthenticatedException, FollowingException {

        String errorMessage = ExceptionUtils.generateErrorMessage(bindingResult);
        if(errorMessage != null)
            throw new PersonNotFoundException(errorMessage);

        Optional<Person> followee = peopleService.findByUsername(relationshipDTO.getUsername());
        if (followee.isEmpty()) {
            throw new PersonNotFoundException("Невозможно подписаться на несуществующего пользователя!");
        }
        friendRequestService.follow(followee.get());

        return ResponseEntity.ok(Map.of("message",
                "Вы успешно подписались на пользователя " + relationshipDTO.getUsername()));
    }

    @DeleteMapping("/unfollow")
    public ResponseEntity<Map<String, String>> unfollow(@RequestBody @Valid RelationshipDTO relationshipDTO, BindingResult bindingResult)
            throws PersonNotFoundException, FollowingException, PersonNotAuthenticatedException {

        String errorMessage = ExceptionUtils.generateErrorMessage(bindingResult);
        if(errorMessage != null)
            throw new PersonNotFoundException(errorMessage);

        Optional<Person> followee = peopleService.findByUsername(relationshipDTO.getUsername());
        if (followee.isEmpty()) {
            throw new PersonNotFoundException("Невозможно отписаться от несуществующего пользователя!");
        }
        friendRequestService.unfollow(followee.get());

        return ResponseEntity.ok(Map.of("message",
                "Вы успешно отписались от пользователя " + relationshipDTO.getUsername()));
    }

    @PatchMapping("/accept_request")
    public ResponseEntity<Map<String, String>> acceptRequest(@RequestBody @Valid RelationshipDTO relationshipDTO, BindingResult bindingResult)
            throws PersonNotFoundException, FollowingException, PersonNotAuthenticatedException {

        String errorMessage = ExceptionUtils.generateErrorMessage(bindingResult);
        if(errorMessage != null)
            throw new PersonNotFoundException(errorMessage);

        Optional<Person> followee = peopleService.findByUsername(relationshipDTO.getUsername());
        if (followee.isEmpty()) {
            throw new PersonNotFoundException("Невозможно принять запрос от несуществующего пользователя!");
        }
        friendRequestService.acceptRequest(followee.get());

        return ResponseEntity.ok(Map.of("message", "Вы успешно добавили пользователя "
                + relationshipDTO.getUsername() + " в друзья"));
    }

    @PatchMapping("/decline_request")
    public ResponseEntity<Map<String, String>> declineRequest(@RequestBody @Valid RelationshipDTO relationshipDTO, BindingResult bindingResult)
            throws FollowingException, PersonNotAuthenticatedException, PersonNotFoundException {

        String errorMessage = ExceptionUtils.generateErrorMessage(bindingResult);
        if(errorMessage != null)
            throw new PersonNotFoundException(errorMessage);

        Optional<Person> followee = peopleService.findByUsername(relationshipDTO.getUsername());
        if (followee.isEmpty()) {
            throw new PersonNotFoundException("Невозможно отклонить запрос от несуществующего пользователя!");
        }
        friendRequestService.declineRequest(followee.get());

        return ResponseEntity.ok(Map.of("message",
                "Вы отклонили запрос пользователя " + relationshipDTO.getUsername()));
    }
}

