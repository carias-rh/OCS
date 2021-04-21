package eu.europa.ec.eci.oct.offline.startup;

import eu.europa.ec.eci.oct.offline.support.Utils;
import eu.europa.ec.eci.oct.offline.support.config.ConfigFileAccessor;
import eu.europa.ec.eci.oct.offline.support.crypto.CryptographyHelper;
import eu.europa.ec.eci.oct.offline.support.crypto.KeyProviderFolderName;
import eu.europa.ec.eci.oct.offline.support.log.OfflineCryptoToolLogger;
import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;

/**
 * @author: micleva
 * @created: 11/30/11
 * @project OCT
 */
public class CryptoOfflineConfig {
    private static OfflineCryptoToolLogger log = OfflineCryptoToolLogger.getLogger(CryptoOfflineConfig.class.getName());
    private final static String SYSTEM_CONFIG_FILE_NAME = "cryptToolConfiguration.properties";
    private final static String EXTERNAL_USER_CONFIG_FILE_NAME = "conf.ini";

    private static CryptoOfflineConfig instance;

    private ConfigFileAccessor systemConfigurations;
    private ConfigFileAccessor userConfigurations;

    public static CryptoOfflineConfig getInstance() {
        if (instance == null) {
            instance = new CryptoOfflineConfig();
        }
        return instance;
    }

    public CryptoOfflineConfig() {
        //read the system config file. We know for sure that this file exist
        systemConfigurations = ConfigFileAccessor.getInstance(SYSTEM_CONFIG_FILE_NAME);

        //check the user file, which might not exist (the user controls it)
        try {
            userConfigurations = ConfigFileAccessor.getInstance(Utils.getDataFile(KeyProviderFolderName.KEYS_TO_KEEP_FOLDER_NAME, EXTERNAL_USER_CONFIG_FILE_NAME));
        } catch (UnsupportedEncodingException e) {
            //do nothing..
        }
    }

    public boolean getBooleanConfigValue(ConfigProperty configProperty, boolean defaultValue) {
        return systemConfigurations.getBooleanValue(configProperty.getKey(), defaultValue);
    }

    public int getIntegerConfigValue(ConfigProperty configProperty, int defaultValue) {
        return systemConfigurations.getIntegerValue(configProperty.getKey(), defaultValue);
    }

    public String getStringUserConfigValue(UserConfigProperty configProperty) {
        String result = userConfigurations.getStringValue(configProperty.getKey());
        if(isSecuredProperty(configProperty)) {
            //decrypt the value
            try {
                result = decryptPropertyValue(result);
            } catch (Exception e) {
                log.warning("Unable to read encrypted value for property: " + configProperty.getKey());
                result = "";
            }
        }

        return result;
    }

    public void writeUserConfigValue(UserConfigProperty configProperty, String value) {
        try {
            if(isSecuredProperty(configProperty)) {
                value = encryptPropertyValue(value);
            }
            //for user configurations, encrypt the config value in order to avoid direct config file modification
            userConfigurations.writeProperty(configProperty.getKey(), value);
        } catch (Exception e) {
            log.warning("Unable to store encrypted value for property: " + configProperty.getKey());
        }
    }

    private boolean isSecuredProperty(UserConfigProperty configProperty) {
        return configProperty.equals( UserConfigProperty.LAST_OUTPUT_FOLDER) ||
                configProperty.equals( UserConfigProperty.LAST_SELECT_FOLDER);
    }

    private String encryptPropertyValue(String value) throws Exception {
        byte[] encBytes = CryptographyHelper.fastEncrypt(value.getBytes("UTF-8"));
        return new String(Hex.encodeHex(encBytes));
    }

    private String decryptPropertyValue(String value) throws Exception {
        byte[] encBytes = Hex.decodeHex(value.toCharArray());
        byte[] decodedBytes = CryptographyHelper.fastDecrypt(encBytes);
        return new String(decodedBytes, "UTF-8");
    }
}
