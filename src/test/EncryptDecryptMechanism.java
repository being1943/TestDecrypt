//package test;
//
//import util.Util;
//
//import javax.crypto.Cipher;
//import javax.crypto.spec.SecretKeySpec;
//import java.io.*;
//import java.nio.charset.StandardCharsets;
//import java.security.Key;
//import java.security.KeyStore;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//public class EncryptDecryptMechanism {
//    private Logger loggerObj;
//
//    EncryptDecryptMechanism(Logger logger) {
//        this.loggerObj = logger;
//    }
//
//    public String decryptKey(byte[] encrypted) {
//        String originalString = null;
//
//        try {
//            this.loggerObj.log(Level.INFO, "In function decryptKey");
//            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(new File(Util.getOriginalFileDir(), "privateKey.key")));
//            Key privateKey = (Key) inputStream.readObject();
//            inputStream.close();
//            Cipher cipher = Cipher.getInstance("RSA");
//            cipher.init(2, privateKey);
//            byte[] original = cipher.doFinal(encrypted);
//            originalString = new String(original);
//            this.loggerObj.log(Level.INFO, "decryptKey.originalString = " + originalString);
//        } catch (Exception var7) {
//            this.loggerObj.log(Level.SEVERE, "Error in function decryptKey while decrypting content : ", var7);
//        }
//
//        this.loggerObj.log(Level.INFO, "End of function decryptKey");
//        return originalString;
//    }
//
//    private Key getKeyFromStore() {
//        this.loggerObj.log(Level.INFO, "In function getKeyFromStore");
//        Key keyFromStore = null;
//        String StorPass = "3DS#PLP";
//        char[] storPass = StorPass.toCharArray();
//        String StoreAlias = "DSO";
//        String DsoAuthentication = "com/ds/dso/license/DSO.Authenticate";
//
//        try {
//            KeyStore keyStore = KeyStore.getInstance("JCEKS");
//            InputStream fKeyStore = new FileInputStream(new File(Util.getOriginalFileDir(), "DSO.Authenticate"));
//            if (fKeyStore.available() > 0) {
//                keyStore.load(fKeyStore, storPass);
//                fKeyStore.close();
//            }
//
//            keyFromStore = keyStore.getKey(StoreAlias, storPass);
//        } catch (Exception var8) {
//            this.loggerObj.log(Level.SEVERE, "In function getKeyFromStore, exception occured:", var8);
//        }
//
//        this.loggerObj.log(Level.INFO, "End function getKeyFromStore");
//        return keyFromStore;
//    }
//
//    public String decryptFile(byte[] bytes) throws Exception {
//        return this.decryptKey(bytes);
//    }
//
//    public String readEncryptedFile(InputStream inputString) throws Exception {
//        this.loggerObj.log(Level.INFO, "In function readEncryptedFile");
//        byte[] bytes;
//
//        try {
//            long length = inputString.available();
//            bytes = new byte[(int) length];
//            int offset = 0;
//
//            for (int numRead; offset < bytes.length && (numRead = inputString.read(bytes, offset, bytes.length - offset)) >= 0; offset += numRead) {
//            }
//
//            if (offset < bytes.length) {
//                this.loggerObj.log(Level.SEVERE, "Could not completely read License file.");
//                throw new IOException("Could not completely read License file.");
//            } else {
//                inputString.close();
//                return this.decryptKey(bytes);
//            }
//        } catch (Exception var7) {
//            this.loggerObj.log(Level.SEVERE, "Could not read encrypted file : ", var7);
//            return "";
//        }
//    }
//
//    String decryptLicenseKey(String sEncryptedKey) {
//        return this.decryptKey(hexStringToByteArray(sEncryptedKey));
//    }
//
//    public String encryptMessage(String MessageToEncrypt) {
//        this.loggerObj.log(Level.INFO, "In function encryptMessage");
//        Key aeskey = this.getKeyFromStore();
//
//        try {
//            Cipher cipher = Cipher.getInstance("AES");
//            byte[] raw = aeskey.getEncoded();
//            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
//            cipher.init(1, skeySpec);
//            byte[] encrypted = cipher.doFinal(MessageToEncrypt.getBytes(StandardCharsets.UTF_8));
//            String byteArrayToHexString = byteArrayToHexString(encrypted);
//            this.loggerObj.log(Level.INFO, "encryptMessage.byteArrayToHexString = " + byteArrayToHexString);
//            return byteArrayToHexString;
//        } catch (Exception var7) {
//            this.loggerObj.log(Level.SEVERE, "In function encryptMessage, Error while encrypting Key", var7);
//            return "";
//        }
//    }
//
//    static byte[] hexStringToByteArray(String str) {
//        byte[] bytes = new byte[str.length() / 2];
//
//        for (int i = 0; i < bytes.length; ++i) {
//            int index = i * 2;
//            int var = Integer.parseInt(str.substring(index, index + 2), 16);
//            bytes[i] = (byte) var;
//        }
//
//        return bytes;
//    }
//
//    private static String byteArrayToHexString(byte[] bytes) {
//        StringBuilder sBuffer = new StringBuilder(bytes.length * 2);
//
//        for (byte aByte : bytes) {
//            int var = aByte & 255;
//            if (var < 16) {
//                sBuffer.append('0');
//            }
//            sBuffer.append(Integer.toHexString(var));
//        }
//
//        return sBuffer.toString().toUpperCase();
//    }
//}
