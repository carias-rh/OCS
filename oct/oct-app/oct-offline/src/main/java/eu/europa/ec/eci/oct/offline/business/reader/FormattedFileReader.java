package eu.europa.ec.eci.oct.offline.business.reader;

import eu.europa.ec.eci.oct.export.DataException;
import eu.europa.ec.eci.oct.export.model.SupportForm;

import java.io.File;

/**
 * @author: micleva
 * @created: 12/2/11
 * @project OCT
 */
public interface FormattedFileReader {

    public SupportForm readFromFile(File inputFile) throws DataException;
}
