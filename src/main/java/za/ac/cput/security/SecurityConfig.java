package za.ac.cput.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF — not needed for stateless REST APIs
                .csrf(csrf -> csrf.disable())

                // Stateless session — no server-side session, JWT carries state
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        // ── PUBLIC endpoints (no token required) ──────────────────
                        .requestMatchers(HttpMethod.POST, "/api/users/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()

                        // ── PATIENT endpoints ─────────────────────────────────────
                        // Patients can view and manage their own appointments & queue
                        .requestMatchers(HttpMethod.POST,  "/api/appointments").hasAnyRole("PATIENT", "ADMIN")
                        .requestMatchers(HttpMethod.GET,   "/api/appointments/user/**").hasAnyRole("PATIENT", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/appointments/*/cancel").hasAnyRole("PATIENT", "ADMIN")
                        .requestMatchers(HttpMethod.POST,  "/api/queue/check-in").hasAnyRole("PATIENT", "ADMIN")
                        .requestMatchers(HttpMethod.GET,   "/api/queue/appointment/**").hasAnyRole("PATIENT", "ADMIN")
                        .requestMatchers(HttpMethod.GET,   "/api/notifications/user/**").hasAnyRole("PATIENT", "ADMIN")

                        // ── NURSE / DOCTOR (STAFF) endpoints ─────────────────────
                        // Staff manage the live queue and update appointment status
                        .requestMatchers(HttpMethod.GET,   "/api/queue/clinic/**").hasAnyRole("NURSE", "DOCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/queue/*/call").hasAnyRole("NURSE", "DOCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/queue/*/start").hasAnyRole("NURSE", "DOCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/queue/*/complete").hasAnyRole("NURSE", "DOCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/queue/*/skip").hasAnyRole("NURSE", "DOCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/appointments/*/complete").hasAnyRole("NURSE", "DOCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET,   "/api/appointments/clinic/**").hasAnyRole("NURSE", "DOCTOR", "ADMIN")

                        // ── ADMIN-only endpoints ──────────────────────────────────
                        // Full system control — reports, clinic management, user admin
                        .requestMatchers("/api/reports/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,   "/api/clinics").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/clinics/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/clinics/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,    "/api/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH,  "/api/appointments/*/no-show").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,    "/api/notifications/due").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,    "/api/notifications/failed/**").hasRole("ADMIN")

                        // ── OTP verification — authenticated user only ────────────
                        .requestMatchers(HttpMethod.PATCH, "/api/users/*/verify-otp").authenticated()

                        // ── Clinic search — any authenticated user ────────────────
                        .requestMatchers(HttpMethod.GET, "/api/clinics/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/clinics").authenticated()

                        // Deny everything else by default
                        .anyRequest().authenticated()
                )

                // Plug in our JWT filter before Spring's default username/password filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // BCrypt for hashing passwords stored in the User entity
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}