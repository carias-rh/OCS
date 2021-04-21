package eu.europa.ec.eci.oct.webcommons.services.api.domain.systemManager;

import java.io.Serializable;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class StepStateChangeDTO implements Serializable{

	private static final long serialVersionUID = 5413193741414144360L;
	
    private boolean active;

    private String step;
    
    public StepStateChangeDTO(){
    	
    }

    public StepStateChangeDTO(boolean active, String step){
    	this.active = active;
    	this.step = step;
    }
    
    
    public boolean isActive (){
        return active;
    }

    public void setActive(boolean active){
        this.active = active;
    }

    public String getStep (){
        return step;
    }

    public void setStep (String step){
        this.step = step;
    }

    @Override
    public String toString(){
        return "StepStateChangeDTO [active = "+active+", step = "+step+"]";
    }
    
}
