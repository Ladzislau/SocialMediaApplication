package by.ladz.gusakov.SocialMedialApp.controllers;

import by.ladz.gusakov.SocialMedialApp.dto.PublicationDTO;
import by.ladz.gusakov.SocialMedialApp.models.Person;
import by.ladz.gusakov.SocialMedialApp.models.Publication;
import by.ladz.gusakov.SocialMedialApp.services.PeopleService;
import by.ladz.gusakov.SocialMedialApp.services.PublicationService;
import by.ladz.gusakov.SocialMedialApp.util.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/publications")
public class PublicationController {

    private final PublicationService publicationService;

    private final PeopleService peopleService;

    private final PublicationMapper publicationMapper;

    @Autowired
    public PublicationController(PublicationService publicationService, PeopleService peopleService, PublicationMapper publicationMapper) {
        this.publicationService = publicationService;
        this.peopleService = peopleService;
        this.publicationMapper = publicationMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublicationDTO> getPublication(@PathVariable("id") int id) throws PublicationNotFoundedException, CantLoadImageException {
        Optional<Publication> foundedPublication = publicationService.getById(id);
        if (foundedPublication.isEmpty()) {
            throw new PublicationNotFoundedException();
        }
        PublicationDTO publication = convertToPublicationDTO(foundedPublication.get());
        return ResponseEntity.ok(publication);
    }

    @GetMapping()
    public ResponseEntity<List<PublicationDTO>> getUserPublications(@RequestParam("creator") String username) throws PersonNotFoundException, CantLoadImageException {
        Optional<Person> creatorOptional = peopleService.findByUsername(username);

        if (creatorOptional.isEmpty()) {
            throw new PersonNotFoundException("Невозможно загрузить публикации! Пользователя "
                    + username + " не существует");
        }

        Person creator = creatorOptional.get();
        List<Publication> publications = publicationService.getAllPublicationsByCreator(creator);
        List<PublicationDTO> publicationDTOS = new ArrayList<>();
        for (Publication publication : publications) {
            PublicationDTO publicationDTO = convertToPublicationDTO(publication);
            publicationDTOS.add(publicationDTO);
        }
        return ResponseEntity.ok(publicationDTOS);
    }

    @PostMapping()
    public ResponseEntity<Map<String, String>> createPublication(@ModelAttribute PublicationDTO publicationDTO,
                                                        @RequestParam(value = "imagesForPost", required = false) List<MultipartFile> imagesForPost)
            throws PersonNotAuthenticatedException {

        Publication publication = convertToPublication(publicationDTO);
        publicationService.save(publication, imagesForPost);
        return ResponseEntity.ok(Map.of("message", "Публикация успешно создана!"));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, String>> updatePublication(@PathVariable("id") int id, @ModelAttribute PublicationDTO publicationDTO)
            throws UnauthorizedPublicationModificationException, PublicationNotFoundedException, PersonNotAuthenticatedException {

        Publication updatedPublication = convertToPublication(publicationDTO);
        publicationService.update(id, updatedPublication);
        return ResponseEntity.ok(Map.of("message", "Публикация успешно обновлена!"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletePublication(@PathVariable("id") int id) throws UnauthorizedPublicationModificationException,
            PublicationNotFoundedException, PersonNotAuthenticatedException {

        publicationService.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Публикация успешно удалена!"));
    }

    @ExceptionHandler
    private ResponseEntity<PublicationErrorResponse> handleException(UnauthorizedPublicationModificationException e) {
        PublicationErrorResponse response = new PublicationErrorResponse(
                "Несанкционированное изменение публикации: текущий пользователь не является её создателем",
                System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    private ResponseEntity<PublicationErrorResponse> handleException(PublicationNotFoundedException e) {
        PublicationErrorResponse errorResponse = new PublicationErrorResponse("Данная публикация не найдена",
                System.currentTimeMillis());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<PublicationErrorResponse> handleException(CantLoadImageException e) {
        PublicationErrorResponse errorResponse = new PublicationErrorResponse(
                "Изображения из публикации не могут быть загружены",
                System.currentTimeMillis());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotFoundException e) {
        PersonErrorResponse response = new PersonErrorResponse("Данный пользователь не найден",
                System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotAuthenticatedException e) {
        PersonErrorResponse response = new PersonErrorResponse(
                "Для создания публикации вы должны быть авторизованы",
                System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }


    private PublicationDTO convertToPublicationDTO(Publication publication) throws CantLoadImageException {
        try {
            PublicationDTO publicationDTO = new PublicationDTO();
            List<String> base64images = imageUtils.loadImages(publication.getImages());
            publicationDTO.setTitle(publication.getTitle());
            publicationDTO.setContent(publication.getContent());
            publicationDTO.setCreatedAt(publication.getCreatedAt());
            publicationDTO.setPublicationImages(base64images);
            return publicationDTO;
        } catch (IOException e) {
            throw new CantLoadImageException();
        }
    }

    private Publication convertToPublication(PublicationDTO publicationDTO) {
        Publication publication = new Publication();
        publication.setTitle(publicationDTO.getTitle());
        publication.setContent(publicationDTO.getContent());
        return publication;
    }

}
