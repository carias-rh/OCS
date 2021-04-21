package eu.europa.ec.eci.oct.offline.business.writer.xml;

import java.io.File;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.FileUtils;

import eu.europa.ec.eci.oct.export.DataException;
import eu.europa.ec.eci.oct.export.model.SupportForm;

public class XmlFileWriter {

	private static final String DECRYPT_FILE_SUFFIX = "_dec";

	public static void writeToFile(SupportForm supportForm, String outputFolderAbsolutePath, File fileInSelection,
			File selectedInput) throws DataException {
		JAXBContext jaxbContext;
		String xmlToWrite = "";
		try {
			jaxbContext = JAXBContext.newInstance(SupportForm.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			StringWriter sw = new StringWriter();
			marshaller.marshal(supportForm, sw);
			xmlToWrite = sanitizeForXml(sw.toString());
		} catch (JAXBException e) {
			throw new DataException(e.getMessage(), e);
		}

		// write to file
		String outputPath = buildOutputFilePath(outputFolderAbsolutePath, fileInSelection, selectedInput);
		File outputFile = new File(outputPath);
		try {
			FileUtils.write(outputFile, xmlToWrite, StandardCharsets.UTF_8, true);
		} catch (Exception e) {
			throw new DataException("Error writing file: " + e.getMessage(), e);
		}
	}

	private static String sanitizeForXml(String xml) {
		return xml.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
	}

	public static String buildOutputFilePath(String outputFileAbsolutePath, File fileInSelection, File selectedInput) {
		StringBuilder outputFilePath = new StringBuilder();
		outputFilePath.append(outputFileAbsolutePath);

		if (selectedInput.isDirectory()) {
			// build the path inside the output folder

			// get the input file path according to the input root file
			String inputRootPath = selectedInput.getAbsolutePath();
			String inputFilePath = fileInSelection.getAbsolutePath();
			// remove the root from the path
			String pathFromInputRootToFile = inputFilePath.substring(inputRootPath.length());
			// remove the inputFile simple name from the path as the file name will be
			// modified
			pathFromInputRootToFile = pathFromInputRootToFile.substring(0,
					(pathFromInputRootToFile.length() - fileInSelection.getName().length()));

			// append the input root filename to the output folder path
			outputFilePath.append(File.separator);
			outputFilePath.append(selectedInput.getName());

			// append the path from root to file
			outputFilePath.append(File.separator);
			outputFilePath.append(pathFromInputRootToFile);
		}
		outputFilePath.append(File.separator);
		String fileName = fileInSelection.getName();
		int extensionStartPos = fileName.lastIndexOf('.');

		if (extensionStartPos > 0 && extensionStartPos < fileName.length() - 1) {
			fileName = fileName.substring(0, extensionStartPos);
		}
		outputFilePath.append(fileName);

		// add the suffix
		outputFilePath.append(DECRYPT_FILE_SUFFIX);
		outputFilePath.append(".xml");
		
		return outputFilePath.toString();
	}

}
