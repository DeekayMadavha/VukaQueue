package za.ac.cput.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.model.User;
import za.ac.cput.security.JwtUtil;
import za.ac.cput.service.UserService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService,
                          JwtUtil jwtUtil,
                          PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    // POST /api/auth/login
    // Body: { "phone": "0821234567", "password": "yourpassword" }
    // Returns: { "token": "eyJ...", "role": "PATIENT", "userId": "..." }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String phone    = body.get("phone");
        String password = body.get("password");

        if (phone == null || password == null) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Phone and password are required."));
        }

        // Look up user by phone number
        User user = userService.findByPhone(phone)
                .orElse(null);

        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid phone number or password."));
        }

        // Verify OTP before allowing login
        if (!user.isOtpVerified()) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Phone number not verified. Please complete OTP verification."));
        }

        // Check password against stored BCrypt hash
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid phone number or password."));
        }

        // Issue JWT token
        String token = jwtUtil.generateToken(user.getPhone(), user.getRole());

        return ResponseEntity.ok(Map.of(
                "token",  token,
                "role",   user.getRole(),
                "userId", user.getUserId(),
                "name",   user.getName()
        ));
    }
}