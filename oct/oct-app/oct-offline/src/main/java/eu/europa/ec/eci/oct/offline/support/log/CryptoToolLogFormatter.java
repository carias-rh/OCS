package eu.europa.ec.eci.oct.offline.support.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * @author: micleva
 * @created: 12/14/11
 * @project OCT
 */
public class CryptoToolLogFormatter extends Formatter {

    private static final DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");

    public CryptoToolLogFormatter() {
        super();
    }

    public String format(LogRecord record) {

        // Create a StringBuilder to contain the formatted record
        // start with the date.
        StringBuilder builder = new StringBuilder();

        builder.append(df.format(new Date(record.getMillis()))).append(" - [");
        builder.append(record.getSourceClassName()).append('.');
        builder.append(record.getSourceMethodName()).append("] - [");
        builder.append(record.getLevel()).append("] - ");
        builder.append(record.getMessage());

        Throwable thrown = record.getThrown();
        if (thrown != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            thrown.printStackTrace(pw);

            builder.append(" StackTrace:");
            builder.append(sw.toString());
        }

        builder.append("\n");

        return builder.toString();
    }

}
