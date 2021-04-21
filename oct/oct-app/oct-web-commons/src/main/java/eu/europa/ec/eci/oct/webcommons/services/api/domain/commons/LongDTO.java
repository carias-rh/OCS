package eu.europa.ec.eci.oct.webcommons.services.api.domain.commons;

import org.springframework.stereotype.Component;

@Component
public class LongDTO {

	private long value;

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "LongDTO [value=" + value + "]";
	}

}
