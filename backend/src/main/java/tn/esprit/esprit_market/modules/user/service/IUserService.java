package tn.esprit.esprit_market.modules.user.service;

import tn.esprit.esprit_market.modules.user.entity.User;
import java.util.List;

public interface IUserService {
    List<User> getAllUsers();
    User getUserById(Long id);
    User getUserByEmail(String email);
    User createUser(User user);
    User updateUser(Long id, User userDetails);
    void deleteUser(Long id);
    List<User> getUsersByRole(tn.esprit.esprit_market.modules.user.enums.Role role);
    User toggleUserStatus(Long id);
}
