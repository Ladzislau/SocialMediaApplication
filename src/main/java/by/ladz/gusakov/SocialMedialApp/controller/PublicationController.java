package by.ladz.gusakov.SocialMedialApp.controller;

import by.ladz.gusakov.SocialMedialApp.dto.PublicationDTO;
import by.ladz.gusakov.SocialMedialApp.exceptions.*;
import by.ladz.gusakov.SocialMedialApp.models.Person;
import by.ladz.gusakov.SocialMedialApp.models.Publication;
import by.ladz.gusakov.SocialMedialApp.services.PeopleService;
import by.ladz.gusakov.SocialMedialApp.services.PublicationService;
import by.ladz.gusakov.SocialMedialApp.util.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/publications")
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
            throw new PublicationNotFoundedException("Публикация не найдена!");
        }

        PublicationDTO publication = publicationMapper.mapToPublicationDTO(foundedPublication.get());
        return ResponseEntity.ok(publication);
    }

    @GetMapping()
    public ResponseEntity<List<PublicationDTO>> getUserPublications(
            @RequestParam("username") String username)
            throws PersonNotFoundException, CantLoadImageException {

        Optional<Person> creatorOptional = peopleService.findByUsername(username);

        if (creatorOptional.isEmpty()) {
            throw new PersonNotFoundException("Невозможно загрузить публикации! Пользователя "
                    + username + " не существует");
        }

        Person creator = creatorOptional.get();
        List<Publication> publications = publicationService.getAllPublicationsByCreator(creator);
        List<PublicationDTO> publicationDTOS = publicationMapper.mapToPublicationDTOList(publications);

        return ResponseEntity.ok(publicationDTOS);
    }

    @PostMapping()
    public ResponseEntity<Map<String, String>> createPublication(@ModelAttribute @Valid PublicationDTO publicationDTO, BindingResult bindingResult,
                                                                 @RequestParam(value = "imagesForPost", required = false) List<MultipartFile> imagesForPost)
            throws PersonNotAuthenticatedException, IncorrectPublicationException {

        String errorMessage = ExceptionUtils.generateErrorMessage(bindingResult);
        if(errorMessage != null)
            throw new IncorrectPublicationException(errorMessage);

        Publication publication = publicationMapper.mapToPublication(publicationDTO);
        publicationService.save(publication, imagesForPost);

        return ResponseEntity.ok(Map.of("message", "Публикация успешно создана!"));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, String>> updatePublication(@PathVariable("id") int id, @RequestBody @Valid PublicationDTO publicationDTO, BindingResult bindingResult)
            throws UnauthorizedPublicationModificationException, PublicationNotFoundedException, PersonNotAuthenticatedException, IncorrectPublicationException {

        String errorMessage = ExceptionUtils.generateErrorMessage(bindingResult);
        if(errorMessage != null)
            throw new IncorrectPublicationException(errorMessage);

        Publication updatedPublication = publicationMapper.mapToPublication(publicationDTO);
        publicationService.update(id, updatedPublication);

        return ResponseEntity.ok(Map.of("message", "Публикация успешно обновлена!"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletePublication(@PathVariable("id") int id) throws UnauthorizedPublicationModificationException,
            PublicationNotFoundedException, PersonNotAuthenticatedException {

        publicationService.deleteById(id);

        return ResponseEntity.ok(Map.of("message", "Публикация успешно удалена!"));
    }

}
