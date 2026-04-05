package tn.esprit.esprit_market.modules.admin.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import tn.esprit.esprit_market.modules.admin.dto.AdminDashboardResponseDTO;
import tn.esprit.esprit_market.modules.admin.dto.UpdateUserRoleRequest;
import tn.esprit.esprit_market.modules.admin.dto.PagedResponseDTO;
import tn.esprit.esprit_market.modules.admin.service.AdminService;
import tn.esprit.esprit_market.modules.user.dto.UserResponseDTO;
import tn.esprit.esprit_market.modules.user.entity.User;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AdminController controller;

    private User user;
    private final String TEST_EMAIL = "admin@mail.com";

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(10L);
        user.setName("John Doe");
        user.setEmail(TEST_EMAIL);
    }

    @Test
    void testGetDashboard() {
        AdminDashboardResponseDTO dashboard = AdminDashboardResponseDTO.builder().build();
        when(adminService.getDashboard(7)).thenReturn(dashboard);

        ResponseEntity<AdminDashboardResponseDTO> response = controller.getDashboard(7);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(adminService).getDashboard(7);
    }

    @Test
    void testGetAllUsers() {
        // Mock PagedResponseDTO of Entity User
        PagedResponseDTO<User> mockPage = PagedResponseDTO.<User>builder()
                .items(Collections.singletonList(user))
                .page(0)
                .size(10)
                .totalElements(1L)
                .build();

        when(adminService.getUsers(null, null, null, 0, 10)).thenReturn(mockPage);

        ResponseEntity<PagedResponseDTO<UserResponseDTO>> response = controller.getAllUsers(null, null, null, 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getItems().size());
        assertEquals(10L, response.getBody().getItems().get(0).getId());
        verify(adminService).getUsers(null, null, null, 0, 10);
    }

    @Test
    void testToggleUserStatus() {
        when(authentication.getName()).thenReturn(TEST_EMAIL);
        when(adminService.toggleUserStatus(10L, TEST_EMAIL)).thenReturn(user);

        ResponseEntity<UserResponseDTO> response = controller.toggleUserStatus(10L, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(10L, response.getBody().getId());
        verify(adminService).toggleUserStatus(10L, TEST_EMAIL);
    }

    @Test
    void testUpdateUserRole() {
        UpdateUserRoleRequest req = new UpdateUserRoleRequest();
        req.setRole("SELLER");

        when(authentication.getName()).thenReturn(TEST_EMAIL);
        when(adminService.updateUserRole(10L, "SELLER", TEST_EMAIL)).thenReturn(user);

        ResponseEntity<UserResponseDTO> response = controller.updateUserRole(10L, req, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(adminService).updateUserRole(10L, "SELLER", TEST_EMAIL);
    }

    @Test
    void testDeleteUser() {
        when(authentication.getName()).thenReturn(TEST_EMAIL);
        doNothing().when(adminService).deleteUser(10L, TEST_EMAIL);

        ResponseEntity<Void> response = controller.deleteUser(10L, authentication);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(adminService).deleteUser(10L, TEST_EMAIL);
    }
}
