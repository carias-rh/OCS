package eu.europa.ec.eci.oct.webcommons.services.persistence;

import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;

import eu.europa.ec.eci.oct.entities.export.ExportCountPerCountry;
import eu.europa.ec.eci.oct.entities.signature.IdentityValue;
import eu.europa.ec.eci.oct.entities.signature.Signature;
import eu.europa.ec.eci.oct.entities.views.FastSignatureCount;
import eu.europa.ec.eci.oct.export.utils.ExportParameter;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTobjectNotFoundException;

public interface SignatureDAO {

	public List<Signature> getAllSignatures() throws PersistenceException;

	public List<Signature> getAllSignatures(int start, int offset) throws PersistenceException;

	List<Signature> getLastSignatures() throws PersistenceException;

	public String insertSignature(Signature s) throws PersistenceException;

	public Signature findByUuid(String uuid) throws PersistenceException, OCTobjectNotFoundException;

	public Signature findByFingerprint(String fingerprint) throws PersistenceException;

	public void deleteSignature(Signature signature) throws PersistenceException;

	public void deleteAllSignatures() throws PersistenceException;

	public List<Signature> getSignaturesByCountryCode(String countryCode, int start, int offset)
			throws PersistenceException;

	public List<Signature> getSignaturesByDate(Date startDate, Date endDate, int start, int offset)
			throws PersistenceException;

	public List<Signature> getSignaturesByCountryAndDate(List<String> countryCodes, Date startDate, Date endDate,
			int start, int offset) throws PersistenceException;

	public long countSignatures(ExportParameter exportParameter) throws PersistenceException;

	List<FastSignatureCount> getAllFastSignatureCounts() throws PersistenceException;

	Long getSignatureCountByCountryId(Long countryId) throws PersistenceException;

	public void updateSignature(Signature testSignature) throws PersistenceException;

	public List<FastSignatureCount> getFastSignatureCounts() throws PersistenceException;

	public Long getFastSignatureCountTotal() throws PersistenceException;

	List<ExportCountPerCountry> getExportCountPerCountry(ExportParameter exportParameter) throws PersistenceException;

	public String findDuplicateIdentityValue(String countryCode, Long propertyId, String encryptedNewIdentityValue)
			throws NoResultException, PersistenceException;

	public void persistIdentityValue(IdentityValue identityValue) throws PersistenceException;

	public List<IdentityValue> getAllIdentityValues() throws PersistenceException;


}
