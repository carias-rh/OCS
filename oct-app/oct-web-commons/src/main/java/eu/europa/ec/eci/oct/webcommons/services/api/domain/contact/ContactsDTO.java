package eu.europa.ec.eci.oct.webcommons.services.api.domain.contact;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class ContactsDTO implements Serializable{
	
	private static final long serialVersionUID = 687537696065412610L;
	
	public ContactsDTO(){}
	
	private List<ContactDTO> contactDTOlist;

	public List<ContactDTO> getContactDTOlist() {
		return contactDTOlist;
	}

	public void setContactDTOlist(List<ContactDTO> contactDTOlist) {
		this.contactDTOlist = contactDTOlist;
	}

	@Override
	public String toString() {
		return "ContactsDTO [contactDTOlist=" + contactDTOlist + "]";
	}

}
