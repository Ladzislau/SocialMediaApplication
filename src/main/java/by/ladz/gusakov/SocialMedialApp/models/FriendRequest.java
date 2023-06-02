package by.ladz.gusakov.SocialMedialApp.models;

import jakarta.persistence.*;


@Entity
@Table(name = "friend_request")
public class FriendRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "from_person_id", referencedColumnName = "id")
    private Person from;

    @ManyToOne(name = "to_person_id", )
    @JoinColumn(name = "to_person_id")
    private Person to;

    @Enumerated
    @Column(name = "status")
    private Status status;

    private enum Status{
        SUBSCRIBER, FRIEND
    }
}

