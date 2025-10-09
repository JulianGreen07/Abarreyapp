package com.abarreyapp.model;

public class User {
    private int id;
    private String name;
    private String role;
    private String email;
    private String phone;
    private String passwordHash;

    public User() {}

    public User(int id, String name, String role, String email, String phone) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.email = email;
        this.phone = phone;
    }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
