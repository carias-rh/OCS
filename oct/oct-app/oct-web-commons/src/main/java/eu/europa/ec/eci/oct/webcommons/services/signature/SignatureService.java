package eu.europa.ec.eci.oct.webcommons.services.signature;

import java.util.Date;
import java.util.List;

import javax.ws.rs.core.Response;

import eu.europa.ec.eci.oct.entities.CountryProperty;
import eu.europa.ec.eci.oct.entities.PropertyGroup;
import eu.europa.ec.eci.oct.entities.export.ExportCount;
import eu.europa.ec.eci.oct.entities.export.ExportCountPerCountry;
import eu.europa.ec.eci.oct.entities.signature.IdentityValue;
import eu.europa.ec.eci.oct.entities.signature.Signature;
import eu.europa.ec.eci.oct.entities.system.Country;
import eu.europa.ec.eci.oct.entities.views.FastSignatureCount;
import eu.europa.ec.eci.oct.export.utils.ExportParameter;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.signature.SignatureDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.signature.SignatureValidation;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTDuplicateSignatureException;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTParameterException;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTobjectNotFoundException;

public interface SignatureService {

	public String insertSignature(Signature signature, Date dateOfSignature) throws OCTException, OCTDuplicateSignatureException;

	public String insertSignature(Signature signature) throws OCTException, OCTDuplicateSignatureException;

	public List<PropertyGroup> getPropertyGroups() throws OCTException;

	public List<CountryProperty> getProperties(Country country, PropertyGroup group) throws OCTException;

	public List<CountryProperty> getAllCountryProperties() throws OCTException;

	public CountryProperty getCountryPropertyById(Long id) throws OCTException;

	public Signature findByUuid(String uuid) throws OCTException, OCTobjectNotFoundException;

	public Signature findByFingerptint(String fingerprint) throws OCTException;

	public void deleteSignature(Signature signature) throws OCTException;

	public void deleteAllSignatures() throws OCTException;

	public List<Signature> getAllSignatures() throws OCTException;

	public List<Signature> getAllSignatures(int start, int offset) throws OCTException;

	public List<CountryProperty> getCountryPropertiesByCountryCode(String countryCode) throws OCTException;

	List<Signature> getLastSignatures() throws OCTException, OCTParameterException;

	public List<Signature> getSignaturesByCountries(List<String> countries, int start, int offset) throws OCTException;

	public List<Signature> getSignaturesByDate(Date startDate, Date endDate, int start, int offset) throws OCTException;

	public List<Signature> getSignaturesForExport(ExportParameter exportParameter, int start, int offset) throws OCTException;

	public long getSignaturesCountForExport(ExportParameter exportParameter) throws OCTException;

	public List<CountryProperty> getCountryPropertiesByCountryCodes(List<String> countryCodes) throws OCTException;

	public Long getFastSignatureCountTotal() throws OCTException;

	public Long getFastSignatureCountByCountryId(Long countryId) throws OCTException;

	public Long getFastSignatureCountByCountryCode(String countryCode) throws OCTException;

	public List<FastSignatureCount> getFastSignatureCounts(int requiredSize) throws OCTException;

	public List<ExportCountPerCountry> getExportCountPerCountry(ExportParameter epb) throws OCTException;

	ExportCount getExportCount(ExportParameter exportParameter) throws OCTException;

	public IdentityValue findIdentityDocumentDuplicate(String label, String value, String countryCode) throws OCTException, OCTDuplicateSignatureException;
	
	public List<IdentityValue> getAllIdentityValues() throws OCTException, OCTDuplicateSignatureException;

	public void storeIdentityValue(IdentityValue identityValue)throws OCTException;

	Response validateSignature(SignatureDTO signatureDTO, SignatureValidation signatureValidation);

}
