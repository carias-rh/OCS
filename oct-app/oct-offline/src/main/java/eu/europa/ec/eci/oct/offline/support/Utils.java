package eu.europa.ec.eci.oct.offline.support;

import java.awt.Component;
import java.awt.Container;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;

import javax.imageio.ImageIO;
import javax.swing.Box;

import eu.europa.ec.eci.oct.offline.support.crypto.KeyProvider;
import eu.europa.ec.eci.oct.offline.support.crypto.KeyProviderFolderName;
import eu.europa.ec.eci.oct.offline.support.log.OfflineCryptoToolLogger;

/**
 * @author: micleva
 * @created: 11/8/11
 * @project OCT
 */
public class Utils {
    private static OfflineCryptoToolLogger log = OfflineCryptoToolLogger.getLogger(Utils.class.getName());

    private static final long ONE_HOUR = 60 * 60 * 1000;
    private static final long ONE_MINUTE = 60 * 1000;
    private static final long ONE_SECOND = 1000;

    private Utils() {
    }

    public static Component getXSeparator(int width) {
        return Box.createHorizontalStrut(width);
    }

    public static Component getYSeparator(int height) {
        return Box.createVerticalStrut(height);
    }

    /**
     * Switches the enable state of all the components given as an input parameter and
     * pf all the child components of the components given as input
     *
     * @param components   the components that should change the state
     * @param enabledState true to put the components on enabled states, false to disable them
     */
    public static void switchComponentsIncludingChildrenState(Component[] components, boolean enabledState) {
        for (Component component : components) {
            component.setEnabled(enabledState);
            if (component instanceof Container) {
                Container container = (Container) component;
                Component[] childComponents = container.getComponents();
                if (childComponents.length > 0) {
                    switchComponentsIncludingChildrenState(childComponents, enabledState);
                }
            }
        }
    }

    /**
     * Returns a formatted elapsed time since a given time in millies.
     * The format looks like this: hours:minutes:seconds
     *
     * @param startTime the time since the elapsed time is calculated
     * @return the elapsed time in format hours:minutes:seconds
     */
    public static String getFormattedElapsedTimeSince(long startTime) {
        long passedMillis = System.currentTimeMillis() - startTime;
        long hours = passedMillis / ONE_HOUR;
        long minutes = (passedMillis % ONE_HOUR) / ONE_MINUTE;
        long seconds = ((passedMillis % ONE_HOUR) % ONE_MINUTE) / ONE_SECOND;

        StringBuilder formattedTime = new StringBuilder();

        if (hours < 10) {
            formattedTime.append('0');
        }
        formattedTime.append(hours).append(':');
        if (minutes < 10) {
            formattedTime.append('0');
        }
        formattedTime.append(minutes).append(':');
        if (seconds < 10) {
            formattedTime.append('0');
        }
        formattedTime.append(seconds);

        return formattedTime.toString();
    }

    public static File getDataFile(KeyProviderFolderName keyProviderFolderName , String fileName) throws UnsupportedEncodingException {

        String dataFolderPath = getFolderPathInProject(keyProviderFolderName);

        File dataFolder = new File(dataFolderPath);
        if (!dataFolder.exists() || !dataFolder.isDirectory()) {
            //create the data folder if it doesnt exist
            boolean wasCreated = dataFolder.mkdirs();
            if (!wasCreated) {
                // if the new folder was not created (it already existed),
                // then something is wrong with the logic
                throw new IllegalStateException("Data folder \"" + dataFolder +
                        "\" was not created! This should not happen.");
            }
        }

        StringBuilder dataFilePath = new StringBuilder();
        dataFilePath.append(dataFolder.getAbsolutePath());

        if(fileName != null){
            dataFilePath.append(File.separatorChar).append(fileName);
        }

        return new File(dataFilePath.toString());
    }

    public static InputStream getInternalResource(String resourceFileName)  {

        //create an instance just to be able to load the file
        Utils utils = new Utils();
        return utils.getClass().getClassLoader().getResourceAsStream(resourceFileName);
    }

    public static String getFolderPathInProject(KeyProviderFolderName keyProviderFolderName) throws UnsupportedEncodingException {

        File currentFolder = new File(KeyProvider.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        currentFolder = currentFolder.isDirectory() ? currentFolder : currentFolder.getParentFile();
        File parent = currentFolder.getParentFile();

        StringBuilder folderPathBuilder = new StringBuilder();
        try {
            folderPathBuilder.append(parent.getCanonicalPath());
        } catch (IOException e) {
            folderPathBuilder.append(parent.getAbsolutePath());
            log.debug("Unable to read the canonical path for file: " + parent, e);
        }
//        if(keyProviderFolderName == KeyProviderFolderName.KEYS_TO_SEND_FOLDER_NAME){
//            folderPathBuilder.append('/').append(KeyProviderFolderName.CRYPTO_DATA_FOLDER_NAME.getName()).append('/').append(keyProviderFolderName.getName());
//        }else{
            folderPathBuilder.append('/').append(keyProviderFolderName.getName());
//        }

        return URLDecoder.decode(folderPathBuilder.toString(), System.getProperty("file.encoding"));
    }
    
    public static Image getImage(String path){
        Image image = null;
		try {
			image = ImageIO.read(ClassLoader.getSystemResourceAsStream(path));
		} catch (IOException e) {
            log.debug("Unable to read locate image file : " + path, e);
		}
        return image;

    }
    

	public static Collection<File> listFilesForFolder(final File folder, String extension) {

		String temp;
		Collection<File> files = new ArrayList<File>();

		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry, extension);
			} else {
				if (fileEntry.isFile()) {
					temp = fileEntry.getName();
					if ((temp.substring(temp.lastIndexOf('.') + 1,temp.length()).toLowerCase()).equals(extension))
						files.add(fileEntry);
				}

			}
		}
		return files;
	}

}
