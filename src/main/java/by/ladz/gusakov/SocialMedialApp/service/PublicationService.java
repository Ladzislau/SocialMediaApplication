package by.ladz.gusakov.SocialMedialApp.service;

import by.ladz.gusakov.SocialMedialApp.model.Image;
import by.ladz.gusakov.SocialMedialApp.model.Person;
import by.ladz.gusakov.SocialMedialApp.model.Publication;
import by.ladz.gusakov.SocialMedialApp.repository.PublicationRepository;
import by.ladz.gusakov.SocialMedialApp.util.ImageUtil;
import by.ladz.gusakov.SocialMedialApp.exception.PersonNotAuthenticatedException;
import by.ladz.gusakov.SocialMedialApp.exception.PublicationNotFoundedException;
import by.ladz.gusakov.SocialMedialApp.exception.UnauthorizedPublicationModificationException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class PublicationService {

    private final PublicationRepository publicationRepository;

    private final PeopleService peopleService;

    private final ImageService imageService;

    private final ImageUtil imageUtil;

    @Autowired
    public PublicationService(PublicationRepository publicationRepository, PeopleService peopleService, ImageService imageService, ImageUtil imageUtil) {
        this.publicationRepository = publicationRepository;
        this.peopleService = peopleService;
        this.imageService = imageService;
        this.imageUtil = imageUtil;
    }

    public Optional<Publication> getById(int id) {
        return publicationRepository.findById(id);
    }

    public List<Publication> getAllPublicationsByCreator(Person creator) {
        return publicationRepository.findPublicationsByCreator(creator);
    }

    @Transactional
    public void save(Publication publication, List<MultipartFile> imagesToSave) throws PersonNotAuthenticatedException {
        Person creator = peopleService.getCurrentUser();

        if (imagesToSave != null) {
            List<Image> images = imageUtil.saveImages(imagesToSave, publication);
            publication.setImages(images);
        }
        publication.setCreator(creator);
        publication.setCreatedAt(new Date());

        publicationRepository.save(publication);
    }

    @Transactional
    public void update(int id, Publication updatedPublication) throws PersonNotAuthenticatedException,
            PublicationNotFoundedException, UnauthorizedPublicationModificationException {

        Optional<Publication> oldPublicationOpt = publicationRepository.findById(id);
        checkOptionalAndCreator(oldPublicationOpt);
        Publication oldPublication = oldPublicationOpt.get();

        updatedPublication.setImages(oldPublication.getImages());
        updatedPublication.setCreatedAt(oldPublication.getCreatedAt());
        updatedPublication.setCreator(oldPublication.getCreator());
        updatedPublication.setId(id);


        publicationRepository.save(updatedPublication);
    }

    @Transactional
    public void deleteById(int id) throws PersonNotAuthenticatedException, PublicationNotFoundedException,
            UnauthorizedPublicationModificationException {

        Optional<Publication> publicationOpt = publicationRepository.findById(id);
        checkOptionalAndCreator(publicationOpt);

        Publication publication = publicationOpt.get();
        imageUtil.deleteImagesFromServer(publication.getImages());
        imageService.deleteByPublication(publication);

        publicationRepository.deleteById(id);
    }

    private void checkOptionalAndCreator(Optional<Publication> publicationOpt) throws PersonNotAuthenticatedException,
            PublicationNotFoundedException, UnauthorizedPublicationModificationException {

        Person currentUser = peopleService.getCurrentUser();

        if (publicationOpt.isEmpty()) {
            throw new PublicationNotFoundedException("Публикация не найдена!");
        }
        Publication publication = publicationOpt.get();
        if (!publication.getCreator().equals(currentUser)) {
            throw new UnauthorizedPublicationModificationException("Невозможно произвести операцию с публикацией! " +
                    "Вы не являетесь её создателем");
        }
    }

}
