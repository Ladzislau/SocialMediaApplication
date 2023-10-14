package by.ladz.gusakov.SocialMedialApp.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class PublicationDTO {

    @NotEmpty(message = "Заголовок публикации не может быть пустым")
    @Size(max = 50, message = "Максимальная длина заголовка публикации – 50 символов")
    private String title;

    @Size(max = 1024, message = "Максимальная длина текста в публикации – 1024 символа")
    private String content;

    private String author;

    private Date createdAt;

    private List<String> publicationImages;
}
