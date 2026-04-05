package tn.esprit.esprit_market.modules.event.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import tn.esprit.esprit_market.modules.event.dto.ChatMessageRequest;
import tn.esprit.esprit_market.modules.event.dto.ChatMessageResponse;
import tn.esprit.esprit_market.modules.event.service.IChatMessageService;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.service.IUserService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatMessageControllerTest {

    @Mock private IChatMessageService chatMessageService;
    @Mock private IUserService userService;
    @Mock private Authentication authentication;
    @InjectMocks private ChatMessageController controller;
    private ChatMessageResponse response;
    private User user;

    @BeforeEach
    void setUp() {
        response = new ChatMessageResponse();
        user = new User(); user.setId(1L); user.setEmail("user@mail.com");
    }

    @Test void testSendMessage() {
        when(authentication.getName()).thenReturn("user@mail.com");
        when(userService.getUserByEmail("user@mail.com")).thenReturn(user);
        when(chatMessageService.sendMessage(eq(1L), eq(1L), any(ChatMessageRequest.class))).thenReturn(response);
        ChatMessageRequest request = new ChatMessageRequest();
        ResponseEntity<ChatMessageResponse> res = controller.sendMessage(1L, request, authentication);
        assertEquals(HttpStatus.CREATED, res.getStatusCode());
    }

    @Test void testGetMessages() {
        when(chatMessageService.getMessagesByLiveSession(1L)).thenReturn(Arrays.asList(response));
        ResponseEntity<List<ChatMessageResponse>> res = controller.getMessages(1L);
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }
}
