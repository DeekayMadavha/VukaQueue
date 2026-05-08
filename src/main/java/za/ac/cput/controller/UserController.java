package za.ac.cput.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.model.User;
import za.ac.cput.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // POST /api/users/register
    // Patient or staff self-registration
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        User created = userService.register(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // GET /api/users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable String id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/users/phone/{phone}
    // Used during login / OTP lookup
    @GetMapping("/phone/{phone}")
    public ResponseEntity<User> getByPhone(@PathVariable String phone) {
        return userService.findByPhone(phone)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/users?role=NURSE
    @GetMapping
    public ResponseEntity<List<User>> getAll(@RequestParam(required = false) String role) {
        if (role != null) {
            return ResponseEntity.ok(userService.findByRole(role));
        }
        return ResponseEntity.ok(userService.findAll());
    }

    // PATCH /api/users/{id}/verify-otp
    // Called after the patient confirms their OTP code
    @PatchMapping("/{id}/verify-otp")
    public ResponseEntity<User> verifyOtp(@PathVariable String id) {
        User verified = userService.verifyOtp(id);
        return ResponseEntity.ok(verified);
    }

    // PUT /api/users/{id}
    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable String id, @RequestBody User user) {
        User toUpdate = new User.Builder().copy(user).setUserId(id).build();
        User updated = userService.update(toUpdate);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/users/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}