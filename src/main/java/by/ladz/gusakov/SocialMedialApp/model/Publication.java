package by.ladz.gusakov.SocialMedialApp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import java.util.Date;
import java.util.List;


@Entity
@Table(name = "publication")
@Getter
@Setter
public class Publication implements Comparable<Publication> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotEmpty(message = "Заголовок публикации не может быть пустым")
    @Column(name = "title")
    @Size(max = 50, message = "Максимальная длина заголовка публикации – 50 символов")
    private String title;

    @Column(name = "content")
    @Size(max = 1024, message = "Максимальная длина текста в публикации – 1024 символов")
    private String content;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    private Person author;

    @OneToMany(mappedBy = "publication")
    @Cascade({org.hibernate.annotations.CascadeType.REMOVE,
              org.hibernate.annotations.CascadeType.PERSIST})
    private List<Image> images;

    @Override
    public int compareTo(Publication o) {
        return createdAt.compareTo(o.getCreatedAt());
    }
}
