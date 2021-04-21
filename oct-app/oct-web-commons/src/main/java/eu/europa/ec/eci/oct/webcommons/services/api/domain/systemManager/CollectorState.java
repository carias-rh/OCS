package eu.europa.ec.eci.oct.webcommons.services.api.domain.systemManager;

import java.io.Serializable;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("prototype")
public class CollectorState implements Serializable{
	
	private static final long serialVersionUID = -3266015748977495828L;
	
	public CollectorState(){}
	
	public static String ON = "ON";
	public static String OFF = "OFF";
	
    public String collectionMode;
    
	public String getCollectionMode() {
		return collectionMode;
	}

	public void setCollectionMode(String collectionMode) {
		this.collectionMode = collectionMode;
	}

	@Override
	public String toString() {
		return "CollectorState [collectionMode=" + collectionMode + "]";
	}
    
}
