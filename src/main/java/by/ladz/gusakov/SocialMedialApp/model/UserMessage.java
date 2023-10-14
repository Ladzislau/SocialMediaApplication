package by.ladz.gusakov.SocialMedialApp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "message")
@Getter
@Setter
public class UserMessage implements Comparable<UserMessage> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    private Person sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id", referencedColumnName = "id")
    private Person recipient;

    @Column(name = "content")
    @NotEmpty(message = "Содержание сообщения не может быть пустым")
    @Size(max = 1024, message = "Максимальная длина сообщения – 1024 символа")
    private String content;

    @Column(name = "sent_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date sentTime;

    @Override
    public int compareTo(UserMessage o) {
        return sentTime.compareTo(o.sentTime);
    }
}
