package eu.europa.ec.eci.oct.offline.business.reader;

import eu.europa.ec.eci.oct.export.DataException ;
import eu.europa.ec.eci.oct.export.JAXBHelper;
import eu.europa.ec.eci.oct.export.model.SupportForm;
import eu.europa.ec.eci.oct.offline.business.DecryptConstants;

import java.io.*;

/**
 * @author: micleva
 * @created: 12/2/11
 * @project OCT
 */
class XmlFormattedFileReader implements FormattedFileReader {

    @Override
    public SupportForm readFromFile(File inputFile) throws DataException {
        try {

            final InputStreamReader inputFileReader = new InputStreamReader(new FileInputStream(inputFile), DecryptConstants.CHARACTER_ENCODING);

            JAXBHelper jaxbHelper = JAXBHelper.getInstance();
            return jaxbHelper.unmarshal(inputFileReader);
        } catch (FileNotFoundException e) {
            throw new DataException("Unable to find the input file: " + inputFile.getAbsolutePath(), e);
        } catch (UnsupportedEncodingException e) {
            throw new DataException("Unsupported exception for file: " + inputFile.getAbsolutePath(), e);
        }
    }
}
