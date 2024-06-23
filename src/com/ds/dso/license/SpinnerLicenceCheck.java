package com.ds.dso.license;

import util.Util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyStore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SpinnerLicenceCheck {
    public static void main(String[] args) {
        try {
            // Step 1: Read the existing encrypted file and decrypt its content
            InputStream encryptedFileStream = new FileInputStream(new File(Util.getOriginalFileDir(), "Spinner_R2022x_HU8.lic"));
            EncryptDecryptMechanism edm = new EncryptDecryptMechanism(Logger.getLogger(EncryptDecryptMechanism.class.getName()));
            String decryptedContent = edm.readEncryptedFile(encryptedFileStream);
            System.out.println("Decrypted Content: " + decryptedContent);

            // Step 2: Modify the decrypted string
            String modifiedString = decryptedContent.replace("31/12/2035", "31/12/2099");
            System.out.println("Modified Content: " + modifiedString);

            // Step 3: Encrypt the modified string
            String encryptedModifiedContent = edm.encryptMessage(modifiedString);
            System.out.println("Encrypted Modified Content: " + encryptedModifiedContent);

            // Step 4: Write the encrypted modified content to a new lic file
            File newLicFile = new File(Util.getOutputFileDir(SpinnerLicenceCheck.class.getName()), "Spinner_R2022x_HU8.lic");
            try (FileOutputStream fos = new FileOutputStream(newLicFile)) {
                byte[] encryptedBytes = EncryptDecryptMechanism.hexStringToByteArray(encryptedModifiedContent);
                fos.write(encryptedBytes);
                System.out.println("New encrypted license file created successfully.");
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Step 5: Test the new lic
            String newLicDecryptedContent = edm.readEncryptedFile(new FileInputStream(newLicFile));
            System.out.println("newLicDecryptedContent = " + newLicDecryptedContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class EncryptDecryptMechanism {
    private Logger loggerObj;

    EncryptDecryptMechanism(Logger logger) {
        this.loggerObj = logger;
    }

    EncryptDecryptMechanism() {
    }

    public String decryptKey(byte[] encrypted) {
        String originalString = null;

        try {
            this.loggerObj.log(Level.INFO, "In function decryptKey");
            ObjectInputStream inputStream = new ObjectInputStream(EncryptDecryptMechanism.class.getResourceAsStream("privateKey.key"));
            Key privateKey = (Key) inputStream.readObject();
            inputStream.close();
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding"); // Ensure padding scheme matches
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] original = cipher.doFinal(encrypted);
            originalString = new String(original);
            this.loggerObj.log(Level.INFO, "originalString = " + originalString);
        } catch (Exception e) {
            this.loggerObj.log(Level.SEVERE, "Error in function decryptKey while decrypting content : ", e);
        }

        this.loggerObj.log(Level.INFO, "End of function decryptKey");
        return originalString;
    }

    private Key getKeyFromStore() {
        this.loggerObj.log(Level.INFO, "In function getKeyFromStore");
        Key keyFromStore = null;
        String StorPass = "3DS#PLP";
        char[] storPass = StorPass.toCharArray();
        String StoreAlias = "DSO";
        String DsoAuthentication = "com/ds/dso/license/DSO.Authenticate";

        try {
            KeyStore keyStore = KeyStore.getInstance("JCEKS");
            InputStream fKeyStore = this.getClass().getClassLoader().getResourceAsStream(DsoAuthentication);
            keyStore.load(fKeyStore, storPass);
            keyFromStore = keyStore.getKey(StoreAlias, storPass);
        } catch (Exception e) {
            this.loggerObj.log(Level.SEVERE, "In function getKeyFromStore, exception occurred:", e);
        }

        this.loggerObj.log(Level.INFO, "End function getKeyFromStore");
        return keyFromStore;
    }

    public String decryptFile(byte[] bytes) {
        return this.decryptKey(bytes);
    }

    public String readEncryptedFile(InputStream inputString) {
        this.loggerObj.log(Level.INFO, "In function readEncryptedFile");
        byte[] bytes;

        try {
            long length = inputString.available();
            bytes = new byte[(int) length];
            int offset = 0;

            for (int numRead; offset < bytes.length && (numRead = inputString.read(bytes, offset, bytes.length - offset)) >= 0; offset += numRead) {
            }

            if (offset < bytes.length) {
                this.loggerObj.log(Level.SEVERE, "Could not completely read License file.");
                throw new IOException("Could not completely read License file.");
            } else {
                inputString.close();
                return this.decryptKey(bytes);
            }
        } catch (Exception e) {
            this.loggerObj.log(Level.SEVERE, "Could not read encrypted file : ", e);
            return "";
        }
    }

    String decryptLicenseKey(String sEncryptedKey) {
        return this.decryptKey(hexStringToByteArray(sEncryptedKey));
    }

    public String encryptMessage(String MessageToEncrypt) {
        this.loggerObj.log(Level.INFO, "In function encryptMessage");
        Key aesKey = this.getKeyFromStore();

        try {
            Cipher cipher = Cipher.getInstance("AES");
            byte[] raw = aesKey.getEncoded();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(MessageToEncrypt.getBytes(StandardCharsets.UTF_8));
            return byteArrayToHexString(encrypted);
        } catch (Exception e) {
            this.loggerObj.log(Level.SEVERE, "In function encryptMessage, Error while encrypting Key", e);
            return "";
        }
    }

    static byte[] hexStringToByteArray(String str) {
        byte[] bytes = new byte[str.length() / 2];

        for (int i = 0; i < bytes.length; ++i) {
            int index = i * 2;
            int var = Integer.parseInt(str.substring(index, index + 2), 16);
            bytes[i] = (byte) var;
        }

        return bytes;
    }

    static String byteArrayToHexString(byte[] bytes) {
        StringBuilder sBuffer = new StringBuilder(bytes.length * 2);

        for (byte aByte : bytes) {
            int var = aByte & 255;
            if (var < 16) {
                sBuffer.append('0');
            }

            sBuffer.append(Integer.toHexString(var));
        }

        return sBuffer.toString().toUpperCase();
    }
}
