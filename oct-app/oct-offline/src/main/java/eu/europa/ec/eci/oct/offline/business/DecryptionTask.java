package eu.europa.ec.eci.oct.offline.business;

import eu.europa.ec.eci.oct.export.DataException;
import eu.europa.ec.eci.oct.export.model.*;
import eu.europa.ec.eci.oct.crypto.CipherOperation;
import eu.europa.ec.eci.oct.crypto.CryptoException;
import eu.europa.ec.eci.oct.crypto.Cryptography;
import eu.europa.ec.eci.oct.export.model.SupportForm;
import eu.europa.ec.eci.oct.offline.business.reader.FormattedFileReader;
import eu.europa.ec.eci.oct.offline.business.writer.xml.XmlFileWriter;
import eu.europa.ec.eci.oct.offline.support.crypto.CryptographyHelper;
import eu.europa.ec.eci.oct.offline.support.log.OfflineCryptoToolLogger;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import static eu.europa.ec.eci.oct.offline.business.reader.FormattedFileReaderProvider.getFormattedFileReaderProvider;

/**
 * @author: micleva
 * @created: 11/18/11
 * @project OCT
 */
class DecryptionTask implements Callable<DecryptTaskStatus> {
    private static OfflineCryptoToolLogger log = OfflineCryptoToolLogger.getLogger(DecryptionTask.class.getName());

    private final File fileInSelection;
    private final File selectedInput;
    private final File outputFolder;
    private final FileType outputFileType;

    private boolean isInterrupted = false;
    private int decryptedData = 0;

    /**
     * Cryptography is not thread safe!
     * As such, use one instance per thread
     */
    private Cryptography cryptography = null;

    public DecryptionTask(File fileInSelection, File selectedInput, File outputFolder, FileType outputFileType) {
        this.fileInSelection = fileInSelection;
        this.selectedInput = selectedInput;
        this.outputFolder = outputFolder;
        this.outputFileType = outputFileType;
    }

    @Override
    public DecryptTaskStatus call() throws Exception {

    	
        boolean success = true;
        long startTime = System.currentTimeMillis();
        String additionalMessage = null;
        try {

            FormattedFileReader fileReader = getFormattedFileReaderProvider(FileType.XML);
            SupportForm supportForm = fileReader.readFromFile(fileInSelection);
            
            decryptSupportForm(supportForm);

            if(outputFileType == FileType.PDF){
                AnnexRevisionHelper annexRevisionHelper = new AnnexRevisionHelper();
                Map<AnnexRevisionType,SupportForm> supportFormRevisionMap = annexRevisionHelper.annexRevisionSplitter(supportForm);
                
                if(supportFormRevisionMap.size() == 1){
                	success = write(supportForm);
                }else{
            		cycle:for(SupportForm sp : supportFormRevisionMap.values()){
                		success = write(sp);
                		if(success == false){
                			break cycle;
                		}
            		}
                }            	
            }else{
            	success = write(supportForm);
            }
            
            //the Decryption process has been interrupted
            if(success == false){
                additionalMessage = "Decryption interrupted!";
            }

        } catch (Throwable e) {
        	e.printStackTrace();
            log.debug("Unable to decrypt file: " + fileInSelection.getAbsolutePath(), e);
            success = false;
            additionalMessage = e.getMessage();
        }
        long execTime = System.currentTimeMillis() - startTime;
        if (success) {
            additionalMessage = "Decrypted data: " + decryptedData;
        }

        return new DecryptTaskStatus(fileInSelection.getAbsolutePath(), execTime, success, additionalMessage);
    }
    
    private boolean write(SupportForm supportForm) throws DataException {
		boolean b = false;
		if (!isInterrupted) {
			XmlFileWriter.writeToFile(supportForm, outputFolder.getAbsolutePath(), fileInSelection, selectedInput);
			b = true;
		} else {
			b = false;
		}
		return b;
	}

    private void initCryptography() throws CryptoException {
        if (cryptography == null) {
            cryptography = new Cryptography(CipherOperation.DECRYPT, CryptographyHelper.getKey());
        }
    }

    private void decryptSupportForm(SupportForm supportForm) throws CryptoException {

        //Collection needed to store any dirty Signature entry
        List<SignatureType> signatureTypesToRemove = new ArrayList<SignatureType>();

        initCryptography();

        List<SignatureType> signatureTypes = supportForm.getSignatures().getSignature();

        for (Iterator<SignatureType> iterator = signatureTypes.iterator(); iterator.hasNext() && !verifyInterrupted();) {
            SignatureType signatureType = iterator.next();
            SignatoryType signatoryType = signatureType.getSignatoryInfo();

            List<GroupType> groupList = signatoryType.getGroups().getGroup();
            for (Iterator<GroupType> iterator1 = groupList.iterator(); iterator1.hasNext() && !verifyInterrupted();) {
                GroupType groupType = iterator1.next();
                List<PropertyType> propertyTypeList = groupType.getProperties().getProperty();
                for (Iterator<PropertyType> iterator2 = propertyTypeList.iterator(); iterator2.hasNext() && !verifyInterrupted();) {
                    PropertyType propertyType = iterator2.next();
//                    if (propertyType.getValue() != null && propertyType.getValue().length() == 768) {
                    if (propertyType.getValue() != null) {
                    	try {
                    		propertyType.setValue(decrypt("group.property.key -> " + propertyType.getKey(), propertyType.getValue()));
                    	} catch (DecoderException e1) {
                    		log.log(Level.SEVERE,"Unable to decrypt field: " + propertyType.getKey() + " for signature ID : " + signatureType.getSignatureIdentifier() , e1);
                    	} catch (CryptoException e2) {
                    		log.log(Level.SEVERE,"Unable to decrypt field: " + propertyType.getKey() + " for signature ID : " + signatureType.getSignatureIdentifier() , e2);                    		
                    	}
                    } else {
                    	//don't do anything; maybe it is in clear text?
                    	log.log(Level.SEVERE,"Unable to decrypt field the length is not 512, signature will be skipped (bad data): " + propertyType.getKey() + " signature ID : " + signatureType.getSignatureIdentifier());
                    	//marking signature as bad and will not be reported in the decripted file
                    	signatureTypesToRemove.add(signatureType);
                    	continue;
                   	}
                }
            }
        }
        
        //removing dirty signatures if any
        for(SignatureType signatureType : signatureTypesToRemove){
        	signatureTypes.remove(signatureType);
        }
        
    }

    private boolean verifyInterrupted() {
        isInterrupted |= Thread.interrupted();

        return isInterrupted;
    }

    private String decrypt(String fieldName, String filedValue) throws DecoderException, CryptoException {
        if (isInterrupted) {
            return "";
        }
        try {
            return new String(cryptography.perform(Hex.decodeHex(filedValue.trim().toCharArray())), DecryptConstants.CHARACTER_ENCODING);
        } catch (DecoderException e) {
        	e.printStackTrace();
            throw new DecoderException("Unable to decode field name: " + fieldName + " -> " + e.getMessage(), e);
        } catch (CryptoException e) {
        	e.printStackTrace();
            throw new CryptoException("Unable to decrypt field name: " + fieldName + " -> " + e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
        	e.printStackTrace();
            throw new DecoderException("Unable to decode field name: " + fieldName + " -> " + e.getMessage(), e);
        } finally {
            decryptedData++;
        }
    }
}
