package by.ladz.gusakov.SocialMedialApp.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserMessageDTO {

    private String senderName;

    @NotEmpty(message = "Вы должны ввести имя получателя")
    @Size(min = 1, max = 20, message = "Введите корректное имя получателя")
    private String recipientName;

    @NotEmpty(message = "Содержание сообщения не может быть пустым")
    @Size(max = 1024, message = "Максимальная длина сообщения – 1024 символа")
    private String content;
}
