package tn.esprit.esprit_market.modules.shared.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import tn.esprit.esprit_market.modules.shared.dto.NotificationDTO;
import tn.esprit.esprit_market.modules.shared.service.NotificationService;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.service.IUserService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private IUserService userService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private NotificationController notificationController;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("user@test.com");
    }

    @Test
    void testMyNotifications() {
        when(authentication.getName()).thenReturn("user@test.com");
        when(userService.getUserByEmail("user@test.com")).thenReturn(testUser);
        
        NotificationDTO notif = new NotificationDTO();
        notif.setId(10L);
        when(notificationService.getMyNotifications(1L)).thenReturn(Arrays.asList(notif));

        ResponseEntity<List<NotificationDTO>> response = notificationController.myNotifications(authentication);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals(10L, response.getBody().get(0).getId());
    }

    @Test
    void testMarkAsRead() {
        when(authentication.getName()).thenReturn("user@test.com");
        when(userService.getUserByEmail("user@test.com")).thenReturn(testUser);
        
        NotificationDTO notif = new NotificationDTO();
        notif.setId(10L);
        notif.setRead(true);
        when(notificationService.markAsRead(10L, 1L)).thenReturn(notif);

        ResponseEntity<NotificationDTO> response = notificationController.markAsRead(10L, authentication);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(10L, response.getBody().getId());
        assertEquals(true, response.getBody().isRead());
    }
}
