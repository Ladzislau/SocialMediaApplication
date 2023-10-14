package by.ladz.gusakov.SocialMedialApp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Date;

@Entity
@Table(name = "message")
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

    public UserMessage() {
    }

    public UserMessage(Person sender, Person recipient, String content) {
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Person getSender() {
        return sender;
    }

    public void setSender(Person sender) {
        this.sender = sender;
    }

    public Person getRecipient() {
        return recipient;
    }

    public void setRecipient(Person recipient) {
        this.recipient = recipient;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getSentTime() {
        return sentTime;
    }

    public void setSentTime(Date sentTime) {
        this.sentTime = sentTime;
    }


    @Override
    public int compareTo(UserMessage o) {
        return sentTime.compareTo(o.sentTime);
    }
}
