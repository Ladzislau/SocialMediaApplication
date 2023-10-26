package by.ladz.gusakov.SocialMedialApp.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserMessageDTO {

    @Size(min = 1, max = 20, message = "Некорректное имя получателя")
    private String recipientName;

    @NotEmpty(message = "Содержание сообщения не может быть пустым")
    @Size(max = 1024, message = "Максимальная длина сообщения – 1024 символа")
    private String content;
}
