package by.ladz.gusakov.SocialMedialApp.repository;

import by.ladz.gusakov.SocialMedialApp.model.Person;
import by.ladz.gusakov.SocialMedialApp.model.UserMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMessageRepository extends JpaRepository<UserMessage, Integer> {

    List<UserMessage> findBySenderAndRecipient(Person sender, Person recipient);
}
