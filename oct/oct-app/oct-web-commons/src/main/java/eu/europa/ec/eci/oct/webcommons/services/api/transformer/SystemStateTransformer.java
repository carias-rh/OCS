package eu.europa.ec.eci.oct.webcommons.services.api.transformer;

import org.springframework.stereotype.Component;

import eu.europa.ec.eci.oct.entities.admin.SystemPreferences;
import eu.europa.ec.eci.oct.entities.admin.SystemState;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.systemManager.SystemStateDTO;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;

@Component
public class SystemStateTransformer extends BaseTransformer {

	public SystemStateDTO transform(SystemPreferences systemPreferences) throws OCTException {
		SystemStateDTO systemStateDTO = new SystemStateDTO();

		systemStateDTO.setCollecting(systemPreferences.isCollecting());
		systemStateDTO.setOnline(systemPreferences.getState().equals(SystemState.OPERATIONAL));
		boolean isInitialized = false;
		if (initiativeService.getDefaultDescription() != null) {
			isInitialized = true;
		}
		systemStateDTO.setInitialized(isInitialized);

		return systemStateDTO;

	}

}