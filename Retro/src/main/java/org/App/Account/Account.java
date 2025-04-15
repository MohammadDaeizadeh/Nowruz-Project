package org.App.Account;

import org.App.utils.PasswordHasher;
import java.util.Objects;
import java.util.regex.Pattern;

public abstract class Account {
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MIN_AGE = 13;
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private final int id;
    private final String username;
    private final String email;
    private String password;
    private final int age;
    private boolean isActive;
    private boolean isBanned;
    private String bio;

    public Account(int id, String username, String email, String password, int age) {
        this.id = id;
        this.username = validateUsername(username);
        this.email = validateEmail(email);
        this.password = PasswordHasher.hashPassword(validatePassword(password));
        this.age = validateAge(age);
        this.isActive = true;
        this.bio = "";
    }

    // Validation methods
    private String validateUsername(String username) {
        Objects.requireNonNull(username, "Username cannot be null");
        if (username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (username.length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 characters");
        }
        return username;
    }

    private String validateEmail(String email) {
        Objects.requireNonNull(email, "Email cannot be null");
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        return email;
    }

    private String validatePassword(String password) {
        Objects.requireNonNull(password, "Password cannot be null");
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Password must be at least " + MIN_PASSWORD_LENGTH + " characters");
        }
        return password;
    }

    private int validateAge(int age) {
        if (age < MIN_AGE) {
            throw new IllegalArgumentException(
                    "User must be at least " + MIN_AGE + " years old");
        }
        return age;
    }


    public String getPassword() {
        return password;
    }

    // Getters and core methods
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public int getAge() { return age; }
    public boolean isActive() { return isActive; }
    public String getBio() { return bio; }


    public boolean verifyPassword(String inputPassword) {
        return PasswordHasher.verifyPassword(inputPassword, this.password);
    }


    public void changePassword(String oldPassword, String newPassword) {
        if (!verifyPassword(oldPassword)) {
            throw new SecurityException("Current password doesn't match");
        }
        this.password = PasswordHasher.hashPassword(validatePassword(newPassword));
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void activate() {
        this.isActive = true;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void ban() {
        this.isBanned = true;
        this.isActive = false; // Also deactivate
    }

    public void unban() {
        this.isBanned = false;
        this.isActive = true;
    }

    public void setBio(String bio) {
        this.bio = Objects.requireNonNullElse(bio, "");
    }


     //Common account actions
    public abstract String getAccountType();

    @Override
    public String toString() {
        return String.format(
                "Account[username='%s', email='%s', age=%d, active=%s, type=%s]",
                username, email, age, isActive, getAccountType());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;
        Account account = (Account) o;
        return email.equals(account.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(password);
    }
}