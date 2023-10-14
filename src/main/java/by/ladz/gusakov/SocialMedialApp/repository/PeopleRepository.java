package by.ladz.gusakov.SocialMedialApp.repository;


import by.ladz.gusakov.SocialMedialApp.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PeopleRepository extends JpaRepository<Person, Integer> {
    Optional<Person> findByUsername(String username);

    Optional<Person> findByEmail(String email);
}
