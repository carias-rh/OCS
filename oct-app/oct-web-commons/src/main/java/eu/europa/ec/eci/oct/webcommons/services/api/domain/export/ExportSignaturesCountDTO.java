package eu.europa.ec.eci.oct.webcommons.services.api.domain.export;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class ExportSignaturesCountDTO implements Serializable {

	private static final long serialVersionUID = 4886529890142337233L;

	private long total = 0;

	List<ExportSignaturesCountDetailDTO> list = new ArrayList<ExportSignaturesCountDetailDTO>();

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public List<ExportSignaturesCountDetailDTO> getList() {
		return list;
	}

	public void setList(List<ExportSignaturesCountDetailDTO> list) {
		this.list = list;
	}
}
