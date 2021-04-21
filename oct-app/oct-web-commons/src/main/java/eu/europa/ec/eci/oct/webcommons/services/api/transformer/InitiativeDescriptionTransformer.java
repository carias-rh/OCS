package eu.europa.ec.eci.oct.webcommons.services.api.transformer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.europa.ec.eci.oct.entities.admin.Contact;
import eu.europa.ec.eci.oct.entities.admin.InitiativeDescription;
import eu.europa.ec.eci.oct.entities.admin.SystemPreferences;
import eu.europa.ec.eci.oct.entities.system.Language;
import eu.europa.ec.eci.oct.utils.DateUtils;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.initiative.InitiativeDescriptionDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.initiative.InitiativeDescriptionDTOext;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.initiative.InitiativeDescriptionsDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.initiative.InitiativeInfoDTO;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;

@Component
public class InitiativeDescriptionTransformer extends BaseTransformer {

	@Autowired
	private ContactTransformer contactTransformer;

	public InitiativeDescriptionsDTO transform(List<InitiativeDescription> initiativeDescriptionsDAO)
			throws OCTException {

		if (initiativeDescriptionsDAO == null) {
			return null;
		}

		InitiativeDescriptionsDTO InitiativeDescriptionsDTO = new InitiativeDescriptionsDTO();
		InitiativeDescriptionsDTO.setInitiativeInfo(getInitiativeInfo());
		for (InitiativeDescription initiativeDescriptionDAO : initiativeDescriptionsDAO) {
			InitiativeDescriptionDTO initiativeDescriptionDTO = null;
			initiativeDescriptionDTO = transform(initiativeDescriptionDAO);
			InitiativeDescriptionsDTO.addDescription(initiativeDescriptionDTO);
		}

		return InitiativeDescriptionsDTO;
	}

	public InitiativeDescriptionDTOext transformExt(InitiativeDescription initiativeDescriptionDAO)
			throws OCTException {

		if (initiativeDescriptionDAO == null) {
			return null;
		}

		// original data
		InitiativeDescriptionDTO initiativeDescriptionDTO = transform(initiativeDescriptionDAO);

		InitiativeDescriptionDTOext initiativeDescriptionDTOext = new InitiativeDescriptionDTOext();
		initiativeDescriptionDTOext.setInitiativeDescription(initiativeDescriptionDTO);
		initiativeDescriptionDTOext.setInitiativeInfo(getInitiativeInfo());

		return initiativeDescriptionDTOext;
	}

	public InitiativeDescriptionDTO transform(InitiativeDescription initiativeDescriptionDAO) {
		InitiativeDescriptionDTO initiativeDescriptionDTO = new InitiativeDescriptionDTO();
		initiativeDescriptionDTO.setId(initiativeDescriptionDAO.getId());
		initiativeDescriptionDTO.setLanguageCode(initiativeDescriptionDAO.getLanguage().getCode());
		initiativeDescriptionDTO.setObjectives(initiativeDescriptionDAO.getObjectives());
		initiativeDescriptionDTO.setTitle(initiativeDescriptionDAO.getTitle());
		initiativeDescriptionDTO.setUrl(initiativeDescriptionDAO.getUrl());
		initiativeDescriptionDTO.setOriginal(initiativeDescriptionDAO.getIsDefault() == InitiativeDescription.IS_DEFAULT);
		initiativeDescriptionDTO.setPartialRegistration(initiativeDescriptionDAO.getPartialRegistration());
		return initiativeDescriptionDTO;
	}

	public InitiativeInfoDTO getInitiativeInfo() throws OCTException {
		SystemPreferences systemPreferences = null;
		try {
			systemPreferences = systemManager.getSystemPreferences();
		} catch (OCTException e) {
			logger.error(e.getMessage());
			throw new OCTException(e.getMessage());
		}
		List<Contact> contacts = null;
		try {
			contacts = contactService.getAllContacts();
		} catch (OCTException e) {
			logger.error(e.getMessage());
			throw new OCTException(e.getMessage());
		}

		InitiativeInfoDTO initiativeInfo = new InitiativeInfoDTO();
		initiativeInfo.setOrganizers(contactTransformer.transformListDAO(contacts));
		initiativeInfo.setRegistrationNumber(systemPreferences.getRegistrationNumber());
		initiativeInfo.setRegistrationDate(DateUtils.formatDate(systemPreferences.getRegistrationDate(), "dd/MM/yyyy"));
		initiativeInfo.setUrl(systemPreferences.getCommissionRegisterUrl());
		initiativeInfo.setClosingDate(DateUtils.formatDate(systemPreferences.getDeadline(), "dd/MM/yyyy"));
		initiativeInfo.setPartiallyRegistered(systemPreferences.isPartiallyRegistered());

		return initiativeInfo;
	}

	public InitiativeDescription transform(InitiativeDescriptionDTO initiativeDescriptionDTO) throws OCTException {
		InitiativeDescription initiativeDescriptionDAO = new InitiativeDescription();

		String languageCode = initiativeDescriptionDTO.getLanguageCode();
		Language language = systemManager.getLanguageByCode(languageCode);
		initiativeDescriptionDAO.setLanguage(language);
		initiativeDescriptionDAO.setObjectives(initiativeDescriptionDTO.getObjectives());
		initiativeDescriptionDAO.setTitle(initiativeDescriptionDTO.getTitle());
		initiativeDescriptionDAO.setUrl(initiativeDescriptionDTO.getUrl());
		initiativeDescriptionDAO.setIsDefault(initiativeDescriptionDTO.isOriginal() ? InitiativeDescription.IS_DEFAULT : 0);
		initiativeDescriptionDAO.setPartialRegistration(initiativeDescriptionDTO.getPartialRegistration());
		return initiativeDescriptionDAO;
	}

	public List<InitiativeDescription> transformList(List<InitiativeDescriptionDTO> initiativeDescriptionDTOlist)
			throws OCTException {
		List<InitiativeDescription> initiativeDescriptionsDAO = new ArrayList<InitiativeDescription>();
		if (initiativeDescriptionDTOlist == null || initiativeDescriptionDTOlist.isEmpty()) {
			return initiativeDescriptionsDAO;
		}
		for (InitiativeDescriptionDTO iDTO : initiativeDescriptionDTOlist) {
			initiativeDescriptionsDAO.add(transform(iDTO));
		}
		return initiativeDescriptionsDAO;
	}
}
