package eu.europa.ec.eci.oct.webcommons.services.api.transformer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import eu.europa.ec.eci.oct.entities.CountryProperty;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.supportForm.SupportFormDTO;

@Component
public class SupportFormTransformer extends BaseTransformer {

	public SupportFormDTO transform(CountryProperty countryPropertyDAO) {
		SupportFormDTO supportFormDTO = new SupportFormDTO();
		supportFormDTO.setId(countryPropertyDAO.getId());
		supportFormDTO.setPriority(countryPropertyDAO.getProperty().getPriority());
		supportFormDTO.setRequired(countryPropertyDAO.isRequired() ? 1 : 0);
		supportFormDTO.setType(countryPropertyDAO.getProperty().getType().name());
		supportFormDTO.setLabel(countryPropertyDAO.getProperty().getName().replace("oct.property.", ""));
		supportFormDTO.setGroup(countryPropertyDAO.getProperty().getGroup().getId().intValue());
		return supportFormDTO;
	}

	public List<SupportFormDTO> transform(List<CountryProperty> countryPropertiesDAO) {
		List<SupportFormDTO> supportFormDTOlist = new ArrayList<SupportFormDTO>();
		for (CountryProperty countryPropertyDAO : countryPropertiesDAO) {
			SupportFormDTO countryPropertyDTO = transform(countryPropertyDAO);
			supportFormDTOlist.add(countryPropertyDTO);
		}
		return supportFormDTOlist;
	}

}
