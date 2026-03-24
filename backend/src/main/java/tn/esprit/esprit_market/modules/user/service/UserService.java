package tn.esprit.esprit_market.modules.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get user by ID
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new tn.esprit.esprit_market.exceptions.ResourceNotFoundException(
                        "User not found with id: " + id));
    }

    // Get user by email
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new tn.esprit.esprit_market.exceptions.ResourceNotFoundException(
                        "User not found with email: " + email));
    }

    // Create user
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new tn.esprit.esprit_market.exceptions.UserException("Email already exists: " + user.getEmail());
        }

        // CONTROL: SELLER must have @esprit.tn email
        if (user.getRole() == tn.esprit.esprit_market.modules.user.enums.Role.SELLER) {
            if (user.getEmail() == null || !user.getEmail().endsWith("@esprit.tn")) {
                throw new tn.esprit.esprit_market.exceptions.UserException(
                        "SELLER registration requires an @esprit.tn email address.");
            }
        }

        // CONTROL: Password must be at least 6 characters
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            throw new tn.esprit.esprit_market.exceptions.UserException(
                    "Password must be at least 6 characters.");
        }

        // CONTROL: Date of birth - user must be between 16 and 100 years old
        if (user.getDateOfBirth() != null) {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            java.util.Date today = cal.getTime();

            if (user.getDateOfBirth().after(today)) {
                throw new tn.esprit.esprit_market.exceptions.UserException(
                        "Date of birth cannot be in the future.");
            }

            cal.setTime(today);
            cal.add(java.util.Calendar.YEAR, -16);
            java.util.Date minAgeDate = cal.getTime();

            cal.setTime(today);
            cal.add(java.util.Calendar.YEAR, -100);
            java.util.Date maxAgeDate = cal.getTime();

            if (user.getDateOfBirth().after(minAgeDate)) {
                throw new tn.esprit.esprit_market.exceptions.UserException(
                        "You must be at least 16 years old to register.");
            }

            if (user.getDateOfBirth().before(maxAgeDate)) {
                throw new tn.esprit.esprit_market.exceptions.UserException(
                        "Invalid date of birth.");
            }
        }

        // CONTROL: Reset password validation (min 6 chars for new password)
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // Update user
    public User updateUser(Long id, User userDetails) {
        User user = getUserById(id);
        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        user.setRole(userDetails.getRole());
        user.setStoreActive(userDetails.isStoreActive());
        return userRepository.save(user);
    }

    // Delete user
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new tn.esprit.esprit_market.exceptions.ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    // Get users by role
    public List<User> getUsersByRole(tn.esprit.esprit_market.modules.user.enums.Role role) {
        return userRepository.findByRole(role);
    }



    // ADMIN: Toggle user status (Block/Unblock)
    public User toggleUserStatus(Long id) {
        User user = getUserById(id);
        user.setActive(!user.isActive());
        return userRepository.save(user);
    }
}
