package by.ladz.gusakov.SocialMedialApp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "following")
@NoArgsConstructor
@Getter
@Setter
public class Following {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    private Person person;

    @ManyToOne
    @JoinColumn(name = "followee_id", referencedColumnName = "id")
    private Person followee;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    public enum Status {
        SENT, DECLINED, ACCEPTED
    }

    public Following(Person person, Person followee, Status status) {
        this.person = person;
        this.followee = followee;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Following following = (Following) o;
        return Objects.equals(person, following.person) && Objects.equals(followee, following.followee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(person, followee);
    }
}
