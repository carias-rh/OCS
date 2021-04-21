package eu.europa.ec.eci.oct.webcommons.services.api.domain.report;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class EvolutionMapDTO {
	
    private List<EvolutionMapEntryDTO> evolutionMapEntryDTOs;

    public List<EvolutionMapEntryDTO> getEvolutionMapEntryDTOs() {
        return evolutionMapEntryDTOs;
    }

    public void setEvolutionMapEntryDTOs(List<EvolutionMapEntryDTO> evolutionMapEntryDTOs) {
        this.evolutionMapEntryDTOs = evolutionMapEntryDTOs;
    }

	@Override
	public String toString() {
		return "EvolutionMapDTO [evolutionMapEntryDTOs=" + evolutionMapEntryDTOs + "]";
	}
    
}
