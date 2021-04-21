package eu.europa.ec.eci.oct.export.rowMappers;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import eu.europa.ec.eci.oct.export.ExportJobRunner;
import eu.europa.ec.eci.oct.export.entities.SignatureBatch;

public class SignatureRowMapper implements RowMapper<SignatureBatch> {

	final String SIGNATORY_INFO_COLUMN_LABEL = "SIGNATORYINFO";
	final String COUNTRY_TO_SIGN_FOR_COLUMN_LABEL = "COUNTRYTOSIGNFOR";
	final String ANNEX_REVISION_COLUMN_LABEL = "ANNEXREVISION";
	final String SIGNATURE_IDENTIFIER_COLUMN_LABEL = "SIGNATUREIDENTIFIER";

	@Override
	public SignatureBatch mapRow(ResultSet resultSet, int rowNum) throws SQLException {

		Blob signatoryInfoBlob = resultSet.getBlob(SIGNATORY_INFO_COLUMN_LABEL);
		String signatoryInfoString = null;
		int signatoryInfoBlobLength = (int) signatoryInfoBlob.length();
		signatoryInfoString = new String(signatoryInfoBlob.getBytes(1, signatoryInfoBlobLength));
		long countryToSignForId = resultSet.getLong(COUNTRY_TO_SIGN_FOR_COLUMN_LABEL);
		String signatureIdentifier = resultSet.getString(SIGNATURE_IDENTIFIER_COLUMN_LABEL);
		int annexRevision = resultSet.getInt(ANNEX_REVISION_COLUMN_LABEL);
		SignatureBatch signatureBatch = new SignatureBatch();
		signatureBatch.setSignatoryInfo(signatoryInfoString);
		signatureBatch.setCountryToSignFor(getCountryCodeFromId(countryToSignForId));
		signatureBatch.setAnnexRevision(annexRevision);
		signatureBatch.setSignatureIdentifier(signatureIdentifier);

		return signatureBatch;
	}

	private String getCountryCodeFromId(long countryToSignForId) {
		return ExportJobRunner.countryIdCodeMap.get(countryToSignForId);
	}

}
