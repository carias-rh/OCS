package eu.europa.ec.eci.oct.webcommons.services.api.transformer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import eu.europa.ec.eci.oct.entities.email.Email;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.email.EmailDTO;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;

@Component
public class EmailTransformer extends BaseTransformer {

	public Email transform(EmailDTO emailDTO) throws OCTException {
		Email email = new Email();
		email.setEmailAddress(emailDTO.getEmailAddress());
		email.setComunicationLanguage(emailDTO.getComunicationLanguage());
		email.setSignatureIdentifier(emailDTO.getSignatureIdentifier());
		email.setInitiativeSubscription((byte) (emailDTO.getInitiativeSubscription() ? 1 : 0));
		return email;
	}

	public EmailDTO transform(Email email) throws OCTException {
		EmailDTO emailDTO = new EmailDTO();
		emailDTO.setEmailAddress(email.getEmailAddress());
		emailDTO.setComunicationLanguage(email.getComunicationLanguage());
		emailDTO.setEmailId(email.getId());
		emailDTO.setInitiativeSubscription(email.getInitiativeSubscription() == (byte) 1 ? true : false);
		return emailDTO;
	}

	public List<EmailDTO> transformList(List<Email> emails) throws OCTException {
		List<EmailDTO> emailsDTO = new ArrayList<>();
		for (Email email : emails) {
			emailsDTO.add(transform(email));
		}
		return emailsDTO;
	}

}
