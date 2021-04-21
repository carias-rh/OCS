package eu.europa.ec.eci.oct.webcommons.services.api.transformer;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import eu.europa.ec.eci.oct.entities.export.ExportCount;
import eu.europa.ec.eci.oct.entities.export.ExportCountPerCountry;
import eu.europa.ec.eci.oct.entities.export.ExportHistory;
import eu.europa.ec.eci.oct.export.utils.ExportParameter;
import eu.europa.ec.eci.oct.utils.CommonsConstants;
import eu.europa.ec.eci.oct.utils.DateUtils;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.export.ExportHistoryDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.export.ExportParameterDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.export.ExportSignaturesCountDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.export.ExportSignaturesCountDetailDTO;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;

@Component
public class ExportTransformer extends BaseTransformer {

	public ExportParameter transform(ExportParameterDTO exportParameterDTO) throws ParseException {

		if (exportParameterDTO == null) {
			return null;
		}

		ExportParameter exportParameter = new ExportParameter();

		boolean dateParameter = StringUtils.isNotBlank(exportParameterDTO.getEndDate()) && StringUtils.isNotBlank(exportParameterDTO.getStartDate());

		if (dateParameter) {
			Date startDate = DateUtils.parseDate(exportParameterDTO.getStartDate());
			exportParameter.setStartDate(startDate);
			Date endDate = DateUtils.parseDate(exportParameterDTO.getEndDate());
			exportParameter.setEndDate(endDate);
		}
		List<String> countries = exportParameterDTO.getCountries();
		if (countries.size() != CommonsConstants.TOTAL_COUNTRIES_NUMBER) {
			// all countries -> no need to filter by country
			exportParameter.setCountries(countries);
		}
		return exportParameter;
	}

	/**
	 * @param exportParameter
	 * @return used for testing purpouse
	 * @throws OCTException
	 */
	public ExportParameterDTO transform(ExportParameter exportParameter) throws OCTException {
		ExportParameterDTO exportParameterDTO = new ExportParameterDTO();

		exportParameterDTO.setStartDate(DateUtils.formatDate(exportParameter.getStartDate()));
		exportParameterDTO.setEndDate(DateUtils.formatDate(exportParameter.getEndDate()));
		List<String> countries = exportParameter.getCountries();
		exportParameterDTO.setCountries(countries);

		return exportParameterDTO;
	}

	public ExportSignaturesCountDTO transform(ExportCount exportCount) {
		ExportSignaturesCountDTO escDTO = new ExportSignaturesCountDTO();
		escDTO.setTotal(exportCount.getTotal());
		List<ExportSignaturesCountDetailDTO> escdDTOlist = new ArrayList<ExportSignaturesCountDetailDTO>();
		for (ExportCountPerCountry ecpc : exportCount.getExportCountPerCountry()) {
			ExportSignaturesCountDetailDTO escdDTO = new ExportSignaturesCountDetailDTO();
			escdDTO.setCount(ecpc.getCount());
			escdDTO.setCountryCode(ecpc.getCountryCode());
			escdDTOlist.add(escdDTO);
		}
		escDTO.setList(escdDTOlist);

		return escDTO;
	}

	public List<ExportHistoryDTO> transformExportHistoryList(List<ExportHistory> exportHistoryList) {
		List<ExportHistoryDTO> exportHistoryDTOlist = new ArrayList<ExportHistoryDTO>();
		for (ExportHistory exportHistory : exportHistoryList) {
			exportHistoryDTOlist.add(transformExportHistory(exportHistory));
		}
		return exportHistoryDTOlist;
	}

	public ExportHistoryDTO transformExportHistory(ExportHistory exportHistory) {
		ExportHistoryDTO exportHistoryDTO = new ExportHistoryDTO();

		if (exportHistory == null) {
			return exportHistoryDTO;
		}
		exportHistoryDTO.setJobId(exportHistory.getJobId());
		exportHistoryDTO.setBatchStatus(exportHistory.getBatchStatus());

		exportHistoryDTO.setCountriesParam(exportHistory.getCountriesParam());

		String dateRangeParam = "";
		if (StringUtils.isNotBlank(exportHistory.getStartDateParam())) {
			dateRangeParam = exportHistory.getStartDateParam() + " - " + exportHistory.getEndDateParam();
		}
		exportHistoryDTO.setDateRangeParam(dateRangeParam);

		exportHistoryDTO.setDuration(exportHistory.getDuration());
		exportHistoryDTO.setExportDate(DateUtils.formatDate(exportHistory.getExportDate()));
		exportHistoryDTO.setExportProgress(exportHistory.getExportProgress());
		exportHistoryDTO.setExportSummary(exportHistory.getExportSummary());
		exportHistoryDTO.setValidationProgress(exportHistoryDTO.getValidationProgress());
		exportHistoryDTO.setValidationSummary(exportHistory.getValidationSummary());

		return exportHistoryDTO;
	}

}
