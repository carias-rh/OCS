package eu.europa.ec.eci.oct.webcommons.services.persistence;


import java.util.List;

import eu.europa.ec.eci.oct.entities.admin.Contact;
import eu.europa.ec.eci.oct.entities.admin.ContactRole;

public interface ContactDAO {

	List<Contact> getAllContacts() throws PersistenceException;
	
	ContactRole getContactRoleByDescription(String roleDescription) throws PersistenceException;
	
	void saveContacts(List<Contact> contacts) throws PersistenceException;
	
}
