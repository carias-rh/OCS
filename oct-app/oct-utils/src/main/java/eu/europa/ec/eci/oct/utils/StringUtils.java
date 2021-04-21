package eu.europa.ec.eci.oct.utils;

import java.sql.Blob;

public class StringUtils {

	public static boolean isEmpty(String s){		
		return s==null || s.trim().length()==0;
	}
	
	public static String getStringFromBlob(Blob blob) throws Exception {
		String string = null;
		try {
			int blobLength = (int) blob.length();
			string = new String(blob.getBytes(1, blobLength));
		} catch (Exception e) {
			throw new Exception("Error while converting data: " + e.getMessage());
		}
		return string;
	}
	
}
