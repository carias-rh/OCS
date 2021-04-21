package eu.europa.ec.eci.oct.webcommons.services.api.transformer;

import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.rowset.serial.SerialBlob;

import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Component;

import eu.europa.ec.eci.oct.entities.Property;
import eu.europa.ec.eci.oct.entities.signature.Signature;
import eu.europa.ec.eci.oct.entities.system.Country;
import eu.europa.ec.eci.oct.entities.views.FastSignatureCount;
import eu.europa.ec.eci.oct.utils.DateUtils;
import eu.europa.ec.eci.oct.utils.XMLutils;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.signature.SignatureCountryCount;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.signature.SignatureDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.signature.SignatureMetadata;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.supportForm.SupportFormDTO;
import eu.europa.ec.eci.oct.webcommons.services.crypto.CryptographyService;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;

@Component
public class SignatureTransformer extends BaseTransformer {

	public Signature transform(SignatureDTO signatureDTO) throws OCTException {

		if (signatureDTO == null) {
			return null;
		}

		Signature signature = new Signature();

		Country country = null;
		try {
			country = systemManager.getCountryByCode(signatureDTO.getCountry());
		} catch (OCTException e) {
			throw new OCTException("ERROR: " + e.getMessage());
		}
		signature.setCountryToSignFor(country);

		Date nowDate = new Date();
		signature.setDateOfSignature(nowDate);
		signature.setDateOfSignatureMsec(nowDate.getTime());

		char[] publicKey = systemManager.getSystemPreferences().getPublicKey().toCharArray();
		final CryptographyService crypto = CryptographyService.getService(publicKey);

		String signatoryInfoXML = getSignatoryInfoXML(signatureDTO, crypto);

		byte[] baSignatoryInfoXML = signatoryInfoXML.getBytes();
		try {
			final byte[] signatureHash = crypto.fingerprint(serialize(signatureDTO));
			signature.setFingerprint(new String(Hex.encodeHex(signatureHash)));
			Blob blobSignatoryInfoXML = new SerialBlob(baSignatoryInfoXML);
			signature.setSignatoryInfo(blobSignatoryInfoXML);
		} catch (Exception e) {
			throw new OCTException("Error transforming signatyre: " + e.getMessage());
		}
		try {
			signature.setFingerprint(hashSOS(signatureDTO));
		} catch (Exception e) {
			throw new OCTException("ERROR: " + e.getMessage());
		}

		signature.setAnnexRevision(systemManager.getSystemPreferences().getCurrentAnnexRevision());

		return signature;
	}

	public String hashSOS(SignatureDTO signatureDTO) throws Exception {
		byte[] signatureHash = null;
		char[] publicKey = systemManager.getSystemPreferences().getPublicKey().toCharArray();
		final CryptographyService crypto = CryptographyService.getService(publicKey);
		try {
			signatureHash = crypto.fingerprint(serialize(signatureDTO));
		} catch (Exception e1) {
			throw new Exception("ERROR: " + e1.getMessage());
		}
		return new String(Hex.encodeHex(signatureHash));
	}

	private String serialize(SignatureDTO s) throws Exception {
		StringBuilder buf = new StringBuilder();

		for (Iterator<SupportFormDTO> iterator = s.getProperties().iterator(); iterator.hasNext();) {
			SupportFormDTO supportFormDTO = iterator.next();
			String propName = SupportFormDTO.OCT_PROPERTY_PREFIX + supportFormDTO.getLabel();
			// System.err.println("PROP NAME = "+supportFormDTO.getLabel());
			// System.err.println("PROP VALUE = "+supportFormDTO.getValue());
			String propValue = supportFormDTO.getValue().toUpperCase();
			buf.append("[").append(propName).append("][").append(propValue).append("]");
		}
		return buf.toString();
	}

	private String getSignatoryInfoXML(SignatureDTO signatureDTO, CryptographyService crypto) throws OCTException {
		StringBuilder buf = new StringBuilder();

//		buf.append("<signatoryInfo>");
		buf.append("<groups>");

		Map<String, List<String>> propertiesMap = getPropertiesMap(signatureDTO, crypto);
		for (String propertyGroupName : propertiesMap.keySet()) {
			buf.append("<group>");
			buf.append("<name>").append(propertyGroupName).append("</name>");
			buf.append("<properties>");
			List<String> propertyValuesMapList = propertiesMap.get(propertyGroupName);
			for (String encryptedPropertyValue : propertyValuesMapList) {
				buf.append("<property>").append(encryptedPropertyValue).append("</property>");
			}
			buf.append("</properties>");
			buf.append("</group>");
		}

		buf.append("</groups>");
//		buf.append("</signatoryInfo>");
		return buf.toString();

	}

	private Map<String, List<String>> getPropertiesMap(SignatureDTO signatureDTO, CryptographyService crypto)
			throws OCTException {
		// System.err.println("getPropertiesMap: "+signatureDTO.getProperties());
		Map<String, List<String>> propertyGroupsMap = new HashMap<String, List<String>>();
		for (SupportFormDTO supportFormDTO : signatureDTO.getProperties()) {

			String propertyValue = supportFormDTO.getValue();

			propertyValue = new String(Hex.encodeHex(crypto.encrypt(propertyValue)));

			// System.err.println("PV: "+propertyValue);

			String propertyName = "";
			String propertyGroup = "";
			Property property = null;
			// System.err.println("signatureDTO: "+signatureDTO);
			try {
				String string = SupportFormDTO.OCT_PROPERTY_PREFIX + supportFormDTO.getLabel();
				// System.err.println("PROP NAME: "+string);
				property = propertyDAO.getPropertyByLabel(string);
				// System.err.println("supportFormDTO: "+supportFormDTO);
				propertyName = property.getName();
				propertyGroup = property.getGroup().getName();
			} catch (Exception e) {
				throw new OCTException("ERROR: " + e.getMessage());
			}

			String countryPropertyValue = XMLutils.KEY_TAG_OPEN + propertyName
					+ XMLutils.KEY_TAG_CLOSE + XMLutils.VALUE_TAG_OPEN + propertyValue + XMLutils.VALUE_TAG_CLOSE;

			if (propertyGroupsMap.containsKey(propertyGroup)) {
				propertyGroupsMap.get(propertyGroup).add(countryPropertyValue);
			} else {
				List<String> propertyValuesList = new ArrayList<String>();
				propertyValuesList.add(countryPropertyValue);
				propertyGroupsMap.put(propertyGroup, propertyValuesList);
			}
		}
		return propertyGroupsMap;
	}

	public SignatureMetadata transformMetadata(Signature signature) {
		SimpleDateFormat formatter = new SimpleDateFormat(DateUtils.DEFAULT_DATE_FORMAT);
		SignatureMetadata signatureMetadata = new SignatureMetadata();
		signatureMetadata.setDate(formatter.format(signature.getDateOfSignature()));
		signatureMetadata.setCountry(signature.getCountryToSignFor().getCode());
		return signatureMetadata;
	}

	public List<SignatureCountryCount> fromFSCtoSignatureCount(List<FastSignatureCount> allFastSignatureCounts) {
		List<SignatureCountryCount> signatureCountryCountList = new ArrayList<SignatureCountryCount>();
		for (FastSignatureCount fastSignatureCount : allFastSignatureCounts) {
			long totalForCountry = fastSignatureCount.getCount();
			SignatureCountryCount signatureCountryCount = new SignatureCountryCount();
			signatureCountryCount.setCountryCode(fastSignatureCount.getCountryCode());
			signatureCountryCount.setCount(totalForCountry);
			long threshold = fastSignatureCount.getThreshold();
			signatureCountryCount.setTreshold(threshold);
			double percentage = 0;
			if (totalForCountry > 0) {
				percentage = (totalForCountry * 100) / threshold;
			}
			signatureCountryCount.setPercentage(percentage);
			signatureCountryCountList.add(signatureCountryCount);
		}
		Collections.sort(signatureCountryCountList);
		return signatureCountryCountList;
	}

}
