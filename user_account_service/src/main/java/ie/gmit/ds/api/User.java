package ie.gmit.ds.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.protobuf.ByteString;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Base64;

/* Cathal Butler | G00346889
 * Class that handles User Objects with constraints set on member variables.
 * POJO
 */

@XmlRootElement(name = "user") //Used to generate xml
//Every non static, non transient field in a JAXB-bound class will be automatically bound to XML, unless annotated by XmlTransient.
@XmlAccessorType(XmlAccessType.FIELD)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    @NotNull
    private int userId;
    @NotBlank
    @Length(min = 2, max = 255)
    private String userName;
    @Pattern(regexp = ".+@.+\\.[a-z]+")
    private String email;
    @NotNull
    @Length(min = 6, max = 255)
    private String password;
    private String salt;
    private String hashedPassword;

    public User() {
    }

    //Constructor with salt and hashed password as a ByteString

    public User(int userId, String userName, String email, ByteString salt, ByteString hashedPassword) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.salt = Base64.getEncoder().encodeToString(salt.toByteArray());
        this.hashedPassword = Base64.getEncoder().encodeToString(hashedPassword.toByteArray());
    }//End constructor

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

    public String getPassword() {
        return password;
    }

    @JsonProperty
    public String getSalt() {
        return salt;
    }

    @JsonProperty
    public String getHashedPassword() {
        return hashedPassword;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", salt=" + salt +
                ", hashedPassword=" + hashedPassword +
                '}';
    }
}//End class