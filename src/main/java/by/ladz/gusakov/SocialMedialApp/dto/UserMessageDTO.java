package by.ladz.gusakov.SocialMedialApp.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Date;

public class UserMessageDTO {

    private String senderName;

    @NotEmpty(message = "Вы должны ввести имя получателя")
    @Size(min = 1, max = 20, message = "Введите корректное имя получателя")
    private String recipientName;

    @NotEmpty(message = "Содержание сообщения не может быть пустым")
    @Size(max = 1024, message = "Максимальная длина сообщения – 1024 символа")
    private String content;

    private Date sentAt;

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }
}
