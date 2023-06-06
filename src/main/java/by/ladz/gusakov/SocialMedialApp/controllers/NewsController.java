package by.ladz.gusakov.SocialMedialApp.controllers;

import by.ladz.gusakov.SocialMedialApp.dto.PublicationDTO;
import by.ladz.gusakov.SocialMedialApp.exceptions.IncorrectPageParametersException;
import by.ladz.gusakov.SocialMedialApp.models.Publication;
import by.ladz.gusakov.SocialMedialApp.services.NewsFeedService;
import by.ladz.gusakov.SocialMedialApp.util.PublicationMapper;
import by.ladz.gusakov.SocialMedialApp.exceptions.CantLoadImageException;
import by.ladz.gusakov.SocialMedialApp.exceptions.PersonNotAuthenticatedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/news")
public class NewsController {

    private final NewsFeedService newsFeedService;

    private final PublicationMapper publicationMapper;

    @Autowired
    public NewsController(NewsFeedService newsFeedService, PublicationMapper publicationMapper) {
        this.newsFeedService = newsFeedService;
        this.publicationMapper = publicationMapper;
    }

    @GetMapping()
    public ResponseEntity<List<PublicationDTO>> index(
            @RequestParam(value = "page", required = false) Integer page,

            @RequestParam(value = "publicationsPerPage", required = false) Integer publicationsPerPage,

            @RequestParam(value = "sortByCreationTime", required = false) Boolean sortByCreationTime)
            throws PersonNotAuthenticatedException, CantLoadImageException, IncorrectPageParametersException {

        List<Publication> news = newsFeedService.generateNewsFeedForCurrentUser(page, publicationsPerPage, sortByCreationTime);
        List<PublicationDTO> newsDTO = publicationMapper.mapToPublicationDTOList(news);

        return ResponseEntity.ok(newsDTO);
    }
}
