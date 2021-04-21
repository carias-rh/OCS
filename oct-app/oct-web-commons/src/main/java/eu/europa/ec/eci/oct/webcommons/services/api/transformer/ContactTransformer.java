package eu.europa.ec.eci.oct.webcommons.services.api.transformer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import eu.europa.ec.eci.oct.entities.admin.Contact;
import eu.europa.ec.eci.oct.entities.admin.ContactRole;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.contact.ContactDTO;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;

@Component
public class ContactTransformer extends BaseTransformer {

	public ContactDTO transform(Contact contactDAO) {
		if (contactDAO == null) {
			return null;
		}
		ContactDTO contactDTO = new ContactDTO();
		contactDTO.setEmail(contactDAO.getEmail());
		contactDTO.setFirstName(contactDAO.getFirstName());
		contactDTO.setFamilyName(contactDAO.getFamilyName());
		String contactRole = contactDAO.getContactRole().getRoleDescription();
		contactDTO.setRole(contactRole);
		if (contactRole.equals(ContactRole.LEGAL_ENTITY)) {
			contactDTO.setResidenceCountry(contactDAO.getCountry().getCode());
		}

		return contactDTO;
	}

	public Contact transform(ContactDTO contactDTO) throws OCTException {
		if (contactDTO == null) {
			return null;
		}
		Contact contactDAO = new Contact();
		contactDAO.setEmail(contactDTO.getEmail());
		contactDAO.setFirstName(contactDTO.getFirstName());
		contactDAO.setFamilyName(contactDTO.getFamilyName());
		ContactRole contactRole = contactService.getContactRoleByDescription(contactDTO.getRole());
		contactDAO.setContactRole(contactRole);
		contactDAO.setCountry(systemManager.getCountryByCode(contactDTO.getResidenceCountry()));
		return contactDAO;
	}

	public List<ContactDTO> transformListDAO(List<Contact> contactsDAO) throws OCTException {
		if (contactsDAO == null) {
			return null;
		}

		List<ContactDTO> dtoContacts = new ArrayList<ContactDTO>();
		for (Contact contactDAO : contactsDAO) {
			dtoContacts.add(transform(contactDAO));
		}
		return dtoContacts;
	}

	public List<Contact> transformListDTO(List<ContactDTO> contactsDTO) throws OCTException {
		if (contactsDTO == null) {
			return null;
		}

		List<Contact> daoContacts = new ArrayList<Contact>();
		for (ContactDTO contactDTO : contactsDTO) {
			daoContacts.add(transform(contactDTO));
		}
		return daoContacts;
	}

	// public ContactStrings getContactStringsFromContacts(List<Contact> contacts)
	// throws OCTException {
	// String representativeContact = "";
	// String representativeName = "";
	// String substituteName = "";
	// String substituteContact = "";
	// String entityContact = "";
	// // String representativeEmailString = "";
	// // String substituteEmailString = "";
	// List<String> members = new ArrayList<String>();
	// for (Contact contact : contacts) {
	// String roleDescription = contact.getContactRole().getRoleDescription();
	// String email = contact.getEmail();
	// String firstName = contact.getFirstName();
	// String familyName = contact.getFamilyName();
	// if (roleDescription.equals("representative")) {
	// representativeName = familyName + " " + firstName;
	// if(!StringUtils.isAllBlank(email)) {
	// representativeContact = representativeName + " (" + email + ")";
	// }
	// // representativeEmailString = email;
	// } else if (roleDescription.equals("substitute")) {
	// substituteName = familyName + " " + firstName;
	// substituteContact = substituteName + " (" + email + ")";
	// // substituteEmailString = email;
	// } else if (roleDescription.equals("member")) {
	// members.add(familyName + " " + firstName);
	// } else if (roleDescription.equals("entity")) {
	// members.add(firstName);
	// } else {
	// throw new OCTException("Role description not recognised: " +
	// roleDescription);
	// }
	// }
	//
	// String namesString = representativeContact + ", " + substituteContact;
	//
	// String organisersString = representativeName + ", " + substituteName + ", ";
	// // String emailsString = representativeEmailString+", "
	// +substituteEmailString;
	// for (int i = 0; i < members.size(); i++) {
	// String memberName = members.get(i);
	// organisersString += memberName;
	// if (i < members.size() - 1) {
	// organisersString += ", ";
	// }
	// }
	// ContactStrings contactStrings = new ContactStrings();
	// contactStrings.setNamesString(namesString);
	// contactStrings.setOrganisersString(organisersString);
	// // contactStrings.setEmailsString(emailsString);
	//
	// return contactStrings;
	// }

}
