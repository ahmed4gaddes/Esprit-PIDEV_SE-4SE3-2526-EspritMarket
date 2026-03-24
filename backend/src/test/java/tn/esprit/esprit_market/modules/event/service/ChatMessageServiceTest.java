package tn.esprit.esprit_market.modules.event.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.event.dto.ChatMessageRequest;
import tn.esprit.esprit_market.modules.event.dto.ChatMessageResponse;
import tn.esprit.esprit_market.modules.event.entities.ChatMessage;
import tn.esprit.esprit_market.modules.event.entities.LiveSession;
import tn.esprit.esprit_market.modules.event.repositories.ChatMessageRepository;
import tn.esprit.esprit_market.modules.event.repositories.LiveSessionRepository;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.enums.Role;
import tn.esprit.esprit_market.modules.user.service.IUserService;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceTest {

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private LiveSessionRepository liveSessionRepository;

    @Mock
    private IUserService userService;

    @InjectMocks
    private ChatMessageService chatMessageService;

    private LiveSession fakeSession;
    private ChatMessage fakeMessage;
    private ChatMessageRequest fakeRequest;
    private User fakeUser;

    @BeforeEach
    void setUp() {
        fakeSession = LiveSession.builder()
                .id(1L)
                .title("Spring Boot Q&A")
                .build();

        fakeUser = new User();
        fakeUser.setId(1L);
        fakeUser.setName("Alice");
        fakeUser.setRole(Role.CUSTOMER); // Ensure Role is set.

        fakeMessage = ChatMessage.builder()
                .id(1L)
                .content("Hello everyone!")
                .sender(fakeUser)
                .sentAt(new Date())
                .liveSession(fakeSession)
                .build();

        fakeRequest = ChatMessageRequest.builder()
                .content("Hello everyone!")
                .build();
    }

    // ==================== CREATE ====================
    @Test
    void testSendMessage_Success() {
        when(liveSessionRepository.findById(1L)).thenReturn(Optional.of(fakeSession));
        when(userService.getUserById(1L)).thenReturn(fakeUser);
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(fakeMessage);

        ChatMessageResponse result = chatMessageService.sendMessage(1L, 1L, fakeRequest);

        assertNotNull(result);
        assertEquals("Hello everyone!", result.getContent());
        assertEquals("Alice", result.getSenderName());
        verify(chatMessageRepository, times(1)).save(any(ChatMessage.class));
    }

    @Test
    void testSendMessage_InvalidSession_ThrowsException() {
        when(liveSessionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> chatMessageService.sendMessage(99L, 1L, fakeRequest));
        verify(chatMessageRepository, never()).save(any());
    }

    // ==================== READ ====================
    @Test
    void testGetMessagesByLiveSession_ReturnsList() {
        when(chatMessageRepository.findByLiveSessionIdOrderBySentAtAsc(1L)).thenReturn(Arrays.asList(fakeMessage));

        List<ChatMessageResponse> result = chatMessageService.getMessagesByLiveSession(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Hello everyone!", result.get(0).getContent());
    }
}
