package eu.europa.ec.eci.oct.offline.support.summary;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: micleva
 * @created: 11/24/11
 * @project OCT
 */
public class SummaryEntry {

    final File rootFile;

    List<File> filesInSelection = new ArrayList<File>();
    long sizeInBytes = 0;

    public SummaryEntry(File rootFile) {
        this.rootFile = rootFile;
    }

    public File getRootFile() {
        return rootFile;
    }

    public List<File> getFilesInSelection() {
        return filesInSelection;
    }

    public long getSizeInBytes() {
        return sizeInBytes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SummaryEntry)) return false;

        SummaryEntry that = (SummaryEntry) o;

        if (sizeInBytes != that.sizeInBytes) return false;
        if (filesInSelection != null ? !filesInSelection.equals(that.filesInSelection) : that.filesInSelection != null)
            return false;
        if (rootFile != null ? !rootFile.equals(that.rootFile) : that.rootFile != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = rootFile != null ? rootFile.hashCode() : 0;
        result = 31 * result + (filesInSelection != null ? filesInSelection.hashCode() : 0);
        result = 31 * result + (int) (sizeInBytes ^ (sizeInBytes >>> 32));
        return result;
    }
}
