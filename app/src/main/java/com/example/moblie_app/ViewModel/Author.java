package com.example.moblie_app.ViewModel;

public class Author {
    private int id;

    private String token;
    private String username;
    private String password;
    private int role_id;
    private String email;
    private String birthday;

    private String role;

    public Author(String username, String password, int role_id, String email, String birthday, String role) {
        this.username = username;
        this.password = password;
        this.role_id = role_id;
        this.email = email;
        this.birthday = birthday;
        this.token = token;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
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

    public int getRole_id() {
        return role_id;
    }

    public void setRole_id(int role_id) {
        this.role_id = role_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getRole() {
        return role;
    }

}
