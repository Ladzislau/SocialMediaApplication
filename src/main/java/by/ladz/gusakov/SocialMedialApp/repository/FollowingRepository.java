package by.ladz.gusakov.SocialMedialApp.repository;

import by.ladz.gusakov.SocialMedialApp.model.Person;
import by.ladz.gusakov.SocialMedialApp.model.Following;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowingRepository extends JpaRepository<Following, Integer> {

    Optional<List<Following>> findByFollowee(Person followee);
    Optional<Following> findByPersonAndFollowee(Person person, Person followee);
}
