package by.ladz.gusakov.SocialMedialApp.services;

import by.ladz.gusakov.SocialMedialApp.models.Publication;
import by.ladz.gusakov.SocialMedialApp.repositories.ImageRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImageService {

    private final ImageRepository imageRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }


    @Transactional
    public void deleteByPublication(Publication publication){
        imageRepository.deleteByPublication(publication);

    }
}
