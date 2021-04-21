package eu.europa.ec.eci.oct.export.entities;

public class SignatureBatch {

	private String signatoryInfo;
	private String descriptionLanguage;
	private String countryToSignFor;
	private String signatureIdentifier;
	private int annexRevision;

	public String getSignatureIdentifier() {
		return signatureIdentifier;
	}

	public void setSignatureIdentifier(String signatureIdentifier) {
		this.signatureIdentifier = signatureIdentifier;
	}

	public int getAnnexRevision() {
		return annexRevision;
	}

	public void setAnnexRevision(int annexRevision) {
		this.annexRevision = annexRevision;
	}

	public String getSignatoryInfo() {
		return signatoryInfo;
	}

	public void setSignatoryInfo(String signatoryInfo) {
		this.signatoryInfo = signatoryInfo;
	}

	public String getDescriptionLanguage() {
		return descriptionLanguage;
	}

	public void setDescriptionLanguage(String descriptionLanguage) {
		this.descriptionLanguage = descriptionLanguage;
	}

	public String getCountryToSignFor() {
		return countryToSignFor;
	}

	public void setCountryToSignFor(String countryToSignFor) {
		this.countryToSignFor = countryToSignFor;
	}

	public static String previewSignatoryInfo(String signatoryInfo) {
		return signatoryInfo.substring(0, 158) + "...";
	}

	@Override
	public String toString() {
		return "SignatureBatch [signatoryInfo=" + signatoryInfo + ", descriptionLanguage=" + descriptionLanguage
				+ ", countryToSignFor=" + countryToSignFor + ", signatureIdentifier=" + signatureIdentifier
				+ ", annexRevision=" + annexRevision + "]";
	}
}
