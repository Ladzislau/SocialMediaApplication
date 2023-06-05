package by.ladz.gusakov.SocialMedialApp.models;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "following")
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

    public Following() {
    }

    public Following(Person person, Person followee, Status status) {
        this.person = person;
        this.followee = followee;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Person getFollowee() {
        return followee;
    }

    public void setFollowee(Person followee) {
        this.followee = followee;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
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
