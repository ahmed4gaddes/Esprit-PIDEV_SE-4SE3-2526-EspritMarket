package tn.esprit.esprit_market.modules.admin.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.esprit_market.exceptions.UserException;
import tn.esprit.esprit_market.modules.admin.dto.AdminAuditLogDTO;
import tn.esprit.esprit_market.modules.admin.dto.AdminDashboardResponseDTO;
import tn.esprit.esprit_market.modules.admin.dto.PagedResponseDTO;
import tn.esprit.esprit_market.modules.admin.entity.AdminAuditLog;
import tn.esprit.esprit_market.modules.admin.repository.AdminAuditLogRepository;
import tn.esprit.esprit_market.modules.event.entities.Ticket;
import tn.esprit.esprit_market.modules.event.repositories.EventRepository;
import tn.esprit.esprit_market.modules.event.repositories.LiveSessionRepository;
import tn.esprit.esprit_market.modules.event.repositories.TicketRepository;
import tn.esprit.esprit_market.modules.service.dto.InternshipApplicationDTO;
import tn.esprit.esprit_market.modules.service.entity.Internship;
import tn.esprit.esprit_market.modules.service.entity.InternshipApplication;
import tn.esprit.esprit_market.modules.service.entity.InternshipApplicationStatus;
import tn.esprit.esprit_market.modules.service.repository.InternshipApplicationRepository;
import tn.esprit.esprit_market.modules.service.repository.InternshipRepository;
import tn.esprit.esprit_market.modules.store.repository.IRepositoryProduct;
import tn.esprit.esprit_market.modules.store.repository.StoreRepository;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.enums.Role;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;
import tn.esprit.esprit_market.modules.user.service.UserService;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private StoreRepository storeRepository;
    @Mock private IRepositoryProduct productRepository;
    @Mock private EventRepository eventRepository;
    @Mock private LiveSessionRepository liveSessionRepository;
    @Mock private InternshipRepository internshipRepository;
    @Mock private InternshipApplicationRepository internshipApplicationRepository;
    @Mock private TicketRepository ticketRepository;
    @Mock private UserService userService;
    @Mock private AdminAuditLogRepository adminAuditLogRepository;

    @InjectMocks private AdminService adminService;

    private User adminUser;
    private User regularUser;

    @BeforeEach
    void setUp() {
        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setEmail("admin@mail.com");
        adminUser.setRole(Role.ADMIN);
        adminUser.setActive(true);
        adminUser.setCreatedAt(Date.from(Instant.now()));

        regularUser = new User();
        regularUser.setId(2L);
        regularUser.setEmail("user@mail.com");
        regularUser.setName("Regular user");
        regularUser.setRole(Role.CUSTOMER);
        regularUser.setActive(true);
        regularUser.setCreatedAt(Date.from(Instant.now()));
    }

    @Test
    void testGetDashboard() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(adminUser, regularUser));
        
        InternshipApplication app = new InternshipApplication();
        app.setStatus(InternshipApplicationStatus.PENDING);
        app.setAppliedAt(Date.from(Instant.now()));
        when(internshipApplicationRepository.findAll()).thenReturn(List.of(app));

        Ticket ticket = new Ticket();
        ticket.setPrice(100.0);
        ticket.setPurchaseDate(Date.from(Instant.now()));
        when(ticketRepository.findAll()).thenReturn(List.of(ticket));

        when(storeRepository.count()).thenReturn(5L);
        when(productRepository.count()).thenReturn(10L);
        when(eventRepository.count()).thenReturn(2L);
        when(liveSessionRepository.count()).thenReturn(3L);
        when(internshipRepository.count()).thenReturn(4L);

        AdminDashboardResponseDTO res = adminService.getDashboard(7);

        assertNotNull(res);
        assertEquals(2, res.getKpis().getTotalUsers());
        assertEquals(2, res.getKpis().getActiveUsers());
        assertEquals(1, res.getKpis().getPendingInternshipApplications());
        assertEquals(100.0, res.getKpis().getTicketRevenue());
        assertEquals(1, res.getKpis().getTicketsSold());
        assertEquals(7, res.getActivity().size());
    }

    @Test
    void testGetUsers_withFilters() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(adminUser, regularUser));

        PagedResponseDTO<User> res = adminService.getUsers("CUSTOMER", true, "Regular", 0, 10);
        
        assertEquals(1, res.getTotalElements());
        assertEquals(regularUser.getId(), res.getItems().get(0).getId());
    }

    @Test
    void testUpdateUserRole_Success() {
        when(userService.getUserById(2L)).thenReturn(regularUser);
        when(userService.getUserByEmail("admin@mail.com")).thenReturn(adminUser);
        when(userRepository.save(any(User.class))).thenReturn(regularUser);

        User res = adminService.updateUserRole(2L, "SELLER", "admin@mail.com");

        assertEquals(Role.SELLER, res.getRole());
        verify(adminAuditLogRepository).save(any(AdminAuditLog.class));
    }

    @Test
    void testUpdateUserRole_DemoteLastAdmin_ThrowsException() {
        when(userService.getUserById(1L)).thenReturn(adminUser);
        when(userService.getUserByEmail("admin@mail.com")).thenReturn(adminUser);

        assertThrows(UserException.class, () -> adminService.updateUserRole(1L, "CUSTOMER", "admin@mail.com"));
    }

    @Test
    void testToggleUserStatus_Success() {
        when(userService.getUserById(2L)).thenReturn(regularUser);
        when(userService.getUserByEmail("admin@mail.com")).thenReturn(adminUser);
        
        User toggledUser = new User();
        toggledUser.setId(2L);
        toggledUser.setActive(false);
        when(userService.toggleUserStatus(2L)).thenReturn(toggledUser);

        User res = adminService.toggleUserStatus(2L, "admin@mail.com");

        assertFalse(res.isActive());
        verify(adminAuditLogRepository).save(any(AdminAuditLog.class));
    }

    @Test
    void testToggleUserStatus_ToggleSelf_ThrowsException() {
        when(userService.getUserById(1L)).thenReturn(adminUser);
        when(userService.getUserByEmail("admin@mail.com")).thenReturn(adminUser);

        assertThrows(UserException.class, () -> adminService.toggleUserStatus(1L, "admin@mail.com"));
    }

    @Test
    void testDeleteUser_Success() {
        when(userService.getUserById(2L)).thenReturn(regularUser);
        when(userService.getUserByEmail("admin@mail.com")).thenReturn(adminUser);
        doNothing().when(userService).deleteUser(2L);

        assertDoesNotThrow(() -> adminService.deleteUser(2L, "admin@mail.com"));
        verify(adminAuditLogRepository).save(any(AdminAuditLog.class));
    }

    @Test
    void testDeleteUser_DeleteSelf_ThrowsException() {
        when(userService.getUserById(1L)).thenReturn(adminUser);
        when(userService.getUserByEmail("admin@mail.com")).thenReturn(adminUser);

        assertThrows(UserException.class, () -> adminService.deleteUser(1L, "admin@mail.com"));
    }

    @Test
    void testGetAuditLogs() {
        AdminAuditLog log = AdminAuditLog.builder()
                .id(1L)
                .action("TEST_ACTION")
                .actorEmail("admin@mail.com")
                .createdAt(Date.from(Instant.now()))
                .build();
        when(adminAuditLogRepository.findAll()).thenReturn(List.of(log));

        PagedResponseDTO<AdminAuditLogDTO> res = adminService.getAuditLogs("TEST_ACTION", "admin", 0, 10);

        assertEquals(1, res.getTotalElements());
        assertEquals("TEST_ACTION", res.getItems().get(0).getAction());
    }

    @Test
    void testGetInternshipApplications() {
        InternshipApplication app = new InternshipApplication();
        app.setId(1L);
        app.setStatus(InternshipApplicationStatus.PENDING);
        app.setApplicant(regularUser);
        
        Internship internship = new Internship();
        internship.setId(10L);
        internship.setTitle("Java Dev");
        internship.setCreator(adminUser);
        app.setInternship(internship);

        when(internshipApplicationRepository.findAll()).thenReturn(List.of(app));

        PagedResponseDTO<InternshipApplicationDTO> res = adminService.getInternshipApplications(
                "PENDING", 10L, 1L, "Regular", 0, 10);

        assertEquals(1, res.getTotalElements());
        assertEquals(1L, res.getItems().get(0).getId());
    }
}
