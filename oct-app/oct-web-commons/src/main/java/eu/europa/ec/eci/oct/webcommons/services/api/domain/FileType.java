package eu.europa.ec.eci.oct.webcommons.services.api.domain;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;


public enum FileType {
	LOGO("logo"), DESCRIPTION("description"), CERTIFICATE("certificate");

	private String fileType;

	FileType(String fileType) {
		this.fileType = fileType;
	}

	public String fileType() {
		return fileType;
	}

	public static FileType getFileTypeEnumFromTypeName(String type) throws OCTException {
		if(!isInvalidType(type)){
			return EnumUtils.getEnumMap(FileType.class).get(type.toUpperCase());
		}else{
			throw new OCTException("Invalid file type: "+type);
		}
	}
	
	public static boolean isInvalidType(String param) {
		if (!(EnumUtils.getEnumMap(FileType.class).containsKey(param.toUpperCase())) || (StringUtils.isEmpty(param))) {
			return true;
		} else {
			return false;
		}
	}
	
}