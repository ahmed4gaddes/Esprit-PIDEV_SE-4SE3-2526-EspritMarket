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
import tn.esprit.esprit_market.modules.event.dto.LiveSessionRequest;
import tn.esprit.esprit_market.modules.event.dto.LiveSessionResponse;
import tn.esprit.esprit_market.modules.event.enums.LiveSessionStatus;
import tn.esprit.esprit_market.modules.event.service.ILiveSessionService;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.service.IUserService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LiveSessionControllerTest {

    @Mock
    private ILiveSessionService liveSessionService;

    @Mock
    private IUserService userService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private LiveSessionController liveSessionController;

    private LiveSessionRequest request;
    private LiveSessionResponse responseDto;
    private User fakeUser;

    @BeforeEach
    void setUp() {
        request = new LiveSessionRequest();
        request.setTitle("My Live Session");

        responseDto = new LiveSessionResponse();
        responseDto.setId(2L);
        responseDto.setTitle("My Live Session");

        fakeUser = new User();
        fakeUser.setId(99L);
        fakeUser.setEmail("creator@esprit.tn");
    }

    @Test
    void testCreateLiveSession() {
        when(authentication.getName()).thenReturn("creator@esprit.tn");
        when(userService.getUserByEmail("creator@esprit.tn")).thenReturn(fakeUser);
        
        when(liveSessionService.createLiveSession(eq(99L), eq(10L), any(LiveSessionRequest.class)))
                .thenReturn(responseDto);

        ResponseEntity<LiveSessionResponse> response = liveSessionController.createLiveSession(10L, request, authentication);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2L, response.getBody().getId());
        verify(liveSessionService).createLiveSession(eq(99L), eq(10L), any(LiveSessionRequest.class));
    }

    @Test
    void testGetAllLiveSessions() {
        when(liveSessionService.getAllLiveSessions()).thenReturn(Arrays.asList(responseDto));
        ResponseEntity<List<LiveSessionResponse>> response = liveSessionController.getAllLiveSessions();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(liveSessionService).getAllLiveSessions();
    }

    @Test
    void testGetLiveSessionsByEvent() {
        when(liveSessionService.getLiveSessionsByEvent(10L)).thenReturn(Arrays.asList(responseDto));
        ResponseEntity<List<LiveSessionResponse>> response = liveSessionController.getLiveSessionsByEvent(10L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(liveSessionService).getLiveSessionsByEvent(10L);
    }

    @Test
    void testGetLiveSessionsByStore() {
        when(liveSessionService.getLiveSessionsByStore(5L)).thenReturn(Arrays.asList(responseDto));
        ResponseEntity<List<LiveSessionResponse>> response = liveSessionController.getLiveSessionsByStore(5L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(liveSessionService).getLiveSessionsByStore(5L);
    }

    @Test
    void testGetLiveSessionsByService() {
        when(liveSessionService.getLiveSessionsByService(15L)).thenReturn(Arrays.asList(responseDto));
        ResponseEntity<List<LiveSessionResponse>> response = liveSessionController.getLiveSessionsByService(15L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(liveSessionService).getLiveSessionsByService(15L);
    }

    @Test
    void testGetLiveSessionsByCreator() {
        when(liveSessionService.getLiveSessionsByCreator(99L)).thenReturn(Arrays.asList(responseDto));
        ResponseEntity<List<LiveSessionResponse>> response = liveSessionController.getLiveSessionsByCreator(99L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(liveSessionService).getLiveSessionsByCreator(99L);
    }

    @Test
    void testGetLiveSessionById() {
        when(liveSessionService.getLiveSessionById(2L)).thenReturn(responseDto);
        ResponseEntity<LiveSessionResponse> response = liveSessionController.getLiveSessionById(2L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2L, response.getBody().getId());
    }

    @Test
    void testUpdateLiveSession() {
        when(authentication.getName()).thenReturn("user@esprit.tn");
        when(liveSessionService.updateLiveSession(eq(2L), any(LiveSessionRequest.class), eq("user@esprit.tn")))
                .thenReturn(responseDto);

        ResponseEntity<LiveSessionResponse> response = liveSessionController.updateLiveSession(2L, request, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(liveSessionService).updateLiveSession(eq(2L), any(LiveSessionRequest.class), eq("user@esprit.tn"));
    }

    @Test
    void testUpdateStatus() {
        when(authentication.getName()).thenReturn("user@esprit.tn");
        when(liveSessionService.updateStatus(2L, LiveSessionStatus.ENDED, "user@esprit.tn"))
                .thenReturn(responseDto);

        ResponseEntity<LiveSessionResponse> response = liveSessionController.updateStatus(2L, LiveSessionStatus.ENDED, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(liveSessionService).updateStatus(2L, LiveSessionStatus.ENDED, "user@esprit.tn");
    }

    @Test
    void testDeleteLiveSession() {
        when(authentication.getName()).thenReturn("user@esprit.tn");
        doNothing().when(liveSessionService).deleteLiveSession(2L, "user@esprit.tn");

        ResponseEntity<Void> response = liveSessionController.deleteLiveSession(2L, authentication);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(liveSessionService).deleteLiveSession(2L, "user@esprit.tn");
    }
}
