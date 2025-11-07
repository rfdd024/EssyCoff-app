package com.example.essycoff_cashier.models;

/**
 * Model class untuk data pengguna (User)
 * Digunakan untuk menyimpan informasi staff dan manager
 */
public class User {
    private String id;
    private String username;
    private String password;
    private String fullName;
    private String role; // "STAFF" atau "MANAGER"
    private String email;
    private boolean isActive;
    private String createdAt;

    // Constructor kosong untuk JSON parsing
    public User() {}

    // Constructor dengan parameter
    public User(String id, String username, String fullName, String role, String email) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
        this.email = email;
        this.isActive = true;
    }

    // Getter dan Setter methods
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public void setId(int id) {
        this.id = String.valueOf(id);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    // Method untuk mengecek apakah user adalah manager
    public boolean isManager() {
        return "MANAGER".equals(role);
    }

    // Method untuk mengecek apakah user adalah staff
    public boolean isStaff() {
        return "STAFF".equals(role);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role='" + role + '\'' +
                ", email='" + email + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
