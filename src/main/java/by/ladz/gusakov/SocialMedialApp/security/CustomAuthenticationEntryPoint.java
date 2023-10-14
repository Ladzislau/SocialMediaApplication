package by.ladz.gusakov.SocialMedialApp.security;

import by.ladz.gusakov.SocialMedialApp.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;
    private final LocaleResolver localeResolver;

    @Autowired
    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper, LocaleResolver localeResolver) {
        this.objectMapper = objectMapper;
        this.localeResolver = localeResolver;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {

        ErrorResponse errorResponse = new ErrorResponse("Ошибка! Пользователь не аутентифицирован", new Date());

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Locale currentLocale = localeResolver.resolveLocale(request);

        objectMapper.setLocale(currentLocale);

        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
    }
}
