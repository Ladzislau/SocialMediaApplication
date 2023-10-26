package by.ladz.gusakov.SocialMedialApp.controller;

import by.ladz.gusakov.SocialMedialApp.dto.UserMessageDTO;
import by.ladz.gusakov.SocialMedialApp.exception.ChatNotFoundException;
import by.ladz.gusakov.SocialMedialApp.exception.FriendshipRequiredException;
import by.ladz.gusakov.SocialMedialApp.model.Chat;
import by.ladz.gusakov.SocialMedialApp.model.Person;
import by.ladz.gusakov.SocialMedialApp.model.UserMessage;
import by.ladz.gusakov.SocialMedialApp.service.PeopleService;
import by.ladz.gusakov.SocialMedialApp.service.UserMessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MessageControllerTest {

    private final String SEND_MESSAGE_ENDPOINT = "/api/v1/messages";

    private final String GET_CHAT_ENDPOINT = "/api/v1/messages/chat";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserMessageService userMessageService;

    @MockBean
    private PeopleService peopleService;

    @Test
    @WithMockUser
    public void sendMessage_validDTO_200MessageSent() throws Exception {
        UserMessageDTO messageDTO = new UserMessageDTO("user_2", "test message");

        String requestBody = objectMapper.writeValueAsString(messageDTO);

        Person recipient = new Person();
        when(peopleService.findByUsername(messageDTO.getRecipientName())).thenReturn(Optional.of(recipient));

        MockHttpServletRequestBuilder mockRequest = post(SEND_MESSAGE_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(requestBody);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Сообщение успешно отправлено")));
    }

    @Test
    @WithMockUser
    public void sendMessage_invalidDTO_400ErrorMapReturned() throws Exception {
        UserMessageDTO messageDtoEmptyNameAndContent = new UserMessageDTO("", "");

        char[] nameChars = new char[21];
        Arrays.fill(nameChars, '*');
        char[] messageChars = new char[1025];
        Arrays.fill(messageChars, '*');
        UserMessageDTO messageDtoLargeNameAndContent = new UserMessageDTO(new String(nameChars), new String(messageChars));

        String requestBody1 = objectMapper.writeValueAsString(messageDtoEmptyNameAndContent);
        String requestBody2 = objectMapper.writeValueAsString(messageDtoLargeNameAndContent);

        String invalidRecipientNameError = "recipientName – Некорректное имя получателя";
        String emptyContentError = "content – Содержание сообщения не может быть пустым";
        String contentLengthError = "content – Максимальная длина сообщения – 1024 символа";

        MockHttpServletRequestBuilder mockRequestEmptyMessageContentAndRecipientName = post(SEND_MESSAGE_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(requestBody1);

        MockHttpServletRequestBuilder mockRequestLargeMessageContentAndRecipientName = post(SEND_MESSAGE_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(requestBody2);

        mockMvc.perform(mockRequestEmptyMessageContentAndRecipientName)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", aMapWithSize(2)))
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.error", allOf(
                        containsString(invalidRecipientNameError),
                        containsString(emptyContentError),
                        not(containsString(contentLengthError))
                )));

        mockMvc.perform(mockRequestLargeMessageContentAndRecipientName)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", aMapWithSize(2)))
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.error", allOf(
                        containsString(invalidRecipientNameError),
                        containsString(contentLengthError),
                        not(containsString(emptyContentError))
                )));
    }

    @Test
    public void sendMessage_unauthenticated_401ErrorMapReturned() throws Exception {
        UserMessageDTO messageDTO = new UserMessageDTO("user_2", "test message");

        String requestBody = objectMapper.writeValueAsString(messageDTO);

        MockHttpServletRequestBuilder mockRequest = post(SEND_MESSAGE_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(requestBody);

        mockMvc.perform(mockRequest)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$", aMapWithSize(2)))
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.error", is("Ошибка! Пользователь не аутентифицирован")));
    }

    @Test
    @WithMockUser
    public void sendMessage_senderAndRecipientAreNotFriends_403ErrorMapReturned() throws Exception {
        UserMessageDTO messageDTO = new UserMessageDTO("user_2", "test message");
        String expectedErrorMessage = "Вы не можете отправить сообщение пользователю, потому что не являетесь друзьями";

        String requestBody = objectMapper.writeValueAsString(messageDTO);

        Person recipient = new Person();
        when(peopleService.findByUsername(messageDTO.getRecipientName())).thenReturn(Optional.of(recipient));
        doThrow(new FriendshipRequiredException(expectedErrorMessage))
                .when(userMessageService).sendMessage(Mockito.any(UserMessage.class));

        MockHttpServletRequestBuilder mockRequest = post(SEND_MESSAGE_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(requestBody);

        mockMvc.perform(mockRequest)
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$", aMapWithSize(2)))
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.error", is(expectedErrorMessage)));
    }

    @Test
    @WithMockUser
    public void sendMessage_recipientDoesntExists_404ErrorMapReturned() throws Exception {
        UserMessageDTO messageDTO = new UserMessageDTO("non-existent_user", "test message");
        String expectedErrorMessage = "Невозможно отправить сообщение! Пользователя "
                + messageDTO.getRecipientName() + " не существует";

        String requestBody = objectMapper.writeValueAsString(messageDTO);

        MockHttpServletRequestBuilder mockRequest = post(SEND_MESSAGE_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(requestBody);

        mockMvc.perform(mockRequest)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", aMapWithSize(2)))
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.error", is(expectedErrorMessage)));
    }

    @Test
    @WithMockUser(username = "test_user")
    public void getChat_validUsernameParam_200ChatReturned() throws Exception {
        String secondChatMemberUsername = "user_2";

        Chat expectedChat = new Chat();
        expectedChat.setFirstChatMemberName("test_user");
        expectedChat.setSecondChatMemberName(secondChatMemberUsername);
        expectedChat.setMessages(new HashMap<>());

        Person expectedPerson = new Person();
        when(peopleService.findByUsername(secondChatMemberUsername)).thenReturn(Optional.of(expectedPerson));
        when(userMessageService.getChatWithPerson(expectedPerson)).thenReturn(expectedChat);

        MockHttpServletRequestBuilder mockRequest = get(GET_CHAT_ENDPOINT)
                .accept(MediaType.APPLICATION_JSON)
                .param("withUser", secondChatMemberUsername);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", aMapWithSize(3)))
                .andExpect(jsonPath("$.firstChatMemberName", is("test_user")))
                .andExpect(jsonPath("$.secondChatMemberName", is(secondChatMemberUsername)))
                .andExpect(jsonPath("$.messages", notNullValue()));
    }

    @Test
    public void getChat_unauthenticated_401ErrorMapReturned() throws Exception {
        String secondChatMemberUsername = "user_2";

        MockHttpServletRequestBuilder mockRequest = get(GET_CHAT_ENDPOINT)
                .accept(MediaType.APPLICATION_JSON)
                .param("withUser", secondChatMemberUsername);

        mockMvc.perform(mockRequest)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$", aMapWithSize(2)))
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.error", is("Ошибка! Пользователь не аутентифицирован")));
    }

    @Test
    @WithMockUser(username = "test_user")
    public void getChat_invalidUsernameParam_404ErrorMapReturned() throws Exception {
        String secondChatMemberUsername = "user_2";

        String invalidUsernameError = "Невозможно открыть чат! Пользователя " + secondChatMemberUsername + " не существует";

        MockHttpServletRequestBuilder mockRequest = get(GET_CHAT_ENDPOINT)
                .accept(MediaType.APPLICATION_JSON)
                .param("withUser", secondChatMemberUsername);

        mockMvc.perform(mockRequest)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", aMapWithSize(2)))
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.error", is(invalidUsernameError)));
    }

    @Test
    @WithMockUser
    public void getChat_chatDoestExists_404ErrorMapReturned() throws Exception {
        String secondChatMemberUsername = "user_2";
        String chatNotFoundError = "Чат с пользователем " + secondChatMemberUsername +
                " не найден! Вы не отправили ему ни одного сообщения";

        Person expectedPerson = new Person();
        when(peopleService.findByUsername(secondChatMemberUsername)).thenReturn(Optional.of(expectedPerson));
        when(userMessageService.getChatWithPerson(expectedPerson)).thenThrow(new ChatNotFoundException(chatNotFoundError));

        MockHttpServletRequestBuilder mockRequest = get(GET_CHAT_ENDPOINT)
                .accept(MediaType.APPLICATION_JSON)
                .param("withUser", secondChatMemberUsername);

        mockMvc.perform(mockRequest)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", aMapWithSize(2)))
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.error", is(chatNotFoundError)));
    }
}