package by.ladz.gusakov.SocialMedialApp.repository;

import by.ladz.gusakov.SocialMedialApp.model.Image;
import by.ladz.gusakov.SocialMedialApp.model.Publication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {
    void deleteByPublication (Publication publication);
}
