package vn.edu.fpt.be.payload.response;

import java.util.List;

public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String role;
    private final String status;

    public AuthResponse(String accessToken, Long id, String username, String role, String status) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.role = role;
        this.status = status;
    }

    public String getAccessToken() {
        return token;
    }

    public void setAccessToken(String accessToken) {
        this.token = accessToken;
    }

    public String getTokenType() {
        return type;
    }

    public void setTokenType(String tokenType) {
        this.type = tokenType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
    public String getStatus() {
        return status;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

}
