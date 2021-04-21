package eu.europa.ec.eci.oct.webcommons.services.persistence;

import eu.europa.ec.eci.oct.entities.email.Email;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTobjectNotFoundException;

public interface EmailDAO {

	public void saveEmail(Email emailToBeSaved) throws PersistenceException;
	public Email findByUuid(String signatureIdentifier) throws PersistenceException, OCTobjectNotFoundException;
	public Email getEmailByAddress(String getEmailByAddress) throws PersistenceException;

}
