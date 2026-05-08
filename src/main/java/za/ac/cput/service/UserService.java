//
//package za.ac.cput.service;
//
//import org.springframework.stereotype.Service;
//import za.ac.cput.model.User;
//import za.ac.cput.repository.UserRepository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class UserService {
//
//    private final UserRepository userRepository;
//
//    public UserService(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    // Called by: POST /api/users/register
//    // FIX: was registerUser() — renamed to match controller
//    public User register(User user) {
//        if (userRepository.existsByPhone(user.getPhone())) {
//            throw new IllegalArgumentException("A user with this phone number already exists.");
//        }
//        return userRepository.save(user);
//    }
//
//    // Called by: GET /api/users/{id}
//    public Optional<User> findById(String userId) {
//        return userRepository.findById(userId);
//    }
//
//    // Called by: GET /api/users/phone/{phone}
//    public Optional<User> findByPhone(String phone) {
//        return userRepository.findByPhone(phone);
//    }
//
//    // Called by: GET /api/users?role=
//    public List<User> findByRole(String role) {
//        return userRepository.findByRole(role);
//    }
//
//    // Called by: GET /api/users
//    public List<User> findAll() {
//        return userRepository.findAll();
//    }
//
//    // Called by: PATCH /api/users/{id}/verify-otp
//    public User verifyOtp(String userId) {
//        User existing = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
//        User verified = new User.Builder().copy(existing).setOtpVerified(true).build();
//        return userRepository.save(verified);
//    }
//
//    // Called by: PUT /api/users/{id}
//    // FIX: was updateUser() — renamed to match controller
//    public User update(User updatedUser) {
//        userRepository.findById(updatedUser.getUserId())
//                .orElseThrow(() -> new IllegalArgumentException("User not found: " + updatedUser.getUserId()));
//        return userRepository.save(updatedUser);
//    }
//
//    // Called by: DELETE /api/users/{id}
//    // FIX: was deleteUser() — renamed to match controller
//    public void delete(String userId) {
//        userRepository.deleteById(userId);
//    }
//}

package za.ac.cput.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import za.ac.cput.model.User;
import za.ac.cput.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Called by: POST /api/users/register
    // Hashes the raw password before saving — raw password is never stored
    public User register(User user) {
        if (userRepository.existsByPhone(user.getPhone())) {
            throw new IllegalArgumentException("A user with this phone number already exists.");
        }

        // Hash the password provided in passwordHash field before persisting
        String hashed = passwordEncoder.encode(user.getPasswordHash());
        User toSave = new User.Builder()
                .copy(user)
                .setPasswordHash(hashed)
                .build();

        return userRepository.save(toSave);
    }

    // Called by: GET /api/users/{id}
    public Optional<User> findById(String userId) {
        return userRepository.findById(userId);
    }

    // Called by: GET /api/users/phone/{phone}
    public Optional<User> findByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    // Called by: GET /api/users?role=
    public List<User> findByRole(String role) {
        return userRepository.findByRole(role);
    }

    // Called by: GET /api/users
    public List<User> findAll() {
        return userRepository.findAll();
    }

    // Called by: PATCH /api/users/{id}/verify-otp
    public User verifyOtp(String userId) {
        User existing = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        User verified = new User.Builder()
                .copy(existing)
                .setOtpVerified(true)
                .build();
        return userRepository.save(verified);
    }

    // Called by: PUT /api/users/{id}
    public User update(User updatedUser) {
        userRepository.findById(updatedUser.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + updatedUser.getUserId()));
        return userRepository.save(updatedUser);
    }

    // Called by: DELETE /api/users/{id}
    public void delete(String userId) {
        userRepository.deleteById(userId);
    }
}