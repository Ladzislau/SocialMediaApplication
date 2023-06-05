package by.ladz.gusakov.SocialMedialApp.controllers;

import by.ladz.gusakov.SocialMedialApp.dto.RelationshipDTO;
import by.ladz.gusakov.SocialMedialApp.exceptions.*;
import by.ladz.gusakov.SocialMedialApp.models.Person;
import by.ladz.gusakov.SocialMedialApp.services.FriendRequestService;
import by.ladz.gusakov.SocialMedialApp.services.PeopleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/following")
public class RelationshipController {

    private final FriendRequestService friendRequestService;
    private final PeopleService peopleService;

    @Autowired
    public RelationshipController(FriendRequestService friendRequestService, PeopleService peopleService) {
        this.friendRequestService = friendRequestService;
        this.peopleService = peopleService;
    }

    @GetMapping("/followers")
    public ResponseEntity<List<Person>> getFollowers(@RequestParam("username") String username) throws PersonNotFoundException, FollowingException {
        Optional<Person> person = peopleService.findByUsername(username);
        if (person.isEmpty()) {
            throw new PersonNotFoundException("Пользователя с данным именем не существует!");
        }
        List<Person> followers = friendRequestService.getPersonFollowers(person.get());
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/friends")
    public ResponseEntity<List<Person>> getFriends(@RequestParam("username") String username) throws PersonNotFoundException, FollowingException {
        Optional<Person> person = peopleService.findByUsername(username);
        if (person.isEmpty()) {
            throw new PersonNotFoundException("Пользователя с данным именем не существует!");
        }
        List<Person> friends = friendRequestService.getPersonFriends(person.get());
        return ResponseEntity.ok(friends);
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
