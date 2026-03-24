package tn.esprit.esprit_market.modules.event.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.event.dto.LiveSessionRequest;
import tn.esprit.esprit_market.modules.event.dto.LiveSessionResponse;
import tn.esprit.esprit_market.modules.event.entities.Event;
import tn.esprit.esprit_market.modules.event.entities.LiveSession;
import tn.esprit.esprit_market.modules.event.enums.LivePlatform;
import tn.esprit.esprit_market.modules.event.enums.LiveSessionStatus;
import tn.esprit.esprit_market.modules.event.repositories.EventRepository;
import tn.esprit.esprit_market.modules.event.repositories.LiveSessionRepository;
import tn.esprit.esprit_market.modules.store.entity.Store;
import tn.esprit.esprit_market.modules.store.service.IserviceStore;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.service.IUserService;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LiveSessionServiceTest {

    @Mock
    private LiveSessionRepository liveSessionRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private IUserService userService;

    @Mock
    private IserviceStore iserviceStore;

    @InjectMocks
    private LiveSessionService liveSessionService;

    private LiveSession fakeSession;
    private LiveSessionRequest fakeRequest;
    private User fakeCreator;
    private Event fakeEvent;

    @BeforeEach
    void setUp() {
        fakeCreator = new User();
        fakeCreator.setId(1L);
        fakeCreator.setName("John Doe");

        fakeEvent = Event.builder()
                .id(1L)
                .title("Tech Conference")
                .build();

        fakeSession = LiveSession.builder()
                .id(1L)
                .title("Spring Boot Q&A")
                .description("Live session about Spring Boot 3")
                .platform(LivePlatform.ZOOM)
                .link("http://zoom.us/j/123456")
                .scheduledAt(new Date())
                .status(LiveSessionStatus.SCHEDULED)
                .creator(fakeCreator)
                .event(fakeEvent)
                .build();

        fakeRequest = LiveSessionRequest.builder()
                .title("Spring Boot Q&A")
                .description("Live session about Spring Boot 3")
                .platform(LivePlatform.ZOOM)
                .link("http://zoom.us/j/123456")
                .scheduledAt(new Date())
                .build();
    }

    // ==================== CREATE ====================
    @Test
    void testCreateLiveSession_Success() {
        when(userService.getUserById(1L)).thenReturn(fakeCreator);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(fakeEvent));
        when(liveSessionRepository.save(any(LiveSession.class))).thenReturn(fakeSession);

        LiveSessionResponse result = liveSessionService.createLiveSession(1L, 1L, fakeRequest);

        assertNotNull(result);
        assertEquals("Spring Boot Q&A", result.getTitle());
        assertEquals(LiveSessionStatus.SCHEDULED, result.getStatus());
        verify(liveSessionRepository, times(1)).save(any(LiveSession.class));
    }

    @Test
    void testCreateLiveSession_InvalidCreator_ThrowsException() {
        when(userService.getUserById(99L)).thenThrow(new ResourceNotFoundException("User not found"));

        assertThrows(ResourceNotFoundException.class, () -> liveSessionService.createLiveSession(99L, 1L, fakeRequest));
        verify(liveSessionRepository, never()).save(any());
    }

    // ==================== READ ====================
    @Test
    void testGetAllLiveSessions_ReturnsList() {
        when(liveSessionRepository.findAll()).thenReturn(Arrays.asList(fakeSession));

        List<LiveSessionResponse> result = liveSessionService.getAllLiveSessions();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetLiveSessionById_Found() {
        when(liveSessionRepository.findById(1L)).thenReturn(Optional.of(fakeSession));

        LiveSessionResponse result = liveSessionService.getLiveSessionById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetLiveSessionById_NotFound_ThrowsException() {
        when(liveSessionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> liveSessionService.getLiveSessionById(99L));
    }

    // ==================== UPDATE ====================
    @Test
    void testUpdateLiveSession_Success() {
        fakeRequest.setTitle("Updated Title");
        when(liveSessionRepository.findById(1L)).thenReturn(Optional.of(fakeSession));
        when(liveSessionRepository.save(any(LiveSession.class))).thenReturn(fakeSession);

        LiveSessionResponse result = liveSessionService.updateLiveSession(1L, fakeRequest);

        assertNotNull(result);
        verify(liveSessionRepository, times(1)).save(any(LiveSession.class));
    }

    @Test
    void testUpdateStatus_ToEnded_Success() {
        when(liveSessionRepository.findById(1L)).thenReturn(Optional.of(fakeSession));
        when(liveSessionRepository.save(any(LiveSession.class))).thenReturn(fakeSession);

        LiveSessionResponse result = liveSessionService.updateStatus(1L, LiveSessionStatus.ENDED);

        assertNotNull(result);
        assertNotNull(fakeSession.getEndTime()); // Ensures endTime is set
    }

    // ==================== DELETE ====================
    @Test
    void testDeleteLiveSession_Success() {
        when(liveSessionRepository.findById(1L)).thenReturn(Optional.of(fakeSession));
        doNothing().when(liveSessionRepository).delete(fakeSession);

        liveSessionService.deleteLiveSession(1L);

        verify(liveSessionRepository, times(1)).delete(fakeSession);
    }
}
