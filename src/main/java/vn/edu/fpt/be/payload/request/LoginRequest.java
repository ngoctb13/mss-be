package vn.edu.fpt.be.payload.request;

import lombok.*;

@Getter
public class LoginRequest {
    private String username;
    private String password;

    private void setUsername(String username) {
        this.username = username;
    }

    private void setPassword(String password) {
        this.password = password;
    }
}
