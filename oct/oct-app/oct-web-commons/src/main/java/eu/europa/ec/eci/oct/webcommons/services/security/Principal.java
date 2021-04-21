package eu.europa.ec.eci.oct.webcommons.services.security;

import java.io.Serializable;
import java.util.Date;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * User: franzmh
 * Date: 20/01/17
 */
@Component
@Scope("prototype")
public class Principal implements Serializable {
    private static final long serialVersionUID = -1123060386436447799L;
    private String token;
    private String role;
    private Date date;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
