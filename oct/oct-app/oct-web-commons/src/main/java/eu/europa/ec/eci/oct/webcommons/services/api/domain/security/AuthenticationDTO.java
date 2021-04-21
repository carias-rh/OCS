package eu.europa.ec.eci.oct.webcommons.services.api.domain.security;

import java.io.Serializable;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * User: franzmh
 * Date: 17/01/17
 */
@Component
@Scope("prototype")
public class AuthenticationDTO implements Serializable{
    private static final long serialVersionUID = 8323152379362140083L;

    public AuthenticationDTO(){}

    private String user;
    private String pwd;
    private String challengeResult;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getChallengeResult() {
        return challengeResult;
    }

    public void setChallengeResult(String challengeResult) {
        this.challengeResult = challengeResult;
    }
}
