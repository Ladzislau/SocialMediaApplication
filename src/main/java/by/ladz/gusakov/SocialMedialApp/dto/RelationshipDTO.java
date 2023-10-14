package by.ladz.gusakov.SocialMedialApp.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RelationshipDTO {

    @NotEmpty(message = "Поле username не может быть пустым")
    @Size(min = 1, max = 20, message = "Введите корректное имя пользователя")
    private String username;
}
