package by.ladz.gusakov.SocialMedialApp.repository;

import by.ladz.gusakov.SocialMedialApp.model.Person;
import by.ladz.gusakov.SocialMedialApp.model.Publication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublicationRepository extends JpaRepository<Publication, Integer> {
    List<Publication> findPublicationsByCreator(Person creator);
}
