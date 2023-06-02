package by.ladz.gusakov.SocialMedialApp.dto;

import jakarta.validation.constraints.NotEmpty;

public class AuthenticationDTO {

    @NotEmpty(message = "Необходимо указать имя пользователя или адрес электронной почты!")
    private String username;

    @NotEmpty(message = "Необходимо указать пароль!")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
