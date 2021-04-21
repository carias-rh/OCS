package eu.europa.ec.eci.oct.webcommons.services.api.domain.systemManager;

import java.io.Serializable;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class SystemStateDTO implements Serializable{
	
	private static final long serialVersionUID = 4386120426395522299L;

	public SystemStateDTO(){}
	
	private boolean isOnline;
	private boolean isCollecting;
	private boolean isInitialized;

	public boolean isOnline() {
		return isOnline;
	}
	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}
	public boolean isCollecting() {
		return isCollecting;
	}
	public void setCollecting(boolean isCollecting) {
		this.isCollecting = isCollecting;
	}
	public boolean isInitialized() {
		return isInitialized;
	}
	public void setInitialized(boolean isInitialized) {
		this.isInitialized = isInitialized;
	}
	
	@Override
	public String toString() {
		return "SystemStateDTO [isOnline=" + isOnline + ", isCollecting=" + isCollecting + ", isInitialized=" + isInitialized + "]";
	}
	
}
