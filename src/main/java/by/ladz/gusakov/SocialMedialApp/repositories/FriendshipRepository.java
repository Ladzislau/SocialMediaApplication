package by.ladz.gusakov.SocialMedialApp.repositories;

import by.ladz.gusakov.SocialMedialApp.models.Friendship;
import by.ladz.gusakov.SocialMedialApp.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Integer> {

    Optional<List<Friendship>> findByFriend(Person friend);
    Optional<Friendship> findByPersonAndFriend(Person person1, Person person2);
}
