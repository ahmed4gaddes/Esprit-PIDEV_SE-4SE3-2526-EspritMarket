package tn.esprit.esprit_market.modules.shared.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.exceptions.UserException;
import tn.esprit.esprit_market.modules.shared.dto.NotificationDTO;
import tn.esprit.esprit_market.modules.shared.entity.Notification;
import tn.esprit.esprit_market.modules.shared.repository.NotificationRepository;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationService notificationService;

    private User user;
    private Notification notification;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(10L);

        notification = new Notification();
        notification.setId(1L);
        notification.setTitle("Test Title");
        notification.setMessage("Test Msg");
        notification.setRead(false);
        notification.setCreatedAt(new Date());
        notification.setUser(user);
    }

    @Test
    void testNotifyUser() {
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        notificationService.notifyUser(10L, "New Title", "New Msg");

        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void testNotifyUserThrowsExceptionWhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> notificationService.notifyUser(99L, "T", "M"));
    }

    @Test
    void testGetMyNotifications() {
        when(notificationRepository.findByUser_IdOrderByCreatedAtDesc(10L)).thenReturn(Arrays.asList(notification));

        List<NotificationDTO> results = notificationService.getMyNotifications(10L);

        assertEquals(1, results.size());
        assertEquals("Test Title", results.get(0).getTitle());
    }

    @Test
    void testMarkAsRead() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        NotificationDTO dto = notificationService.markAsRead(1L, 10L);

        assertTrue(notification.isRead());
        verify(notificationRepository).save(notification);
    }

    @Test
    void testMarkAsReadThrowsUserExceptionForWrongUser() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        assertThrows(UserException.class, () -> notificationService.markAsRead(1L, 99L));
    }

    @Test
    void testMarkAsReadThrowsNotFound() {
        when(notificationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> notificationService.markAsRead(99L, 10L));
    }
}
