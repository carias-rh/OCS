package eu.europa.ec.eci.oct.webcommons.services.persistence.jpa;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.query.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.eci.oct.entities.export.ExportCountPerCountry;
import eu.europa.ec.eci.oct.entities.signature.IdentityValue;
import eu.europa.ec.eci.oct.entities.signature.Signature;
import eu.europa.ec.eci.oct.entities.views.FastSignatureCount;
import eu.europa.ec.eci.oct.entities.views.LastSignatures;
import eu.europa.ec.eci.oct.export.utils.ExportParameter;
import eu.europa.ec.eci.oct.export.utils.ItemProcessorsUtils;
import eu.europa.ec.eci.oct.utils.CommonsConstants;
import eu.europa.ec.eci.oct.utils.DateUtils;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTobjectNotFoundException;
import eu.europa.ec.eci.oct.webcommons.services.persistence.PersistenceException;
import eu.europa.ec.eci.oct.webcommons.services.persistence.SignatureDAO;

@Repository
@Transactional
@SuppressWarnings("unchecked")
public class JpaSignatureDAO extends AbstractJpaDAO implements SignatureDAO {

	@Override
	@Transactional(readOnly = true)
	public List<Signature> getAllSignatures() throws PersistenceException {
		return getAllSignatures(0, 0);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Signature> getAllSignatures(int start, int offset) throws PersistenceException {
		try {

			Query query = this.sessionFactory.getCurrentSession().createQuery("FROM Signature s ORDER BY s.id");
			if (offset > 0) {
				query.setMaxResults(offset);
				query.setFirstResult(start);
			}

			List<Signature> allSignatures = query.list();
			return allSignatures;
		} catch (Exception e) {
			throw wrapException("getAllSignatures", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<Signature> getLastSignatures() throws PersistenceException {
		try {
			logger.debug("getLastSignatures");
			SQLQuery sqlQuery = this.sessionFactory.getCurrentSession()
					.createSQLQuery("SELECT * FROM LASTSIGNATURESVIEW");
			sqlQuery.addEntity(LastSignatures.class);
			List<LastSignatures> lastSignaturesId = sqlQuery.list();

			List<Signature> lastSignatures = new ArrayList<Signature>();
			for (LastSignatures lastSignature : lastSignaturesId) {
				Signature sig = getSignatureById(lastSignature.getSignatureId());
				lastSignatures.add(sig);
			}
			return lastSignatures;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw wrapException("getLastSignatures", e);
		}
	}

	@Transactional(readOnly = true)
	private Signature getSignatureById(Long signatureId) throws PersistenceException {
		try {
			Query getSignaturesByFingerprintQuery = this.sessionFactory.getCurrentSession()
					.createQuery("FROM Signature s WHERE s.id = :signatureId").setParameter("signatureId", signatureId);
			return (Signature) getSignaturesByFingerprintQuery.uniqueResult();
		} catch (HibernateException he) {
			throw wrapException("getSignatureById " + signatureId, he);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<Signature> getSignaturesByCountryCode(String countryCode, int start, int offset)
			throws PersistenceException {
		try {
			Query getSignaturesByCountryCodeQuery = this.sessionFactory.getCurrentSession()
					.createQuery("FROM Signature s WHERE s.countryToSignFor.code = :countryCode ORDER BY s.id")
					.setParameter("countryCode", countryCode);
			if (offset > 0) {
				getSignaturesByCountryCodeQuery.setFirstResult(start);
				getSignaturesByCountryCodeQuery.setMaxResults(offset);
			}
			List<Signature> signaturesByCountryCode = getSignaturesByCountryCodeQuery.list();

			return signaturesByCountryCode;
		} catch (Exception e) {
			throw wrapException("getSignaturesForCountryCode " + countryCode, e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<Signature> getSignaturesByDate(Date startDate, Date endDate, int start, int offset)
			throws PersistenceException {
		long startDateMsec = DateUtils.getMillisecOfMidnightFromADate(startDate);
		long endDateMsec = DateUtils.getMillisecOfMidnightFromADate(endDate);
		try {
			Query getSignaturesByDateQuery = this.sessionFactory.getCurrentSession().createQuery(
					"FROM Signature s WHERE s.dateOfSignatureMsec >= :startDateMsec AND s.dateOfSignatureMsec <= :endDateMsec ORDER BY s.id")
					.setParameter("startDateMsec", startDateMsec).setParameter("endDateMsec", endDateMsec);
			if (offset > 0) {
				getSignaturesByDateQuery.setFirstResult(start);
				getSignaturesByDateQuery.setMaxResults(offset);
			}
			List<Signature> signaturesByDate = getSignaturesByDateQuery.list();

			return signaturesByDate;
		} catch (Exception e) {
			throw wrapException("getSignaturesForDate " + "[startDate=" + startDate + " / endDate=" + endDate + "]", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<Signature> getSignaturesByCountryAndDate(List<String> countryCodes, Date startDate, Date endDate,
			int start, int offset) throws PersistenceException {
		long startDateMsec = DateUtils.getMillisecOfMidnightFromADate(startDate);
		long endDateMsec = DateUtils.getMillisecOfMidnightFromADate(endDate);
		try {
			Query getSignaturesByDateQuery = this.sessionFactory.getCurrentSession().createQuery(
					"FROM Signature s WHERE s.dateOfSignatureMsec >=  :startDateMsec AND s.dateOfSignatureMsec <= :endDateMsec AND s.countryToSignFor.code IN (:countryCodes) ORDER BY s.id")
					.setParameter("startDateMsec", startDateMsec).setParameter("endDateMsec", endDateMsec)
					.setParameterList("countryCodes", countryCodes);
			if (offset > 0) {
				getSignaturesByDateQuery.setFirstResult(start);
				getSignaturesByDateQuery.setMaxResults(offset);
			}
			List<Signature> signaturesByDate = getSignaturesByDateQuery.list();

			return signaturesByDate;
		} catch (Exception e) {
			throw wrapException("getSignaturesByCountryAndDate " + "[countryCodes=" + countryCodes + " / startDate="
					+ startDate + " / endDate=" + endDate + "]", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = HibernateException.class)
	public String insertSignature(Signature signature) throws PersistenceException {
		try {
			this.sessionFactory.getCurrentSession().save(signature);
			this.sessionFactory.getCurrentSession().flush();
			return signature.getUuid();
		} catch (HibernateException e) {
			throw wrapException("insertSignature " + signature, e);
		}
	}

	public Signature findByFingerprint(String fingerprint) throws PersistenceException {
		try {
			Query getSignaturesByFingerprintQuery = this.sessionFactory.getCurrentSession()
					.createQuery("FROM Signature s WHERE s.fingerprint = :fingerprint")
					.setParameter("fingerprint", fingerprint);
			return (Signature) getSignaturesByFingerprintQuery.uniqueResult();
		} catch (HibernateException he) {
			throw wrapException("findByFingerprint " + fingerprint, he);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = Exception.class)
	public void deleteSignature(Signature signature) throws PersistenceException {
		try {
			this.sessionFactory.getCurrentSession().delete(signature);
		} catch (Exception e) {
			throw wrapException("deleteSignature " + signature, e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Signature findByUuid(String uuid) throws PersistenceException, OCTobjectNotFoundException {
		try {
			Query getSignaturesByUuidQuery = this.sessionFactory.getCurrentSession()
					.createQuery("FROM Signature s WHERE s.uuid = :uuid").setParameter("uuid", uuid);
			Signature signatureByUUID = (Signature) getSignaturesByUuidQuery.uniqueResult();
			if (signatureByUUID == null) {
				throw new OCTobjectNotFoundException();
			}
			return signatureByUUID;
		} catch (HibernateException e) {
			throw wrapException("uuid " + uuid, e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = Exception.class)
	public void deleteAllSignatures() throws PersistenceException {
		try {
			this.sessionFactory.getCurrentSession().createQuery("DELETE FROM Signature").executeUpdate();
			this.sessionFactory.getCurrentSession().createQuery("DELETE FROM Email").executeUpdate();
			this.sessionFactory.getCurrentSession().createQuery("DELETE FROM IdentityValue").executeUpdate();
		} catch (Exception e) {
			throw wrapException("deleteAllSignatures", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public long countSignatures(ExportParameter exportParameter) throws PersistenceException {

		// @formatter:off
		String countQuery = "			SELECT  	COUNT(SIG.ID) AS COUNT" + "			FROM  		OCT_SIGNATURE SIG"
				+ "      		JOIN OCT_COUNTRY COU ON SIG.COUNTRYTOSIGNFOR_ID = COU.ID"
				+ "     		JOIN OCT_INITIATIVE_DESC DES ON DES.ID = SIG.DESCRIPTION_ID"
				+ "      		JOIN OCT_LANG LAN ON LAN.ID = DES.LANGUAGE_ID ";
		// @formatter:on

		Date startDate = exportParameter.getStartDate();
		Date endDate = exportParameter.getEndDate();
		boolean dateParameters = endDate != null && startDate != null;
		List<String> countries = exportParameter.getCountries();
		boolean countryParameter = !countries.isEmpty() && countries.size() != CommonsConstants.TOTAL_COUNTRIES_NUMBER;

		if (dateParameters || countryParameter) {
			countQuery += getFilterParametersSQL(startDate, endDate, countries);
		}

		try {
			Integer count = ((Number) this.sessionFactory.getCurrentSession().createSQLQuery(countQuery).uniqueResult())
					.intValue();
			return count.longValue();
		} catch (Exception e) {
			throw wrapException("countSignatures", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<ExportCountPerCountry> getExportCountPerCountry(ExportParameter exportParameter)
			throws PersistenceException {

		// @formatter:off
		String countQuery = "			SELECT  	COU.CODE AS COUNTRYCODE,"
				+ "						COUNT(SIG.ID) AS COUNT" + "			FROM  		OCT_SIGNATURE SIG"
				+ "      					RIGHT JOIN OCT_COUNTRY COU ON SIG.COUNTRYTOSIGNFOR_ID = COU.ID ";
		// @formatter:on

		Date startDate = exportParameter.getStartDate();
		Date endDate = exportParameter.getEndDate();
		boolean dateParameters = endDate != null && startDate != null;
		List<String> countries = exportParameter.getCountries();
		boolean countryParameter = !countries.isEmpty() && countries.size() != CommonsConstants.TOTAL_COUNTRIES_NUMBER;

		if (dateParameters || countryParameter) {
			countQuery += getFilterParametersSQL(startDate, endDate, countries);
		}
		countQuery += " GROUP BY COU.CODE";

		logger.info("COUNT QUERY REQUESTED: ");
		logger.info(countQuery);
		try {
			List<ExportCountPerCountry> exportCountPerCountryList = (List<ExportCountPerCountry>) this.sessionFactory
					.getCurrentSession().createSQLQuery(countQuery).addScalar("countryCode", StandardBasicTypes.STRING)
					.addScalar("count", StandardBasicTypes.LONG)
					.setResultTransformer(Transformers.aliasToBean(ExportCountPerCountry.class)).list();
			return exportCountPerCountryList;
		} catch (Exception e) {
			throw wrapException("getExportCountPerCountry", e);
		}
	}

	private String getFilterParametersSQL(Date startDate, Date endDate, List<String> countryCodesList) {
		String filterConditions = "";
		String countryCodesParameter = "";
		boolean countryFilter = false;
		if (!countryCodesList.isEmpty()) {
			countryFilter = true;
			int countryCodesSize = countryCodesList.size();
			for (int i = 0; i < countryCodesSize; i++) {
				String countryCode = countryCodesList.get(i);
				String countryCodeSLQparameterFormat = "'" + countryCode + "'";
				countryCodesParameter += countryCodeSLQparameterFormat;
				if (i < (countryCodesSize - 1)) {
					countryCodesParameter += ",";
				}
			}
		}

		String startDateParameterMsec = "";
		String endDateParameterMsec = "";
		boolean dateFilter = false;
		// startDate / endDate
		if (startDate != null && endDate != null) {
			startDateParameterMsec = startDate.getTime() + "";
			/* endDate = 23.59 of the day */
			endDateParameterMsec = "" + (endDate.getTime() + (ItemProcessorsUtils.ONE_DAY_MILLISEC - 1));
			dateFilter = true;
		}

		if (countryFilter || dateFilter) {
			filterConditions += " WHERE ";
			if (countryFilter) {
				filterConditions += " COU.CODE IN (" + countryCodesParameter + ") ";
			}
			if (dateFilter) {
				if (countryFilter) {
					filterConditions += " AND ";
				}
				filterConditions += " SIG.DATEOFSIGNATURE_MSEC >= " + startDateParameterMsec
						+ " AND SIG.DATEOFSIGNATURE_MSEC <= " + endDateParameterMsec;
			}

		}
		return filterConditions;
	}

	@Override
	@Transactional(readOnly = true)
	public List<FastSignatureCount> getAllFastSignatureCounts() throws PersistenceException {
		try {
			return this.sessionFactory.getCurrentSession().createQuery("FROM FastSignatureCount").list();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw wrapException("getSignatureCount", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Long getSignatureCountByCountryId(Long countryId) throws PersistenceException {
		try {
			Long totalForCountry = 0l;
			FastSignatureCount fsc = (FastSignatureCount) this.sessionFactory.getCurrentSession()
					.createQuery("FROM FastSignatureCount WHERE countryId = :countryId")
					.setParameter("countryId", countryId).uniqueResult();
			if (fsc != null) {
				totalForCountry = fsc.getCount();
			}
			return totalForCountry;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw wrapException("getSignatureCount", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = PersistenceException.class)
	public void updateSignature(Signature testSignature) throws PersistenceException {
		try {
			this.sessionFactory.getCurrentSession().update(testSignature);
			this.sessionFactory.getCurrentSession().flush();
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			throw wrapException("updateSignature " + testSignature, e);
		}

	}

	@Override
	@Transactional(readOnly = true)
	public List<FastSignatureCount> getFastSignatureCounts() throws PersistenceException {
		try {
			return this.sessionFactory.getCurrentSession()
					.createQuery("FROM FastSignatureCount fsc WHERE fsc.countryId < 99").list();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw wrapException("getTop7SignatureCounts", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Long getFastSignatureCountTotal() throws PersistenceException {
		Long totalCount = 0L;
		try {
			FastSignatureCount fscTotal = (FastSignatureCount) this.sessionFactory.getCurrentSession()
					.createQuery("FROM FastSignatureCount fsc WHERE fsc.countryId = "
							+ CommonsConstants.FAST_SIGNATURE_COUNT_TOTAL_COUNTRY_ID)
					.uniqueResult();
			if (fscTotal != null) {
				totalCount += fscTotal.getCount();
			}
			return totalCount;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw wrapException("getFastSignatureCountTotal", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public String findDuplicateIdentityValue(String countryCode, Long propertyId, String encryptedNewIdentityValue)
			throws PersistenceException {
		String duplicate = "";
		try {
			IdentityValue duplicateIdentityValue = (IdentityValue) this.sessionFactory.getCurrentSession().createQuery(
					"FROM IdentityValue WHERE countryCode = :countryCode AND propertyId = :propertyId AND identityValue = :identityValue")
					.setParameter("countryCode", countryCode).setParameter("propertyId", propertyId)
					.setParameter("identityValue", encryptedNewIdentityValue).uniqueResult();
			if (duplicateIdentityValue != null) {
				duplicate = duplicateIdentityValue.getIdentityValue();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw wrapException("findDuplicateIdentityValue", e);
		}
		return duplicate;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = PersistenceException.class)
	public void persistIdentityValue(IdentityValue identityValue) throws PersistenceException {
		try {
			this.sessionFactory.getCurrentSession().persist(identityValue);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw wrapException("persistIdentityValue", e);
		}

	}

	@Override
	@Transactional(readOnly = true)
	public List<IdentityValue> getAllIdentityValues() throws PersistenceException {
		List<IdentityValue> allIdentityValues = new ArrayList<IdentityValue>();
		try {
			allIdentityValues = (List<IdentityValue>) this.sessionFactory.getCurrentSession()
					.createQuery("FROM IdentityValue").list();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw wrapException("getAllIdentityValues", e);
		}
		return allIdentityValues;
	}

}