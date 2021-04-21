package eu.europa.ec.eci.oct.offline.support.summary;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: micleva
 * @created: 11/14/11
 * @project OCT
 */
public class SelectionSummary {

    long sizeInBytes = 0;
    int fileInTotal = 0;

    private static final Map<Integer, String> levelSizeString;

    static {
        levelSizeString = new HashMap<Integer, String>();
        levelSizeString.put(1, "bytes");
        levelSizeString.put(2, "KB");
        levelSizeString.put(3, "MB");
        levelSizeString.put(4, "GB");
        levelSizeString.put(5, "TB");
    }

    SelectionSummary() {
    }

    public int getFileInTotal() {
        return fileInTotal;
    }

    public String getPrettyFormattedFileSize() {

        int sizeLevel = 1;

        BigDecimal sizeToDisplay = new BigDecimal(sizeInBytes);
        BigDecimal divisor = new BigDecimal(1024);

        while (sizeToDisplay.doubleValue() > 1000) {

            //devide the size to display with 1024
            sizeToDisplay = sizeToDisplay.divide(divisor, 4, RoundingMode.HALF_UP);

            sizeLevel++;
        }

        //the display size in the end should have 2 decimals
        sizeToDisplay = sizeToDisplay.compareTo(BigDecimal.ZERO) == 0 ? sizeToDisplay.setScale(0, RoundingMode.HALF_UP)
                : sizeToDisplay.setScale(2, RoundingMode.HALF_UP);

        StringBuilder formattedSize = new StringBuilder();
        formattedSize.append(sizeToDisplay).append(' ').append(levelSizeString.get(sizeLevel));

        return formattedSize.toString();
    }

    SelectionSummary copy() {
        SelectionSummary copy = new SelectionSummary();
        copy.sizeInBytes = this.sizeInBytes;
        copy.fileInTotal = this.fileInTotal;

        return copy;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("FileSummary");
        sb.append("{sizeInBytes=").append(sizeInBytes);
        sb.append(", fileInTotal=").append(fileInTotal);
        sb.append('}');
        return sb.toString();
    }
}
