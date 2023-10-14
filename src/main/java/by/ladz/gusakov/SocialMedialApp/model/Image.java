package by.ladz.gusakov.SocialMedialApp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "image")
@NoArgsConstructor
@Getter
@Setter
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "path_to_image")
    @Size(max = 200, message = "Название изображения слишком длинное")
    private String pathToImage;

    @ManyToOne
    @JoinColumn(name = "publication_id", referencedColumnName = "id")
    private Publication publication;

    public Image(String pathToImage, Publication publication) {
        this.pathToImage = pathToImage;
        this.publication = publication;
    }
}
