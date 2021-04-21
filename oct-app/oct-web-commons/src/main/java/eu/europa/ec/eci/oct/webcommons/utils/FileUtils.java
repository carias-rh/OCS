package eu.europa.ec.eci.oct.webcommons.utils;

import org.apache.commons.lang3.StringUtils;

import eu.europa.ec.eci.oct.webcommons.services.api.domain.FileType;

public class FileUtils {
	public static boolean isAcceptedExtension(String fileName, FileType type) {
		if (StringUtils.isBlank(fileName) || type == null) {
			return false;
		}
		switch (type) {
		case DESCRIPTION:
			if (fileName.endsWith(".xml")) {
				return true;
			}
		case LOGO:
			// .jpg, .jpeg, .gif, .png
			if (fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".jpeg") || fileName.toLowerCase().endsWith(".gif")
					|| fileName.toLowerCase().endsWith(".bmp") || fileName.toLowerCase().endsWith(".png")) {
				return true;
			}
		case CERTIFICATE:
			// .pdf
			if (fileName.endsWith(".pdf")) {
				return true;
			}
		default:
			return false;
		}
	}

}