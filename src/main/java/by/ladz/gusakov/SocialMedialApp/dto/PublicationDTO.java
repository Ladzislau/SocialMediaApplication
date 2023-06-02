package by.ladz.gusakov.SocialMedialApp.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

public class PublicationDTO {

    @NotEmpty(message = "Заголовок публикации не может быть пустым")
    @Size(max = 50, message = "Максимальная длина заголовка публикации – 50 символов")
    private String title;

    @Size(max = 1000, message = "Максимальная длина текста в публикации – 1000 символов")
    private String content;

    private Date createdAt;

    private List<String> publicationImages;

    public PublicationDTO() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setPublicationImages(List<String> publicationImages) {
        this.publicationImages = publicationImages;
    }

    public List<String> getPublicationImages() {
        return publicationImages;
    }
}