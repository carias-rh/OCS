package eu.europa.ec.eci.oct.webcommons.services.contact;

import java.util.List;

import eu.europa.ec.eci.oct.entities.admin.Contact;
import eu.europa.ec.eci.oct.entities.admin.ContactRole;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;

public interface ContactService {
	
	public List<Contact> getAllContacts() throws OCTException;
	public void saveContacts(List<Contact> contacts) throws OCTException;
	public ContactRole getContactRoleByDescription(String role) throws OCTException;

}
