package shared.logic;

import java.io.Serializable;

public class AuthResponse implements Serializable {
    private boolean success;
    private String token;

    public AuthResponse(boolean success, String token) {
        this.success = success;
        this.token = token;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getToken() {
        return token;
    }
}
