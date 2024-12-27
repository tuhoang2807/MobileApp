package com.example.moblie_app.api;
public class TokenProvider {
    private static String token;

    // Phương thức để lưu token
    public static void setToken(String newToken) {
        token = newToken;
    }

    // Phương thức để lấy token
    public static String getToken() {
        return token;
    }
}

