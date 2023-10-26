package by.ladz.gusakov.SocialMedialApp.service;

import by.ladz.gusakov.SocialMedialApp.exception.ChatNotFoundException;
import by.ladz.gusakov.SocialMedialApp.model.Chat;
import by.ladz.gusakov.SocialMedialApp.model.Friendship;
import by.ladz.gusakov.SocialMedialApp.model.Person;
import by.ladz.gusakov.SocialMedialApp.model.UserMessage;
import by.ladz.gusakov.SocialMedialApp.repository.UserMessageRepository;
import by.ladz.gusakov.SocialMedialApp.exception.FriendshipRequiredException;
import by.ladz.gusakov.SocialMedialApp.exception.PersonNotAuthenticatedException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserMessageService {

    private final UserMessageRepository userMessageRepository;

    private final FriendRequestService friendRequestService;

    private final PeopleService peopleService;

    public UserMessageService(UserMessageRepository userMessageRepository, FriendRequestService friendRequestService, PeopleService peopleService) {
        this.userMessageRepository = userMessageRepository;
        this.friendRequestService = friendRequestService;
        this.peopleService = peopleService;
    }

    public Chat getChatWithPerson(Person person) throws PersonNotAuthenticatedException, ChatNotFoundedException {
        Person currentUser = peopleService.getCurrentUser();
        Chat chat = new Chat(currentUser.getUsername(), person.getUsername());

        List<UserMessage> messagesFromCurrentUser = userMessageRepository.findBySenderAndRecipient(currentUser, person);
        List<UserMessage> messages = userMessageRepository.findBySenderAndRecipient(person, currentUser);
        if(messages.isEmpty() && messagesFromCurrentUser.isEmpty()){
            throw new ChatNotFoundException("Чат с пользователем " + person.getUsername() + " не найден!" +
                    " Вы не отправили ему ни одного сообщения");
        }
        messages.addAll(messagesFromCurrentUser);

        Map<Date, String> messagesForChat = new TreeMap<>();
        for (UserMessage message :
                messages) {
            messagesForChat.put( message.getSentTime(), message.getContent());
        }
        chat.setMessages(messagesForChat);

        return chat;
    }

    @Transactional
    public void sendMessage(UserMessage message) throws PersonNotAuthenticatedException, FriendshipRequiredException {
        Person currentUser = peopleService.getCurrentUser();

        if(areFriends(currentUser, message.getRecipient()))
            throw new FriendshipRequiredException("Вы не можете отправить сообщение пользователю "
            + message.getRecipient().getUsername() + ", потому что не являетесь друзьями");

        message.setSender(currentUser);
        message.setSentTime(new Date());

        userMessageRepository.save(message);
    }

    public boolean areFriends(Person person1, Person person2) {
        List<Friendship> friendships = friendRequestService.findFriendship(person1, person2);
        return !friendships.isEmpty();
    }

}
