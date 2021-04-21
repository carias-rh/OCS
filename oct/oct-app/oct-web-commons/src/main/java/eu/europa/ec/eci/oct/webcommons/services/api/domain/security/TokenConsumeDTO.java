package eu.europa.ec.eci.oct.webcommons.services.api.domain.security;

import java.io.Serializable;

import org.springframework.stereotype.Component;

/**
 * Created by falloda on 24/01/2017.
 */
@Component
public class TokenConsumeDTO implements Serializable{

    boolean consumed;

    public void setConsumed(boolean b){
        consumed = b;
    }
    public boolean getConsumed() {
        return consumed;
    }
}
