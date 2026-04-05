package tn.esprit.esprit_market.modules.user.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import tn.esprit.esprit_market.modules.user.dto.UserResponseDTO;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.enums.Role;
import tn.esprit.esprit_market.modules.user.service.IUserService;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private IUserService userService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserController userController;

    private User fakeUser;

    @BeforeEach
    void setUp() {
        fakeUser = new User();
        fakeUser.setId(1L);
        fakeUser.setName("Test User");
        fakeUser.setEmail("test@esprit.tn");
        fakeUser.setRole(Role.CUSTOMER);
        fakeUser.setActive(true);
        fakeUser.setDateOfBirth(new Date());
        fakeUser.setCreatedAt(new Date());
    }

    @Test
    void testGetAllUsers() {
        when(userService.getAllUsers()).thenReturn(Arrays.asList(fakeUser));

        ResponseEntity<List<UserResponseDTO>> response = userController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Test User", response.getBody().get(0).getName());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void testGetUserById() {
        when(userService.getUserById(1L)).thenReturn(fakeUser);

        ResponseEntity<UserResponseDTO> response = userController.getUserById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void testGetCurrentUser() {
        when(authentication.getName()).thenReturn("test@esprit.tn");
        when(userService.getUserByEmail("test@esprit.tn")).thenReturn(fakeUser);

        ResponseEntity<UserResponseDTO> response = userController.getCurrentUser(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("test@esprit.tn", response.getBody().getEmail());
        verify(userService, times(1)).getUserByEmail("test@esprit.tn");
    }

    @Test
    void testCreateUser() {
        when(userService.createUser(any(User.class))).thenReturn(fakeUser);

        ResponseEntity<UserResponseDTO> response = userController.createUser(fakeUser);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test User", response.getBody().getName());
        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void testUpdateUser() {
        // Assume updated user
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("Updated User");
        updatedUser.setEmail("test@esprit.tn");
        updatedUser.setRole(Role.CUSTOMER);
        updatedUser.setDateOfBirth(new Date());
        updatedUser.setCreatedAt(new Date());

        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);

        ResponseEntity<UserResponseDTO> response = userController.updateUser(1L, updatedUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated User", response.getBody().getName());
        verify(userService, times(1)).updateUser(eq(1L), any(User.class));
    }

    @Test
    void testDeleteUser() {
        doNothing().when(userService).deleteUser(1L);

        ResponseEntity<Void> response = userController.deleteUser(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    void testGetUsersByRole() {
        when(userService.getUsersByRole(Role.CUSTOMER)).thenReturn(Arrays.asList(fakeUser));

        ResponseEntity<List<UserResponseDTO>> response = userController.getUsersByRole(Role.CUSTOMER);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(userService, times(1)).getUsersByRole(Role.CUSTOMER);
    }
}
