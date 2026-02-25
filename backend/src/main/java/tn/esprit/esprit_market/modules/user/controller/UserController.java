package tn.esprit.esprit_market.modules.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.user.dto.UserResponseDTO;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Helper method to map User to UserResponseDTO manually (No ModelMapper needed)
    private UserResponseDTO mapToDTO(User user) {
        return UserResponseDTO.builder().id(user.getId()).name(user.getName()).email(user.getEmail())
                .role(user.getRole()).storeActive(user.isStoreActive()).phoneNumber(user.getPhoneNumber())
                .address(user.getAddress()).profilePicture(user.getProfilePicture()).totalSales(user.getTotalSales())
                .dateOfBirth(
                        user.getDateOfBirth() != null ? new java.sql.Date(user.getDateOfBirth().getTime()).toLocalDate()
                                : null)
                .createdAt(user.getCreatedAt() != null
                        ? user.getCreatedAt().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()
                        : null)
                .isActive(user.isActive()).build();
    }

    // GET /api/users — Get all users
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers().stream().map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    // GET /api/users/{id} — Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(mapToDTO(user));
    }

    // POST /api/users — Create new user
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody User user) {
        User createdUser = userService.createUser(user);
        return new ResponseEntity<>(mapToDTO(createdUser), HttpStatus.CREATED);
    }

    // PUT /api/users/{id} — Update user
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(mapToDTO(updatedUser));
    }

    // DELETE /api/users/{id} — Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/users/role/{role} — Get users by role
    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserResponseDTO>> getUsersByRole(
            @PathVariable tn.esprit.esprit_market.modules.user.enums.Role role) {
        List<UserResponseDTO> users = userService.getUsersByRole(role).stream().map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }
}
