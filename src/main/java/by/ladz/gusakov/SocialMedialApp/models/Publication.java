package by.ladz.gusakov.SocialMedialApp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.Cascade;

import java.util.Date;
import java.util.List;


@Entity
@Table(name = "publication")
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
    private Person creator;

    @OneToMany(mappedBy = "publication")
    @Cascade({org.hibernate.annotations.CascadeType.REMOVE,
              org.hibernate.annotations.CascadeType.PERSIST})
    private List<Image> images;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String header) {
        this.title = header;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String body) {
        this.content = body;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Person getCreator() {
        return creator;
    }

    public void setCreator(Person creator) {
        this.creator = creator;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    @Override
    public int compareTo(Publication o) {
        return createdAt.compareTo(o.getCreatedAt());
    }
}
