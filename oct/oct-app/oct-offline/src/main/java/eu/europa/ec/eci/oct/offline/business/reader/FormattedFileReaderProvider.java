package eu.europa.ec.eci.oct.offline.business.reader;

import eu.europa.ec.eci.oct.offline.business.FileType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: micleva
 * @created: 12/2/11
 * @project OCT
 */
public class FormattedFileReaderProvider {
    private static FormattedFileReaderProvider instance;
    private Map<FileType, FormattedFileReader> readers;

    private FormattedFileReaderProvider() {
        readers = new HashMap<FileType, FormattedFileReader>();
        readers.put(FileType.XML, new XmlFormattedFileReader());
    }

    private static FormattedFileReaderProvider getInstance() {
        if (instance == null) {
            instance = new FormattedFileReaderProvider();
        }
        return instance;
    }

    public static FormattedFileReader getFormattedFileReaderProvider(FileType fileType) {
        FormattedFileReaderProvider instance = FormattedFileReaderProvider.getInstance();
        if (!instance.readers.containsKey(fileType)) {
            throw new UnsupportedOperationException("No reader configured for the file type: " + fileType);
        }
        return instance.readers.get(fileType);
    }
}
