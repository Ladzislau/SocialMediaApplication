package by.ladz.gusakov.SocialMedialApp.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class RelationshipDTO {

    @NotEmpty(message = "Поле username не может быть пустым")
    @Size(min = 1, max = 20, message = "Введите корректное имя пользователя")
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
