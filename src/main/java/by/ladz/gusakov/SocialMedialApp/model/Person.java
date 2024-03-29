package by.ladz.gusakov.SocialMedialApp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "Person")
@NoArgsConstructor
@Getter
@Setter
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "username")
    @NotEmpty(message = "Необходимо указать имя пользователя")
    @Size(min = 1, max = 20, message = "Имя пользователя должно содержать от 1 до 20 символов")
    private String username;

    @Column(name = "email")
    @NotEmpty(message = "Необходимо указать адрес электронной почты")
    @Email(message = "Некорректный адрес электронной почты")
    @Size(max = 256, message = "Длина адреса электронной почты не может превышать 256 символов")
    private String email;

    @Column(name = "password")
    @NotEmpty(message = "Необходимо указать пароль!")
    private String password;

    @OneToMany(mappedBy = "author")
    @Cascade(org.hibernate.annotations.CascadeType.REMOVE)
    private List<Publication> publications;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return id == person.id && Objects.equals(username, person.username) && Objects.equals(email, person.email) && Objects.equals(password, person.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email, password);
    }
}
