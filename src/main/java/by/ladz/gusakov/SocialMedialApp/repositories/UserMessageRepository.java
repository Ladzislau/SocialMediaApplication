package by.ladz.gusakov.SocialMedialApp.repositories;

import by.ladz.gusakov.SocialMedialApp.models.Person;
import by.ladz.gusakov.SocialMedialApp.models.UserMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMessageRepository extends JpaRepository<UserMessage, Integer> {

    List<UserMessage> findBySenderAndRecipient(Person sender, Person recipient);
}
