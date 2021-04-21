package eu.europa.ec.eci.oct.webcommons.services.email;

import eu.europa.ec.eci.oct.entities.email.Email;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTobjectNotFoundException;

public interface EmailService {

	public void saveEmail(Email emailToBeSaved) throws OCTException, OCTobjectNotFoundException;
	public Email getEmailByAddress(String emailAddress) throws OCTException, OCTobjectNotFoundException;

}
