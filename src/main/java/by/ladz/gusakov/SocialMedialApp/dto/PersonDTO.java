package by.ladz.gusakov.SocialMedialApp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class PersonDTO {

    @NotEmpty(message = "Необходимо указать имя пользователя")
    @Size(min = 1, max = 20, message = "Имя пользователя должно содержать от 1 до 20 символов")
    private String username;

    @NotEmpty(message = "Необходимо указать адрес электронной почты")
    @Email(message = "Некорректный адрес электронной почты")
    @Size(max = 256, message = "Длина адреса электронной почты не может превышать 256 символов")
    private String email;

    @NotEmpty(message = "Необходимо указать пароль")
    @Size(min = 6, max = 20, message = "Пароль должен содержать от 6 до 20 символов")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
