package tn.esprit.esprit_market.modules.admin.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.esprit_market.exceptions.UserException;
import tn.esprit.esprit_market.modules.event.repositories.EventRepository;
import tn.esprit.esprit_market.modules.event.repositories.LiveSessionRepository;
import tn.esprit.esprit_market.modules.event.repositories.TicketRepository;
import tn.esprit.esprit_market.modules.admin.repository.AdminAuditLogRepository;
import tn.esprit.esprit_market.modules.service.repository.InternshipApplicationRepository;
import tn.esprit.esprit_market.modules.service.repository.InternshipRepository;
import tn.esprit.esprit_market.modules.store.repository.IRepositoryProduct;
import tn.esprit.esprit_market.modules.store.repository.StoreRepository;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.enums.Role;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;
import tn.esprit.esprit_market.modules.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private StoreRepository storeRepository;
    @Mock
    private IRepositoryProduct productRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private LiveSessionRepository liveSessionRepository;
    @Mock
    private InternshipRepository internshipRepository;
    @Mock
    private InternshipApplicationRepository internshipApplicationRepository;
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private UserService userService;
    @Mock
    private AdminAuditLogRepository adminAuditLogRepository;

    @InjectMocks
    private AdminService adminService;

    @Test
    void updateUserRole_WhenActorDemotesSelf_ShouldThrowUserException() {
        User actor = user(1L, Role.ADMIN, true);

        when(userService.getUserById(1L)).thenReturn(actor);
        when(userService.getUserByEmail("admin@gmail.com")).thenReturn(actor);

        assertThrows(UserException.class, () -> adminService.updateUserRole(1L, "CUSTOMER", "admin@gmail.com"));
    }

    @Test
    void updateUserRole_WhenTargetIsLastActiveAdmin_ShouldThrowUserException() {
        User actor = user(1L, Role.ADMIN, true);
        User target = user(2L, Role.ADMIN, true);

        when(userService.getUserById(2L)).thenReturn(target);
        when(userService.getUserByEmail("admin@gmail.com")).thenReturn(actor);
        when(userRepository.findAll()).thenReturn(List.of(target));

        assertThrows(UserException.class, () -> adminService.updateUserRole(2L, "CUSTOMER", "admin@gmail.com"));
    }

    @Test
    void toggleUserStatus_WhenActorTargetsSelf_ShouldThrowUserException() {
        User actor = user(1L, Role.ADMIN, true);

        when(userService.getUserById(1L)).thenReturn(actor);
        when(userService.getUserByEmail("admin@gmail.com")).thenReturn(actor);

        assertThrows(UserException.class, () -> adminService.toggleUserStatus(1L, "admin@gmail.com"));
        verify(userService, never()).toggleUserStatus(1L);
    }

    @Test
    void toggleUserStatus_WhenTargetIsLastActiveAdmin_ShouldThrowUserException() {
        User actor = user(1L, Role.ADMIN, true);
        User target = user(2L, Role.ADMIN, true);

        when(userService.getUserById(2L)).thenReturn(target);
        when(userService.getUserByEmail("admin@gmail.com")).thenReturn(actor);
        when(userRepository.findAll()).thenReturn(List.of(target));

        assertThrows(UserException.class, () -> adminService.toggleUserStatus(2L, "admin@gmail.com"));
        verify(userService, never()).toggleUserStatus(2L);
    }

    @Test
    void deleteUser_WhenActorTargetsSelf_ShouldThrowUserException() {
        User actor = user(1L, Role.ADMIN, true);

        when(userService.getUserById(1L)).thenReturn(actor);
        when(userService.getUserByEmail("admin@gmail.com")).thenReturn(actor);

        assertThrows(UserException.class, () -> adminService.deleteUser(1L, "admin@gmail.com"));
        verify(userService, never()).deleteUser(1L);
    }

    @Test
    void deleteUser_WhenTargetIsLastActiveAdmin_ShouldThrowUserException() {
        User actor = user(1L, Role.ADMIN, true);
        User target = user(2L, Role.ADMIN, true);

        when(userService.getUserById(2L)).thenReturn(target);
        when(userService.getUserByEmail("admin@gmail.com")).thenReturn(actor);
        when(userRepository.findAll()).thenReturn(List.of(target));

        assertThrows(UserException.class, () -> adminService.deleteUser(2L, "admin@gmail.com"));
        verify(userService, never()).deleteUser(2L);
    }

    private User user(Long id, Role role, boolean active) {
        User user = new User();
        user.setId(id);
        user.setRole(role);
        user.setActive(active);
        return user;
    }
}
