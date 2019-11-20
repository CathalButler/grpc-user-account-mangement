package ie.gmit.ds.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class UserLogin {
    @NotNull
    private int userId;
    @NotNull
    private String password;

    public UserLogin() {
    }

    public UserLogin(int userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    @JsonProperty
    public int getUserId() {
        return userId;
    }

    @JsonProperty
    public String getPassword() {
        return password;
    }
}//End login class
