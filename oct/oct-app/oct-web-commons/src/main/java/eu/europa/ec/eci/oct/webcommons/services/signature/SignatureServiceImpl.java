package eu.europa.ec.eci.oct.webcommons.services.signature;

import static eu.europa.ec.eci.oct.webcommons.services.api.domain.ApiResponse.buildError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.NoResultException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.eci.oct.entities.CountryProperty;
import eu.europa.ec.eci.oct.entities.PropertyGroup;
import eu.europa.ec.eci.oct.entities.admin.SystemState;
import eu.europa.ec.eci.oct.entities.export.ExportCount;
import eu.europa.ec.eci.oct.entities.export.ExportCountPerCountry;
import eu.europa.ec.eci.oct.entities.signature.IdentityValue;
import eu.europa.ec.eci.oct.entities.signature.Signature;
import eu.europa.ec.eci.oct.entities.system.Country;
import eu.europa.ec.eci.oct.entities.views.FastSignatureCount;
import eu.europa.ec.eci.oct.export.utils.ExportParameter;
import eu.europa.ec.eci.oct.utils.DateUtils;
import eu.europa.ec.eci.oct.validation.ValidationBean;
import eu.europa.ec.eci.oct.validation.ValidationError;
import eu.europa.ec.eci.oct.validation.ValidationProperty;
import eu.europa.ec.eci.oct.validation.ValidationResult;
import eu.europa.ec.eci.oct.webcommons.services.BaseService;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.ApiResponse;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.signature.SignatureDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.signature.SignatureValidation;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.supportForm.SupportFormDTO;
import eu.europa.ec.eci.oct.webcommons.services.crypto.CryptographyService;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTDuplicateSignatureException;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTobjectNotFoundException;
import eu.europa.ec.eci.oct.webcommons.services.persistence.PersistenceException;

@Service
@Transactional
public class SignatureServiceImpl extends BaseService implements SignatureService {

	@Override
	@Transactional(readOnly = true)
	public List<PropertyGroup> getPropertyGroups() throws OCTException {
		logger.debug("Getting property groups");

		try {
			List<PropertyGroup> result = propertyDAO.getGroups();
			logger.debug("Returning list of " + result.size() + " groups");

			return result;
		} catch (PersistenceException e) {
			logger.error("There was a problem while obtaining property groups. Message: " + e.getLocalizedMessage(), e);
			throw new OCTException("Getting property groups failed", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<CountryProperty> getProperties(Country c, PropertyGroup group) throws OCTException {
		if (logger.isInfoEnabled()) {
			logger.debug("Getting properties for group " + group.toString());
		}

		try {
			List<CountryProperty> result = propertyDAO.getProperties(c, group);
			if (logger.isDebugEnabled()) {
				logger.debug("Properties for " + group.toString() + " contain " + (result == null ? "0" : result.size())
						+ " elements.");
			}

			return result;
		} catch (PersistenceException e) {
			logger.error("There was a problem while obtaining property groups. Message: " + e.getLocalizedMessage(), e);
			throw new OCTException("Getting property groups for " + group.toString() + " failed.", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public CountryProperty getCountryPropertyById(Long id) throws OCTException {
		try {
			CountryProperty result = propertyDAO.getCountryPropertyById(id);
			return result;
		} catch (PersistenceException e) {
			logger.error("There was a roblem while retrieving the country property for id " + id + ". Message: "
					+ e.getLocalizedMessage(), e);
			throw new OCTException("Getting country property for id " + id + " failed.", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = Exception.class)
	public String insertSignature(Signature signature) throws OCTException, OCTDuplicateSignatureException {
		return insertSignature(signature, new Date());
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = Exception.class)
	public String insertSignature(Signature signature, Date dateOfSignature)
			throws OCTException, OCTDuplicateSignatureException {

		signature.setDateOfSignature(dateOfSignature);
		// for the msec column we don't want time, just midnight of the day
		long dateMsecAtMidnight = DateUtils.getMillisecOfMidnightFromADate(dateOfSignature);
		signature.setDateOfSignatureMsec(dateMsecAtMidnight);
		boolean isSignatureAlreadyInDb = isSignatureAlreadyPresent(signature.getFingerprint());

		if (!isSignatureAlreadyInDb) {
			logger.debug("Signature does not exist in DB. It will be persisted....");
			signature.setUuid(UUID.randomUUID().toString());
			String persistedSignatureUUID = "";
			try {
				persistedSignatureUUID = signatureDAO.insertSignature(signature);
			} catch (PersistenceException e) {
				logger.error("There was a problem persisting the signature: " + e.getLocalizedMessage(), e);
				throw new OCTException("There was a problem persisting the signature: ", e);
			}
			return persistedSignatureUUID;
		} else {
			logger.warn("Duplicate signature detected.");
			throw new OCTDuplicateSignatureException("Duplicate signature detected.");
		}

	}

	private boolean isSignatureAlreadyPresent(String fingerprint) throws OCTException {
		Signature findByFingerptint = findByFingerptint(fingerprint);
		return findByFingerptint != null;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = Exception.class)
	public void deleteSignature(Signature signature) throws OCTException {
		try {
			signatureDAO.deleteSignature(signature);
		} catch (PersistenceException e) {
			logger.error("There was a problem while deleting signature. The message was: " + e.getMessage(), e);
			throw new OCTException("There was a problem while deleting signature.", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Signature findByUuid(String uuid) throws OCTException, OCTobjectNotFoundException {
		try {
			return signatureDAO.findByUuid(uuid);
		} catch (PersistenceException pe) {
			logger.error("There was a problem while locating signature. The message was: " + pe.getLocalizedMessage(),
					pe);
			throw new OCTException("There was a problem while locating signature.", pe);
		} catch (OCTobjectNotFoundException nre) {
			logger.warn("No signature found for UUID: " + uuid);
			throw new OCTobjectNotFoundException("No signature found for UUID: " + uuid);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Signature findByFingerptint(String fingerprint) throws OCTException, NoResultException {
		try {
			return signatureDAO.findByFingerprint(fingerprint);
		} catch (PersistenceException pe) {
			logger.error("There was a problem while locating signature. The message was: " + pe.getLocalizedMessage(),
					pe);
			throw new OCTException("There was a problem while locating signature.", pe);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<CountryProperty> getCountryPropertiesByCountryCode(String countryCode) throws OCTException {
		logger.debug("Getting country properties...");

		try {
			List<CountryProperty> result = propertyDAO.getCountryPropertiesByCountryCode(countryCode);
			if (logger.isDebugEnabled()) {
				logger.debug(
						"Country properties table contains " + (result == null ? "0" : result.size()) + " elements.");
			}

			return result;
		} catch (PersistenceException e) {
			logger.error(
					"There was a problem while obtaining all country properties. Message: " + e.getLocalizedMessage(),
					e);
			throw new OCTException("Getting all properties failed.", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<CountryProperty> getCountryPropertiesByCountryCodes(List<String> countryCodes) throws OCTException {
		logger.debug("Getting country properties...");

		try {
			List<CountryProperty> result = propertyDAO.getCountryPropertiesByCountryCodes(countryCodes);
			if (logger.isDebugEnabled()) {
				logger.debug(
						"Country properties table contains " + (result == null ? "0" : result.size()) + " elements.");
			}

			return result;
		} catch (PersistenceException e) {
			logger.error(
					"There was a problem while obtaining all country properties. Message: " + e.getLocalizedMessage(),
					e);
			throw new OCTException("Getting all properties failed.", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<CountryProperty> getAllCountryProperties() throws OCTException {
		logger.debug("Getting country properties...");

		try {
			List<CountryProperty> result = propertyDAO.getAllCountryProperties();
			if (logger.isDebugEnabled()) {
				logger.debug(
						"Country properties table contains " + (result == null ? "0" : result.size()) + " elements.");
			}

			return result;
		} catch (PersistenceException e) {
			logger.error(
					"There was a problem while obtaining all country properties. Message: " + e.getLocalizedMessage(),
					e);
			throw new OCTException("Getting all properties failed.", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = OCTException.class)
	public void deleteAllSignatures() throws OCTException {
		SystemState systemState = systemManager.getSystemPreferences().getState();
		if (systemState.equals(SystemState.OPERATIONAL)) {
			throw new OCTException("Delete all signatures is not available in ONLINE mode");
		}
		try {
			signatureDAO.deleteAllSignatures();
		} catch (PersistenceException e) {
			logger.error(
					"There was a problem while deleting all signatures. The message was: " + e.getLocalizedMessage(),
					e);
			throw new OCTException("There was a problem while deleting all signatures.", e);
		}

	}

	/**
	 * TO BE USED ONLY FOR TESTING. WITH MORE THAN 100k OF SIGNATURES USE THE
	 * PAGINATED API
	 * 
	 * @throws Exception
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Signature> getAllSignatures() throws OCTException {
		return getAllSignatures(0, 0);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Signature> getAllSignatures(int start, int offset) throws OCTException {
		List<Signature> result = new ArrayList<>();
		try {
			if (offset > 0) {
				logger.debug("Getting all signatures [start=" + start + "/offset=" + offset + "]");
				result = signatureDAO.getAllSignatures(start, offset);
			} else {
				logger.debug("Getting all signatures...");
				result = signatureDAO.getAllSignatures();
			}
		} catch (PersistenceException e) {
			logger.error("There was a problem while obtaining all signatures. Message: " + e.getLocalizedMessage(), e);
			throw new OCTException("Getting all signatures failed.", e);
		}
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Signature> getSignaturesByCountries(List<String> countryCodes, int start, int offset)
			throws OCTException {
		List<Signature> signaturesByCountries = new ArrayList<Signature>();
		for (String countryCode : countryCodes) {
			List<Signature> signaturesByCountryCode = null;
			try {
				signaturesByCountryCode = signatureDAO.getSignaturesByCountryCode(countryCode, start, offset);
			} catch (PersistenceException pe) {
				throw new OCTException("Error while retrieving signatures by countryCode[" + countryCode + "]");
			}
			signaturesByCountries.addAll(signaturesByCountryCode);
		}
		return signaturesByCountries;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Signature> getSignaturesByDate(Date startDate, Date endDate, int start, int offset)
			throws OCTException {
		List<Signature> signaturesByDate = null;
		try {
			signaturesByDate = signatureDAO.getSignaturesByDate(startDate, endDate, start, offset);
		} catch (PersistenceException pe) {
			throw new OCTException(
					"Error while retrieving signatures by date[start=" + startDate + " / end=" + endDate + "]");
		}
		return signaturesByDate;
	}

	@Transactional(readOnly = true)
	private List<Signature> getSignaturesByCountryAndDate(ExportParameter exportParameter, int start, int offset)
			throws OCTException {
		List<Signature> signaturesByCountryAndDate = null;
		Date startDate = exportParameter.getStartDate();
		Date endDate = exportParameter.getEndDate();
		List<String> countryCodes = exportParameter.getCountries();
		try {
			signaturesByCountryAndDate = signatureDAO.getSignaturesByCountryAndDate(countryCodes, startDate, endDate,
					start, offset);
		} catch (PersistenceException pe) {
			throw new OCTException("Error while retrieving signatures by date[start=" + startDate + " / end=" + endDate
					+ "] and countries[" + countryCodes + "]");
		}
		return signaturesByCountryAndDate;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Signature> getLastSignatures() throws OCTException {
		try {
			return signatureDAO.getLastSignatures();
		} catch (PersistenceException pe) {
			throw new OCTException("getLastSignatures failed", pe);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public long getSignaturesCountForExport(ExportParameter exportParameter) throws OCTException {
		try {
			return signatureDAO.countSignatures(exportParameter);
		} catch (PersistenceException e) {
			logger.error("There was a problem retrieving signature counts for countries. The message was: "
					+ e.getLocalizedMessage(), e);
			throw new OCTException("There was a problem retrieving signature counts for countries.", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<Signature> getSignaturesForExport(ExportParameter exportParameter, int start, int offset)
			throws OCTException {
		List<Signature> signaturesForExport = new ArrayList<Signature>();
		try {
			signaturesForExport = getSignaturesByCountryAndDate(exportParameter, start, offset);
		} catch (Exception e) {
			throw new OCTException("Error while retrieving signatures for export: " + e.getMessage());
		}
		return signaturesForExport;
	}

	@Override
	@Transactional(readOnly = true)
	public ExportCount getExportCount(ExportParameter exportParameter) throws OCTException {

		long total = 0l;
		List<ExportCountPerCountry> exportCountPerCountryList = signatureService
				.getExportCountPerCountry(exportParameter);
		for (ExportCountPerCountry exportCountPerCountry : exportCountPerCountryList) {
			total += exportCountPerCountry.getCount();
		}
		ExportCount exportCount = new ExportCount();
		exportCount.setExportCountPerCountry(exportCountPerCountryList);
		exportCount.setTotal(total);

		return exportCount;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ExportCountPerCountry> getExportCountPerCountry(ExportParameter exportParameter) throws OCTException {

		List<ExportCountPerCountry> result = new ArrayList<ExportCountPerCountry>();
		try {
			result = signatureDAO.getExportCountPerCountry(exportParameter);
		} catch (PersistenceException pe) {
			logger.error("There was a problem retrieving export count per country. The message was: "
					+ pe.getLocalizedMessage(), pe);
			throw new OCTException("There was a problem retrievingexport count per country.", pe);
		}
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public Long getFastSignatureCountTotal() throws OCTException {
		Long totCount = 0L;
		try {
			totCount = signatureDAO.getFastSignatureCountTotal();
		} catch (PersistenceException e) {
			logger.error(
					"There was a problem retrieving signature fast count. The message was: " + e.getLocalizedMessage(),
					e);
			throw new OCTException("There was a problem retrieving signature fast count.", e);
		}
		return totCount;
	}

	@Override
	@Transactional(readOnly = true)
	public Long getFastSignatureCountByCountryId(Long countryId) throws OCTException {
		try {
			Long signatureCountByCountryId = signatureDAO.getSignatureCountByCountryId(countryId);
			return signatureCountByCountryId != null ? signatureCountByCountryId : 0l;
		} catch (PersistenceException e) {
			logger.error("There was a problem retrieving signature fast count by countryId [" + countryId
					+ "]. The message was: " + e.getLocalizedMessage(), e);
			throw new OCTException(
					"There was a problem retrieving signature fast count by countryId [" + countryId + "].", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Long getFastSignatureCountByCountryCode(String countryCode) throws OCTException {
		try {
			Country c = countryDAO.getCountryByCode(countryCode);
			return getFastSignatureCountByCountryId(c.getId());
		} catch (PersistenceException e) {
			logger.error("There was a problem retrieving signature fast count by countryCode [" + countryCode
					+ "]. The message was: " + e.getLocalizedMessage(), e);
			throw new OCTException(
					"There was a problem retrieving signature fast count by countryCode [" + countryCode + "].", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<FastSignatureCount> getFastSignatureCounts(int requiredSize) throws OCTException {
		List<FastSignatureCount> fastSignatureCounts = new ArrayList<FastSignatureCount>();
		try {
			fastSignatureCounts = signatureDAO.getFastSignatureCounts();
		} catch (PersistenceException e) {
			logger.error(
					"There was a problem retrieving signature fast count . The message was: " + e.getLocalizedMessage(),
					e);
			throw new OCTException("There was a problem retrieving signature fast count.", e);
		}

		Collections.sort(fastSignatureCounts);

		int supportingCountriesSize = fastSignatureCounts.size();

		if (supportingCountriesSize < requiredSize) {
			/*
			 * if supporting countries are not yet enough we need to add countries to fill
			 * the required size
			 */
			int missingCountriesSize = requiredSize - supportingCountriesSize;
			List<Country> allCountries = systemManager.getAllCountries();
			List<Long> countryIds = new ArrayList<Long>();
			for (FastSignatureCount fastSignatureCount : fastSignatureCounts) {
				countryIds.add(fastSignatureCount.getCountryId());
			}

			for (Country c : allCountries) {
				if (missingCountriesSize == 0) {
					break;
				} else {
					Long countryId = c.getId();
					if (!countryIds.contains(countryId)) {
						FastSignatureCount fsc = new FastSignatureCount();
						fsc.setCount(0L);
						fsc.setCountryId(countryId);
						fsc.setThreshold(c.getThreshold());
						fsc.setCountryCode(c.getCode());
						fastSignatureCounts.add(fsc);
						missingCountriesSize--;
					}
				}
			}
		}
		return fastSignatureCounts.subList(0, requiredSize);
	}

	@Override
	@Transactional(readOnly = true)
	public IdentityValue findIdentityDocumentDuplicate(String propertyLabel, String newIdentityValue,
			String countryCode) throws OCTException, OCTDuplicateSignatureException {
		String encryptedNewIdentityValue = fingerprintIdentityValue(newIdentityValue);
		Long propertyId;
		try {
			propertyId = propertyDAO.getPropertyByLabel(propertyLabel).getId();
		} catch (PersistenceException e) {
			logger.error("There was a problem retrieving property by label [" + propertyLabel + "]. The message was: "
					+ e.getLocalizedMessage(), e);
			throw new OCTException("There was a problem retrieving property by label [" + propertyLabel + "].", e);
		}

		String duplicate = "";
		try {
			duplicate = signatureDAO.findDuplicateIdentityValue(countryCode, propertyId, encryptedNewIdentityValue);
		} catch (PersistenceException e) {
			logger.error("There was a problem retrieving identityValue by string [" + newIdentityValue
					+ "]. The message was: " + e.getLocalizedMessage(), e);
			throw new OCTException("There was a problem retrieving identityValue by string [" + newIdentityValue + "].",
					e);
		}
		if (StringUtils.isNotBlank(duplicate)) {
			throw new OCTDuplicateSignatureException(
					"Find duplicate signature for given identity: " + propertyLabel + "/" + newIdentityValue);
		}

		IdentityValue identityValue = new IdentityValue();
		identityValue.setPropertyId(propertyId);
		identityValue.setCountryCode(countryCode);
		identityValue.setIdentityValue(encryptedNewIdentityValue);
		return identityValue;
	}

	private String fingerprintIdentityValue(String newIdentityValue) throws OCTException {
		String encryptedNewIdentityValue;
		char[] publicKey = systemManager.getSystemPreferences().getPublicKey().toCharArray();
		final CryptographyService crypto = CryptographyService.getService(publicKey);

		try {
			encryptedNewIdentityValue = new String(Hex.encodeHex(crypto.fingerprint(newIdentityValue)));

		} catch (Exception e1) {
			throw new OCTException("There was a problem retrieving encrypted identity value: ", e1);
		}
		return encryptedNewIdentityValue;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = Exception.class)
	public void storeIdentityValue(IdentityValue identityValue) throws OCTException {

		try {
			signatureDAO.persistIdentityValue(identityValue);
		} catch (PersistenceException e) {
			logger.error("There was a problem persisting identityValue [" + identityValue + "]. The message was: "
					+ e.getLocalizedMessage(), e);
			throw new OCTException("There was a problem persisting identityValue [" + identityValue + "].", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<IdentityValue> getAllIdentityValues() throws OCTException {
		List<IdentityValue> allIdentityValues = new ArrayList<IdentityValue>();
		try {
			allIdentityValues = signatureDAO.getAllIdentityValues();
		} catch (PersistenceException e) {
			logger.error("There was a problem getting all identityValues. The message was: " + e.getLocalizedMessage(),
					e);
			throw new OCTException("There was a problem getting all identityValues.", e);
		}
		return allIdentityValues;
	}

	/**
	 * Validation again the content of SignatureDTO. If the validation fails, a
	 * Response will be returned, null if all is ok.
	 *
	 * @param signatureDTO
	 * @param signatureValidation
	 * @return Response in case of errors, null if all is ok.
	 */
	@Override
	public Response validateSignature(SignatureDTO signatureDTO, SignatureValidation signatureValidation) {
		ValidationBean validationBean = new ValidationBean();
		String country = signatureDTO.getCountry();
		validationBean.setNationality(country);

		for (SupportFormDTO supportForm : signatureDTO.getProperties()) {
			ValidationProperty validationProperty = new ValidationProperty();
			validationProperty.setKey(supportForm.getLabel());
			validationProperty.setValue(supportForm.getValue());
			validationBean.addProperty(validationProperty);
		}

		ApiResponse apiResponse;
		try {
			ValidationResult validationResult = ruleService.validate(validationBean);
			if (!validationResult.getValidationErrors().isEmpty()) {
				// we have errors on validation
				for (ValidationError validationError : validationResult.getValidationErrors()) {
					signatureValidation.addErrorField(validationError.getKey(), validationError.getErrorKey(),
							validationError.isSkippable());
					logger.debug(validationError.toString());
				}

				if (!validationResult.isValidationSkippable() || !signatureDTO.isOptionalValidation()) {
					apiResponse = buildError(Status.EXPECTATION_FAILED.getStatusCode(),
							"One or more form field values are not correct.");
					return Response.status(Status.EXPECTATION_FAILED).entity(signatureValidation).build();
				}
			}
		} catch (OCTException e) {
			logger.error(e.getMessage());
			apiResponse = buildError(Status.EXPECTATION_FAILED.getStatusCode(),
					" Validation of the signature error. " + e.getMessage());
			return Response.status(Status.EXPECTATION_FAILED).entity(apiResponse).build();
		}
		// all is ok
		return null;
	}
}
