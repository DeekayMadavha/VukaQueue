package za.ac.cput.model;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "user_id", updatable = false, nullable = false)
    private String userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String phone;

    @Column(nullable = false)
    private String role; // PATIENT, NURSE, DOCTOR, ADMIN

    @Column(nullable = false)
    private String passwordHash;

    private boolean otpVerified;

    protected User() {}

    private User(Builder builder) {
        this.userId = builder.userId;
        this.name = builder.name;
        this.phone = builder.phone;
        this.role = builder.role;
        this.passwordHash = builder.passwordHash;
        this.otpVerified = builder.otpVerified;
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getRole() { return role; }
    public String getPasswordHash() { return passwordHash; }
    public boolean isOtpVerified() { return otpVerified; }

    public static class Builder {
        private String userId;
        private String name;
        private String phone;
        private String role;
        private String passwordHash;
        private boolean otpVerified;

        public Builder setUserId(String userId) { this.userId = userId; return this; }
        public Builder setName(String name) { this.name = name; return this; }
        public Builder setPhone(String phone) { this.phone = phone; return this; }
        public Builder setRole(String role) { this.role = role; return this; }
        public Builder setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; return this; }
        public Builder setOtpVerified(boolean otpVerified) { this.otpVerified = otpVerified; return this; }

        public Builder copy(User u) {
            this.userId = u.userId;
            this.name = u.name;
            this.phone = u.phone;
            this.role = u.role;
            this.passwordHash = u.passwordHash;
            this.otpVerified = u.otpVerified;
            return this;
        }

        public User build() { return new User(this); }
    }
}