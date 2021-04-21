package eu.europa.ec.eci.oct.webcommons.services.api.domain.systemManager;

import java.io.Serializable;

import org.springframework.stereotype.Component;

@Component
public class InitiativeModeDTO implements Serializable{

    private String mode;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

}
