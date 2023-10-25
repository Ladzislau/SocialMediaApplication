package by.ladz.gusakov.SocialMedialApp.dto;

import jakarta.validation.constraints.Email;
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
public class PersonDTO {

    @Size(min = 1, max = 20, message = "Имя пользователя должно содержать от 1 до 20 символов")
    private String username;

    @NotEmpty(message = "Необходимо указать адрес электронной почты")
    @Email(message = "Некорректный адрес электронной почты")
    @Size(max = 256, message = "Длина адреса электронной почты не может превышать 256 символов")
    private String email;

    @Size(min = 6, max = 20, message = "Пароль должен содержать от 6 до 20 символов")
    private String password;
}
