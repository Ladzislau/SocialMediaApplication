package by.ladz.gusakov.SocialMedialApp.util;

import by.ladz.gusakov.SocialMedialApp.dto.PublicationDTO;
import by.ladz.gusakov.SocialMedialApp.model.Publication;
import by.ladz.gusakov.SocialMedialApp.exception.CantLoadImageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class PublicationMapper {

    private final ImageUtils imageUtils;

    @Autowired
    public PublicationMapper(ImageUtils imageUtils) {
        this.imageUtils = imageUtils;
    }

    public List<PublicationDTO> mapToPublicationDTOList(List<Publication> publications) throws CantLoadImageException {
        List<PublicationDTO> publicationDTOList = new ArrayList<>();
        for (Publication publication : publications) {
            PublicationDTO publicationDTO = mapToPublicationDTO(publication);
            publicationDTOList.add(publicationDTO);
        }
        return publicationDTOList;
    }

    public PublicationDTO mapToPublicationDTO(Publication publication) throws CantLoadImageException {
        try {
            PublicationDTO publicationDTO = new PublicationDTO();
            List<String> base64images = imageUtils.loadImages(publication.getImages());
            publicationDTO.setTitle(publication.getTitle());
            publicationDTO.setContent(publication.getContent());
            publicationDTO.setCreatedAt(publication.getCreatedAt());
            publicationDTO.setPublicationImages(base64images);
            publicationDTO.setAuthor(publication.getCreator().getUsername());
            return publicationDTO;
        } catch (IOException e) {
            throw new CantLoadImageException("Произошла ошибка при загрузке изображения(-ий)");
        }
    }

    public Publication mapToPublication(PublicationDTO publicationDTO) {
        Publication publication = new Publication();
        publication.setTitle(publicationDTO.getTitle());
        publication.setContent(publicationDTO.getContent());
        return publication;
    }
}
