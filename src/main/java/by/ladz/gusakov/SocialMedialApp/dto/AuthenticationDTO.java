package by.ladz.gusakov.SocialMedialApp.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationDTO {

    @NotEmpty(message = "Необходимо указать имя пользователя или адрес электронной почты!")
    private String usernameOrEmail;

    @NotEmpty(message = "Необходимо указать пароль!")
    private String password;
}
