package ie.gmit.ds.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "userlogin")
//Every non static, non transient field in a JAXB-bound class will be automatically bound to XML, unless annotated by XmlTransient.
@XmlAccessorType(XmlAccessType.FIELD)
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
