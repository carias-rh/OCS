package eu.europa.ec.eci.oct.offline.support.config;

import eu.europa.ec.eci.oct.offline.support.Utils;
import eu.europa.ec.eci.oct.offline.support.log.OfflineCryptoToolLogger;

import java.io.*;
import java.util.Properties;

/**
 * @author: micleva
 * @created: 4/4/12
 * @project ECI
 */
public class ConfigFileAccessor {

    private static OfflineCryptoToolLogger log = OfflineCryptoToolLogger.getLogger(ConfigFileAccessor.class.getName());

    private final Properties properties;
    private final String resourceName;

    private ConfigFileAccessor(String resourceName) {
        this.resourceName = resourceName;
        this.properties = new Properties();
    }

    private void initialize(String resourceName) {

        InputStream inputStream = Utils.getInternalResource(resourceName);
        loadProperties(inputStream);
    }

    private void loadProperties(InputStream inputStream) {
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            log.debug("Unable to read the config content", e);
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e1) {
                    //do nothing
                }
            }
        }
    }

    public static ConfigFileAccessor getInstance(String resourceName) {
        ConfigFileAccessor configFileAccessor = new ConfigFileAccessor(resourceName);
        configFileAccessor.initialize(resourceName);

        return configFileAccessor;
    }

    private void initialize(File file) {
        if (file.exists()) {
            try {
                loadProperties(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                //do nothing
            }
        }
    }

    public static ConfigFileAccessor getInstance(File file) {
        ConfigFileAccessor configFileAccessor = new ConfigFileAccessor(file.getAbsolutePath());
        configFileAccessor.initialize(file);

        return configFileAccessor;
    }

    public int getIntegerValue(String propertyName, int defaultValue) {
        String configValue = getPropertyValue(propertyName);

        int resultValue = defaultValue;
        if (configValue != null) {
            try {
                resultValue = Integer.valueOf(configValue);
            } catch (NumberFormatException e) {
                //ignore as the default value will be used in this case
                log.debug("Not a number value for propertyName:" + propertyName, e);
            }
        }

        return resultValue;
    }

    public Boolean getBooleanValue(String propertyName, boolean defaultValue) {
        String configValue = getPropertyValue(propertyName);

        boolean resultValue = defaultValue;
        if (configValue != null) {
            resultValue = Boolean.valueOf(configValue);
        }

        return resultValue;
    }

    public String getStringValue(String propertyName) {
        return getPropertyValue(propertyName);
    }

    private String getPropertyValue(String propertyName) {
        return properties != null ? properties.getProperty(propertyName) : null;
    }

    public boolean writeProperty(String propertyName, String propertyValue) {
        boolean success = true;
        try {
            properties.setProperty(propertyName, propertyValue);
            FileOutputStream outputStream = new FileOutputStream(resourceName);
            properties.store(outputStream, null);

            outputStream.flush();
            outputStream.close();

        } catch (Exception e) {
            log.warning("Unable to write property: " + propertyName + "; value = " + propertyValue, e);
            success = false;
        }
        return success;
    }
}
