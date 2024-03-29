package by.ladz.gusakov.SocialMedialApp.controller;

import by.ladz.gusakov.SocialMedialApp.dto.AuthenticationDTO;
import by.ladz.gusakov.SocialMedialApp.dto.PersonDTO;
import by.ladz.gusakov.SocialMedialApp.model.Person;
import by.ladz.gusakov.SocialMedialApp.service.PeopleService;
import by.ladz.gusakov.SocialMedialApp.service.PersonDetailsService;
import by.ladz.gusakov.SocialMedialApp.service.RegistrationService;
import by.ladz.gusakov.SocialMedialApp.util.JWTUtil;
import by.ladz.gusakov.SocialMedialApp.util.PersonValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    private final String REGISTRATION_ENDPOINT = "/api/v1/auth/registration";
    private final String LOGIN_ENDPOINT = "/api/v1/auth/login";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PeopleService peopleService;

    @MockBean
    private PersonDetailsService personDetailsService;

    @MockBean
    private RegistrationService registrationService;

    @MockBean
    private AuthenticationProvider authenticationProvider;

    @MockBean
    private PersonValidator personValidator;

    @MockBean
    private JWTUtil jwtUtil;

    @Test
    public void performRegistration_validDTO_200JwtTokenReturned() throws Exception {
        PersonDTO personDTO = new PersonDTO("test_user", "test@mail.com", "password");

        String expectedToken = "mock-jwt-token";

        when(jwtUtil.generateToken(personDTO.getUsername())).thenReturn(expectedToken);

        String requestBody = objectMapper.writeValueAsString(personDTO);

        MockHttpServletRequestBuilder mockRequest = post(REGISTRATION_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(requestBody);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt-token", is(expectedToken)));
    }

    @Test
    public void performRegistration_invalidDTO_400ErrorMapReturned() throws Exception {
        PersonDTO personDTO = new PersonDTO("", "test", "");

        String requestBody = objectMapper.writeValueAsString(personDTO);

        String usernameLengthError = "username – Имя пользователя должно содержать от 1 до 20 символов";
        String invalidEmailError = "email – Некорректный адрес электронной почты";
        String passwordLengthError = "password – Пароль должен содержать от 6 до 20 символов";
        String emptyEmailError = "email – Необходимо указать адрес электронной почты";
        String emailLengthError = "email – Длина адреса электронной почты не может превышать 256 символов";

        MockHttpServletRequestBuilder mockRequest = post(REGISTRATION_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(requestBody);

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", aMapWithSize(2)))
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.error", allOf(
                        containsString(usernameLengthError),
                        containsString(invalidEmailError),
                        containsString(passwordLengthError),
                        not(containsString(emptyEmailError)),
                        not(containsString(emailLengthError))
                )));
    }

    @Test
    public void performLogin_validDTO_200JwtTokenReturned() throws Exception {
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("test_user", "ValidPassword");

        String requestBody = objectMapper.writeValueAsString(authenticationDTO);
        String expectedToken = "mock-jwt-token";

        Person person = new Person();
        when(peopleService.findByUsernameOrEmail(authenticationDTO.getUsernameOrEmail())).thenReturn(Optional.of(person));
        when(jwtUtil.generateToken(person.getUsername())).thenReturn(expectedToken);

        MockHttpServletRequestBuilder mockRequest = patch(LOGIN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(requestBody);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt-token", is(expectedToken)));
    }

    @Test
    public void performLogin_invalidDTO_401ErrorMapReturned() throws Exception {
        AuthenticationDTO authenticationDtoBadCredentials = new AuthenticationDTO("test_user", "invalidPassword");
        AuthenticationDTO authenticationDtoInvalidFields = new AuthenticationDTO("", "");

        String badCredentialsError = "Неправильное имя или пароль";
        String emptyUsernameOrEmailError = "Необходимо указать имя пользователя или адрес электронной почты!";
        String emptyPasswordError = "Необходимо указать пароль!";

        String requestBodyBadCredentials = objectMapper.writeValueAsString(authenticationDtoBadCredentials);
        String requestBodyInvalidDTO = objectMapper.writeValueAsString(authenticationDtoInvalidFields);

        UsernamePasswordAuthenticationToken authInputToken = new UsernamePasswordAuthenticationToken(
                authenticationDtoBadCredentials.getUsernameOrEmail(), authenticationDtoBadCredentials.getPassword());
        when(authenticationProvider.authenticate(authInputToken)).thenThrow(new BadCredentialsException(""));

        MockHttpServletRequestBuilder mockRequestBadCredentials = patch(LOGIN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(requestBodyBadCredentials);

        MockHttpServletRequestBuilder mockRequestInvalidDTO = patch(LOGIN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(requestBodyInvalidDTO);

        mockMvc.perform(mockRequestBadCredentials)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", is(badCredentialsError)));

        mockMvc.perform(mockRequestInvalidDTO)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", allOf(
                        containsString(emptyUsernameOrEmailError),
                        containsString(emptyPasswordError),
                        not(containsString(badCredentialsError))
                )));
    }
}