package by.ladz.gusakov.SocialMedialApp.services;

import by.ladz.gusakov.SocialMedialApp.exceptions.ChatNotFoundedException;
import by.ladz.gusakov.SocialMedialApp.models.Chat;
import by.ladz.gusakov.SocialMedialApp.models.Friendship;
import by.ladz.gusakov.SocialMedialApp.models.Person;
import by.ladz.gusakov.SocialMedialApp.models.UserMessage;
import by.ladz.gusakov.SocialMedialApp.repositories.UserMessageRepository;
import by.ladz.gusakov.SocialMedialApp.exceptions.NotFriendException;
import by.ladz.gusakov.SocialMedialApp.exceptions.PersonNotAuthenticatedException;
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
            throw new ChatNotFoundedException("Чат с пользователем " + person.getUsername() + " не найден!" +
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
    public void sendMessage(UserMessage message) throws PersonNotAuthenticatedException, NotFriendException {
        Person currentUser = peopleService.getCurrentUser();

        if(!areFriends(currentUser, message.getRecipient()))
            throw new NotFriendException("Вы не можете отправить сообщение пользователю "
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
