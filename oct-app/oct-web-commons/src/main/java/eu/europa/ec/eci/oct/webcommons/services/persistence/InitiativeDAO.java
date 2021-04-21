package eu.europa.ec.eci.oct.webcommons.services.persistence;

import java.util.List;

import eu.europa.ec.eci.oct.entities.admin.InitiativeDescription;

public interface InitiativeDAO {
	
	InitiativeDescription getDefaultDescription() throws PersistenceException;
	
	void saveInitiativeDescription(InitiativeDescription id) throws PersistenceException;

	void updateInitiativeDescription(InitiativeDescription newDescription) throws PersistenceException;

	InitiativeDescription getDescriptionByLanguageCode(String languageCode) throws PersistenceException;

	InitiativeDescription getDescriptionById(long id) throws PersistenceException;

	List<InitiativeDescription> getAllDescriptions() throws PersistenceException;

	void deleteDescription(InitiativeDescription description) throws PersistenceException;

	
}
