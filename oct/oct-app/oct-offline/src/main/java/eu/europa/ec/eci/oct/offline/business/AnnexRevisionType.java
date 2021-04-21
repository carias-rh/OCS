package eu.europa.ec.eci.oct.offline.business;

import java.util.HashMap;
import java.util.Map;

public enum AnnexRevisionType {

	/**
	 * Annex revision related to the first release to OCS
	 */
	ANNEX_REVISION_0_INITIAL_RELEASE(0),
	
	/**
	 * Annex revision related to annex III changes. 
	 */
	ANNEX_REVISION_1_ANNEX_III(1),
	
	/**
	 * Annex revision related to annex III changes. 15/June/2015 
	 */
	ANNEX_REVISION_2_ANNEX_III(2);
	
	private int annexRevisionNumber;
	
	private static Map<Integer, AnnexRevisionType> annexRevisionTypeMap = new HashMap<Integer, AnnexRevisionType>();
	
	static {
	    for (AnnexRevisionType type : AnnexRevisionType.values()) {
	    	annexRevisionTypeMap.put(type.getRevisionNumber(), type);
	    }
	}
	
	
	AnnexRevisionType(int annexRevisionNumber){
		this.annexRevisionNumber = annexRevisionNumber;
	}
	
	public int getRevisionNumber(){
		return annexRevisionNumber;
	}
	
	public static AnnexRevisionType valueOf(int revisionNumber){
		return annexRevisionTypeMap.get(revisionNumber);
	}
}
