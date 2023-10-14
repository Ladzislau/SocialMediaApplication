package by.ladz.gusakov.SocialMedialApp.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "friendship")
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    private Person person;

    @ManyToOne
    @JoinColumn(name = "friend_id", referencedColumnName = "id")
    private Person friend;

    public Friendship() {
    }

    public Friendship(Person person, Person friend) {
        this.person = person;
        this.friend = friend;
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

    public Person getFriend() {
        return friend;
    }

    public void setFriend(Person friend) {
        this.friend = friend;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friendship that = (Friendship) o;
        return Objects.equals(person, that.person) && Objects.equals(friend, that.friend);
    }

    @Override
    public int hashCode() {
        return Objects.hash(person, friend);
    }
}
