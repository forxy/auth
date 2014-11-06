package auth.api.v1.pojo;

import java.io.Serializable;

public class Credentials implements Serializable {

    private static final long serialVersionUID = -6314351637400403449L;

    private String email;

    private String password;

    public Credentials(){}

    public Credentials(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
