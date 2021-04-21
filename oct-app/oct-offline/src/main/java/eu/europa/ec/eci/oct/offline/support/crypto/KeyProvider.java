package eu.europa.ec.eci.oct.offline.support.crypto;

import eu.europa.ec.eci.oct.offline.support.Utils;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;

import static eu.europa.ec.eci.oct.offline.support.Utils.getDataFile;

/**
 * @author: micleva
 * @created: 11/22/11
 * @project OCT
 */
public class KeyProvider {
    private static final String OCT_HASH_PASSWORD_FILE = "system_credentials";
    public static final String OCT_HASH_PASSWORD_FILE_EXT = "wac";
    private static final String OCT_KEY_FILE = "public.key";
    private static final String CRYPTO_KEY_FILE = "private.key";
    private static final String CRYPTO_SALT_FILE = "crypto.salt";

    private static final int DIGEST_ITERATIONS = 100000;

    public static void saveCredentials(String username, String salt, String hash) throws Exception {
        String fileName = buildWacFileName(username);
        File file = getDataFile(KeyProviderFolderName.KEYS_TO_SEND_FOLDER_NAME, fileName);
        Collection<String> lines = new ArrayList<>();
        lines.add("Username:" + username);
        lines.add("Salt:" + salt);
        lines.add("Hash:" + hash);
        FileUtils.writeLines(file, "UTF-8", lines, false);
    }

    public static void saveCryptoKeyWithPassword(PrivateKey privateKey, char[] pass) throws Exception {
        byte[] priv = privateKey.getEncoded();

        SecureRandom saltGenerator = new SecureRandom();
        byte[] salt = saltGenerator.generateSeed(256);

        byte[] encBytes = PbeCipher.encrypt(priv, pass, salt, DIGEST_ITERATIONS);

        String hexEnc = new String(Hex.encodeHex(encBytes));

        //write the private key and the salt used for password in external files
        writeBytesToFile(hexEnc.getBytes("UTF-8"), KeyProviderFolderName.KEYS_TO_KEEP_FOLDER_NAME, CRYPTO_KEY_FILE);
        writeBytesToFile(salt, KeyProviderFolderName.KEYS_TO_KEEP_FOLDER_NAME, CRYPTO_SALT_FILE);
    }

    
    public static void saveOctKeyWithPassword(PublicKey publicKey) throws Exception {
        String hexEncoded = new String(Hex.encodeHex(publicKey.getEncoded()));

        //write the private key and the salt used for password in external files
        writeBytesToFile(hexEncoded.getBytes("UTF-8"), KeyProviderFolderName.KEYS_TO_KEEP_FOLDER_NAME, OCT_KEY_FILE);
        writeBytesToFile(hexEncoded.getBytes("UTF-8"), KeyProviderFolderName.KEYS_TO_SEND_FOLDER_NAME, OCT_KEY_FILE);
    }

    public static byte[] loadKeyFromFile(char[] pass) throws Exception {

        //read the encrypted private key bytes and the salt used for password encryption from files
        byte[] keyData = readBytesFromFile(CRYPTO_KEY_FILE);
        byte[] saltData = readBytesFromFile(CRYPTO_SALT_FILE);

        byte[] encryptedKey = Hex.decodeHex(new String(keyData, "UTF-8").toCharArray());

        return PbeCipher.decrypt(encryptedKey, pass, saltData, DIGEST_ITERATIONS);
    }

    private static byte[] readBytesFromFile(String filePath) throws Exception {
        File file = getDataFile(KeyProviderFolderName.KEYS_TO_KEEP_FOLDER_NAME, filePath);

        DataInputStream dis = new DataInputStream(new FileInputStream(file));
        byte[] theData = new byte[(int) file.length()];
        dis.readFully(theData);
        dis.close();
        return theData;
    }

    private static void writeBytesToFile(byte[] bytes, KeyProviderFolderName keyProviderFolderName, String filePath) throws Exception {
    	
        File file = getDataFile(keyProviderFolderName, filePath);
        FileOutputStream fos = new FileOutputStream(file);
        DataOutputStream dos = new DataOutputStream(fos);
        dos.write(bytes);
        dos.close();
    }

    public static boolean keyFileExists() {
        try {
            return getDataFile(KeyProviderFolderName.KEYS_TO_KEEP_FOLDER_NAME, CRYPTO_KEY_FILE).exists();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean keyWacExistsbyUsername(String username) {
        try {
            return getDataFile(KeyProviderFolderName.KEYS_TO_SEND_FOLDER_NAME, buildWacFileName(username)).exists();
        } catch (Exception e) {
            return false;
        }
    }
    public static boolean keyWacsExists() {
        try {
        	boolean b = false; 
            File folder = getDataFile(KeyProviderFolderName.KEYS_TO_SEND_FOLDER_NAME, null);
            Collection<File> files = Utils.listFilesForFolder(folder, KeyProvider.OCT_HASH_PASSWORD_FILE_EXT);
            if(files == null || files.size() > 0){
            	b = true;
            }
            return b;
        } catch (Exception e) {
            return false;
        }
    }
    
    public static String buildWacFileName(String username){
    	return username + "_" + OCT_HASH_PASSWORD_FILE + "." + OCT_HASH_PASSWORD_FILE_EXT;
    }
}
