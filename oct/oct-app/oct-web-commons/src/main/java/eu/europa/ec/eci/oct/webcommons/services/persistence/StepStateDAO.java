package eu.europa.ec.eci.oct.webcommons.services.persistence;

import eu.europa.ec.eci.oct.entities.admin.StepState;

public interface StepStateDAO {
	
	public StepState getStepState() throws PersistenceException;
	public void setStepState(StepState stepState) throws PersistenceException;
}
