package eu.europa.ec.eci.oct.offline.support.log;

import eu.europa.ec.eci.oct.offline.support.Utils;
import eu.europa.ec.eci.oct.offline.support.crypto.KeyProviderFolderName;

import java.io.File;
import java.io.IOException;
import java.util.logging.*;

/**
 * @author: micleva
 * @created: 12/14/11
 * @project OCT
 */
public class OfflineCryptoToolLogger extends Logger {
//    private static final String LOG_FOLDER = "log";

    // the max file of one log file is 25 MB
    private static final int MB_25 = 25 * 1024 * 1024;

    private OfflineCryptoToolLogger(String name, String resourceBundleName) {
        super(name, resourceBundleName);
    }

    public static synchronized OfflineCryptoToolLogger getLogger(String name) {
        LogManager manager = LogManager.getLogManager();

        Logger result = manager.getLogger(name);
        OfflineCryptoToolLogger octLogger = null;
        if (result == null || !(result instanceof OfflineCryptoToolLogger)) {
            octLogger = new OfflineCryptoToolLogger(name, null);
            manager.addLogger(octLogger);
        }
        return octLogger;
    }


    public static void setup(boolean isVerbose) {
        try {
            Logger logger = Logger.getLogger("");
            logger.setLevel(isVerbose ? Level.FINE : Level.INFO);

            Handler[] handlers = logger.getHandlers();
            for (Handler handler : handlers) {
                logger.removeHandler(handler);
            }

            String logFolderPath = Utils.getFolderPathInProject(KeyProviderFolderName.LOG_FOLDER);
            File logFolder = new File(logFolderPath);
            boolean logFolderOk = logFolder.exists() && logFolder.isDirectory();
            if (!logFolder.exists() || !logFolder.isDirectory()) {
                //create the log folder if it doesn't exist
                logFolderOk = logFolder.mkdir();
            }


            if (logFolderOk) {

                FileHandler fileHandler = new FileHandler(logFolderPath + "/oct_%g.log", MB_25, 20);
                fileHandler.setFormatter(new CryptoToolLogFormatter());
                logger.addHandler(fileHandler);
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to initialize the logger: " + e.getMessage(), e);
        }
    }

    public void debug(String msg, Throwable ex) {
        log(Level.FINE, msg, ex);
    }

    public void warning(String msg, Throwable ex) {
        log(Level.WARNING, msg, ex);
    }

    public void info(String msg, Throwable ex) {
        log(Level.INFO, msg, ex);
    }


}
