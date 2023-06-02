package by.ladz.gusakov.SocialMedialApp.repositories;

import by.ladz.gusakov.SocialMedialApp.models.Image;
import by.ladz.gusakov.SocialMedialApp.models.Publication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {
    void deleteByPublication (Publication publication);
}
