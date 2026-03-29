package tn.esprit.esprit_market.modules.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.exceptions.UserException;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.enums.Role;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User fakeUser;
    private User fakeSeller;

    @BeforeEach
    void setUp() {
        fakeUser = new User();
        fakeUser.setId(1L);
        fakeUser.setName("Test User");
        fakeUser.setEmail("test@gmail.com");
        fakeUser.setPassword("password123");
        fakeUser.setRole(Role.CUSTOMER);
        fakeUser.setActive(true);
        // Set age to 20 years old
        fakeUser.setDateOfBirth(Date.from(LocalDate.now().minusYears(20).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        fakeSeller = new User();
        fakeSeller.setId(2L);
        fakeSeller.setName("Test Seller");
        fakeSeller.setEmail("seller@esprit.tn");
        fakeSeller.setPassword("sellerpass");
        fakeSeller.setRole(Role.SELLER);
        fakeSeller.setDateOfBirth(Date.from(LocalDate.now().minusYears(25).atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    // ==================== READ ====================
    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(fakeUser, fakeSeller));

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(fakeUser));

        User result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals("test@gmail.com", result.getEmail());
    }

    @Test
    void testGetUserById_NotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(99L));
    }

    // ==================== CREATE ====================
    @Test
    void testCreateUser_Customer_Success() {
        when(userRepository.existsByEmail("test@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed-pwd");
        when(userRepository.save(any(User.class))).thenReturn(fakeUser);

        User result = userService.createUser(fakeUser);

        assertNotNull(result);
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUser_EmailAlreadyExists_ThrowsException() {
        when(userRepository.existsByEmail("test@gmail.com")).thenReturn(true);

        UserException exception = assertThrows(UserException.class, () -> userService.createUser(fakeUser));
        assertTrue(exception.getMessage().contains("Email already exists"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testCreateUser_SellerWrongEmailDomain_ThrowsException() {
        fakeSeller.setEmail("seller@gmail.com"); // Invalid domain for seller

        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        UserException exception = assertThrows(UserException.class, () -> userService.createUser(fakeSeller));
        assertTrue(exception.getMessage().contains("SELLER registration requires an @esprit.tn"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testCreateUser_PasswordTooShort_ThrowsException() {
        fakeUser.setPassword("12345"); // Only 5 chars

        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        UserException exception = assertThrows(UserException.class, () -> userService.createUser(fakeUser));
        assertTrue(exception.getMessage().contains("Password must be at least 6 characters"));
    }

    @Test
    void testCreateUser_Underage_ThrowsException() {
        // Set age to 10 years old
        fakeUser.setDateOfBirth(Date.from(LocalDate.now().minusYears(10).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        UserException exception = assertThrows(UserException.class, () -> userService.createUser(fakeUser));
        assertTrue(exception.getMessage().contains("You must be at least 16 years old"));
    }

    // ==================== UPDATE & DELETE ====================
    @Test
    void testUpdateUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(fakeUser));
        when(userRepository.save(any(User.class))).thenReturn(fakeUser);

        User updatedDetails = new User();
        updatedDetails.setName("New Name");
        updatedDetails.setEmail("new@gmail.com");

        User result = userService.updateUser(1L, updatedDetails);

        assertEquals("New Name", fakeUser.getName());
        verify(userRepository, times(1)).save(fakeUser);
    }

    @Test
    void testDeleteUser_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testToggleUserStatus_Success() {
        assertTrue(fakeUser.isActive());
        when(userRepository.findById(1L)).thenReturn(Optional.of(fakeUser));
        when(userRepository.save(any(User.class))).thenReturn(fakeUser);

        User result = userService.toggleUserStatus(1L);

        assertFalse(result.isActive()); // Toggled status
        verify(userRepository, times(1)).save(fakeUser);
    }
}
