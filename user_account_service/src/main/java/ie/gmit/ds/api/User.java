package ie.gmit.ds.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.protobuf.ByteString;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class User {
    @NotNull
    private int userId;
    @NotBlank
    @Length(min = 2, max = 255)
    private String userName;
    @Pattern(regexp = ".+@.+\\.[a-z]+")
    private String email;
    @NotNull
    private String password;

    private ByteString salt;
    private ByteString hashedPassword;

    public User() {
    }

    public User(int userId, String userName, String email, ByteString salt, ByteString hashedPassword) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.salt = salt;
        this.hashedPassword = hashedPassword;
    }

    public User(int userId, String userName, String email) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
    }

    @JsonProperty
    public int getUserId() {
        return userId;
    }

    @JsonProperty
    public String getUserName() {
        return userName;
    }

    @JsonProperty
    public String getEmail() {
        return email;
    }

    @JsonProperty
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public ByteString getSalt() {
        return salt;
    }

    @JsonProperty
    public ByteString getHashedPassword() {
        return hashedPassword;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", salt='" + salt + '\'' +
                '}';
    }
}//End class
