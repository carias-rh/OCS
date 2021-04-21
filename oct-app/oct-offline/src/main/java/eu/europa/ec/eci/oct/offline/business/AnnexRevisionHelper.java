package eu.europa.ec.eci.oct.offline.business;

import java.util.HashMap;
import java.util.Map;
import eu.europa.ec.eci.oct.export.model.SignatureListType;
import eu.europa.ec.eci.oct.export.model.SignatureType;
import eu.europa.ec.eci.oct.export.model.SupportForm;

public class AnnexRevisionHelper {

//	//Annex revision related to the first release to OCS
//	public static final int ANNEX_REVISION_0_INITIAL_RELEASE = 0;
//	
//	//Annex revision related to annex III changes.
//	public static final int ANNEX_REVISION_1_ANNEX_III = 1;
//	
	private Map<AnnexRevisionType,SupportForm> supportFormRevisionMap;
	
	public AnnexRevisionHelper(){
		supportFormRevisionMap = new HashMap<AnnexRevisionType,SupportForm>();
	}
	
	/**
	 * Method taking care of clone and divide the statemente of support to each revision number
	 * @param supportForm
	 * @return Map<Integer,SupportForm>
	 */
	public Map<AnnexRevisionType,SupportForm> annexRevisionSplitter(SupportForm supportForm){
		
		if(hasDifferentAnnexRevisions(supportForm)){
			//If supportForm has an annexRevision
			for(SignatureType signatureType : supportForm.getSignatures().getSignature()){
				addSignatureToRevision(signatureType, supportForm);					
			}
		}else{
			//SupportForum doesn't contains a revision, so all supportForm can be processed as it is.
			supportFormRevisionMap.put(AnnexRevisionType.ANNEX_REVISION_0_INITIAL_RELEASE, supportForm);
		}
		return supportFormRevisionMap;
	}
	
	
	/**
	 * Method that care of to add a the passed signatureType to the related SupportForm
	 * @param signatureType
	 * @param supportForm
	 */
	private void addSignatureToRevision(SignatureType signatureType, SupportForm supportForm) {
		
		AnnexRevisionType annexRevisionType = AnnexRevisionType.valueOf(signatureType.getAnnexRevision());
		
		SupportForm sfAnnexRevision = supportFormRevisionMap.get(annexRevisionType);
		if(sfAnnexRevision == null){
			SupportForm newSupportForm = cloneSupportForm(supportForm);
			supportFormRevisionMap.put(annexRevisionType, newSupportForm);
			addSignatureToNewRevisionSupportForm(newSupportForm, signatureType);
		}else{
//			supportFormRevisionMap.put(annexRevisionType, sfAnnexRevision);
			addSignatureToNewRevisionSupportForm(sfAnnexRevision, signatureType);
		}
	}
	
	/**
	 * Add signature to the related support form.
	 * @param newSupportForm
	 * @param signatureType
	 */
	private void addSignatureToNewRevisionSupportForm(SupportForm newSupportForm, SignatureType signatureType){
		newSupportForm.getSignatures().getSignature().add(signatureType);
	}
	
	/**
	 * Clone the SupportForm data except for the Signature list, that would be empty
	 * @param supportForm
	 * @return cloned SupportForm
	 */
	private SupportForm cloneSupportForm(SupportForm supportForm){
		
		SupportForm newSupportForm = new SupportForm();
		newSupportForm.setForCountry(supportForm.getForCountry());
		newSupportForm.setInitiativeData(supportForm.getInitiativeData());
		newSupportForm.setSignatures(new SignatureListType());
		
		return newSupportForm;
	}
	
	/**
	 * Look into SupportForm object if there are annexRevision
	 * @param supportForm
	 * @return boolean
	 */
	public boolean hasDifferentAnnexRevisions(SupportForm supportForm){
		
		boolean toReturn = false;
		
		cycle:for(SignatureType signatureType  : supportForm.getSignatures().getSignature()){
			if(signatureType.getAnnexRevision() > AnnexRevisionType.ANNEX_REVISION_0_INITIAL_RELEASE.getRevisionNumber()){
				toReturn = true;
				break cycle;
			}
		}
		return toReturn;
	}
	
	
	/**
	 * Retrieve the support form signatures annex revision.
	 * this has to be done after the originating supportForm has been splitted with different revision
	 * @param supportForm
	 * @return AnnexRevisionType
	 */
	public static AnnexRevisionType getAnnexRevisionNumber(SupportForm supportForm){
		int annexRevision = AnnexRevisionType.ANNEX_REVISION_0_INITIAL_RELEASE.getRevisionNumber();
		cycle:for(SignatureType signatureType  : supportForm.getSignatures().getSignature()){
			annexRevision = signatureType.getAnnexRevision();
			break cycle;
		}
		AnnexRevisionType annexRevisionType = AnnexRevisionType.valueOf(annexRevision);
		return annexRevisionType;
	}
	
	
}
