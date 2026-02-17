package tn.esprit.esprit_market.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.esprit_market.entities.User;
import tn.esprit.esprit_market.repositories.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

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
}
