package eu.europa.ec.eci.oct.webcommons.services.email;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.eci.oct.entities.email.Email;
import eu.europa.ec.eci.oct.webcommons.services.BaseService;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTobjectNotFoundException;
import eu.europa.ec.eci.oct.webcommons.services.persistence.PersistenceException;

@Service
@Transactional
public class EmailServiceImpl extends BaseService implements EmailService {

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = OCTException.class)
	public void saveEmail(Email emailToBeSaved) throws OCTException, OCTobjectNotFoundException {
		String signatureIdentifier = emailToBeSaved.getSignatureIdentifier();

		// check if the signature UUID exists
		try {
			signatureService.findByUuid(signatureIdentifier);
		} catch (OCTobjectNotFoundException o) {
			throw new OCTobjectNotFoundException("Signature not found for UUID " + signatureIdentifier);
		}

		// check if the email has been already stored for this signature
		boolean isEmailAlreadyExistent;
		try {
			emailDAO.findByUuid(signatureIdentifier);
			isEmailAlreadyExistent = true;
		} catch (OCTobjectNotFoundException o) {
			isEmailAlreadyExistent = false;
		} catch (PersistenceException e) {
			logger.error("Unable to fetch email by UUID " + signatureIdentifier);
			throw new OCTException(e.getMessage());
		}

		if (isEmailAlreadyExistent) {
			throw new OCTException("Email already present for UUID but failed the first check." + signatureIdentifier);
		}

		try {
			emailDAO.saveEmail(emailToBeSaved);
		} catch (PersistenceException e) {
			logger.error("persistence problem while saving email", e);
			throw new OCTException("persistence problem while saving email", e);
		}

	}
	
	@Override
	@Transactional(readOnly = true)
	public Email getEmailByAddress(String emailAddress) throws OCTException, OCTobjectNotFoundException {
		try {
			Email emailByAddress = emailDAO.getEmailByAddress(emailAddress);
			if (emailByAddress == null) {
				throw new OCTobjectNotFoundException("No email found for emailAddress: " + emailAddress);
			}
			return emailByAddress;
		} catch (PersistenceException e) {
			logger.error("persistence problem while getting email by emailAddress [" + emailAddress + "]", e);
			throw new OCTException(
					"persistence problem while getting email by emailAddress [" + emailAddress + "]", e);
		}
	}

}
