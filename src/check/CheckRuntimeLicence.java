package check;

import util.Util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CheckRuntimeLicence {

    public static void main(String[] args) throws Exception {

        //读取.LIC文件 写入byte数组
//        FileInputStream inputStream = new FileInputStream(new File(Util.getOriginalFileDir(), "Spinner_R2022x_HU8.lic"));
        FileInputStream inputStream = new FileInputStream(new File(Util.getOriginalFileDir(), "Spinner_R2022x_HU8.lic"));
        byte[] bytes = new byte[inputStream.available()];
        int offset = 0;

        for (int numRead; offset < bytes.length && (numRead = inputStream.read(bytes, offset, bytes.length - offset)) >= 0; offset += numRead) {
        }
        inputStream.close();
        //读取privateKey,对lic文件内容（byte数组）进行解密得到加密的内容
        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(new File(Util.getOriginalFileDir(), "privateKey.key")));
        Key privateKey = (Key) objectInputStream.readObject();
        System.out.println("privateKey.getAlgorithm() = " + privateKey.getAlgorithm());
        System.out.println("privateKey.getFormat() = " + privateKey.getFormat());
        System.out.println(privateKey);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] original = cipher.doFinal(bytes);

        String originalString = new String(original);
        System.out.println("originalString = " + originalString);

        //组成sKeyDetails
        String[] sKey = originalString.split("!");
        String prefix = sKey[0];
        Map<String, String> sKeyDetails = new HashMap<>();
        for (int i = 1; i < sKey.length; ++i) {
            sKeyDetails.put(Character.toString(prefix.charAt(i - 1)), sKey[i]);
        }
        System.out.println("sKeyDetails = " + sKeyDetails); //sKeyDetails = {A=Mathieu DUTHOIT, C=HU8, D=HU8, E=31/12/2035, N=ENOVIA Schema Agent, P=, S=21/11/2021, T=0, U=Yes, V=R2022x, W=, X=none, Y=Internal}

//        // 修改E字段值为31/12/2099
        sKeyDetails.put("E", "31/12/2099");

        String newLic = modify(prefix, sKeyDetails, cipher, privateKey);

        testNewLic(newLic);

    }

    private static String modify(String prefix, Map<String, String> sKeyDetails, Cipher cipher, Key privateKey) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
        // 重新组合字符串
        StringBuilder modifiedString = new StringBuilder(prefix);
        for (int i = 1; i <= prefix.length(); ++i) {
            String key = Character.toString(prefix.charAt(i - 1));
            modifiedString.append("!").append(sKeyDetails.get(key));
        }

        System.out.println("modifiedString = " + modifiedString);

        // 加密字符串
        cipher.init(2, privateKey);  // 使用相应的公钥
        byte[] modifiedBytes = cipher.doFinal(modifiedString.toString().getBytes());

        // 将加密的字节数组写入新的许可文件
        File newLic = new File(Util.getOutputFileDir(CheckRuntimeLicence.class.getName()), "Spinner_R2022x_HU8.lic");
        FileOutputStream outputStream = new FileOutputStream(newLic);
        outputStream.write(modifiedBytes);
        outputStream.close();

        return newLic.getAbsolutePath();
    }

    private Key getKeyFromStore() {
        Key keyFromStore = null;
        String StorPass = "3DS#PLP";
        char[] storPass = StorPass.toCharArray();
        String StoreAlias = "DSO";
        String DsoAuthentication = "com/ds/dso/license/DSO.Authenticate";

        try {
            KeyStore keyStore = KeyStore.getInstance("JCEKS");
            InputStream fKeyStore = this.getClass().getClassLoader().getResourceAsStream(DsoAuthentication);
            if (fKeyStore != null && fKeyStore.available() > 0) {
                keyStore.load(fKeyStore, storPass);
                fKeyStore.close();
            }

            keyFromStore = keyStore.getKey(StoreAlias, storPass);
        } catch (Exception ignored) {
        }

        return keyFromStore;
    }

    public String encryptKey(HashMap<String, String> sKeyDetails, String sLoggerPath) {
        String sKeyToEncrypt = "";
        String sKeyPrefix = "";
        String sKeyValue = "";
        String sEncryptedKey = "";

        String key;
        for (Iterator<String> var8 = sKeyDetails.keySet()
                .iterator(); var8.hasNext(); sKeyValue = sKeyValue + "!" + sKeyDetails.get(key)) {
            key = var8.next();
            sKeyPrefix = sKeyPrefix + key;
        }

        sKeyToEncrypt = sKeyPrefix + sKeyValue;
        sEncryptedKey = this.encryptMessage(sKeyToEncrypt);
        return sEncryptedKey;
    }

    public String encryptMessage(String MessageToEncrypt) {
        Key aeskey = this.getKeyFromStore();

        try {
            Cipher cipher = Cipher.getInstance("AES");
            byte[] raw = aeskey.getEncoded();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            cipher.init(1, skeySpec);
            byte[] encrypted = cipher.doFinal(MessageToEncrypt.getBytes(StandardCharsets.UTF_8));
            String byteArrayToHexString = byteArrayToHexString(encrypted);
            System.out.println("byteArrayToHexString = " + byteArrayToHexString);
            return byteArrayToHexString;
        } catch (Exception var7) {
            return "";
        }
    }

    private static String byteArrayToHexString(byte[] bytes) {
        StringBuffer sBuffer = new StringBuffer(bytes.length * 2);

        for (byte aByte : bytes) {
            int var = aByte & 255;
            if (var < 16) {
                sBuffer.append('0');
            }
            sBuffer.append(Integer.toHexString(var));
        }

        return sBuffer.toString().toUpperCase();
    }

    private static void testNewLic(String licFile) throws Exception {

        //读取.LIC文件 写入byte数组
        FileInputStream inputStream = new FileInputStream(licFile);
        byte[] bytes = new byte[inputStream.available()];
        int offset = 0;

        for (int numRead; offset < bytes.length && (numRead = inputStream.read(bytes, offset, bytes.length - offset)) >= 0; offset += numRead) {
        }

        //读取privateKey获取key
        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("privateKey.key"));
        Key privateKey = (Key) objectInputStream.readObject();
        System.out.println("privateKey.getAlgorithm() = " + privateKey.getAlgorithm());
        System.out.println("privateKey.getFormat() = " + privateKey.getFormat());
        System.out.println(privateKey);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] original = cipher.doFinal(bytes);

        String originalString = new String(original);
        System.out.println("originalString = " + originalString);

        //组成sKeyDetails
        String[] sKey = originalString.split("!");
        String prefix = sKey[0];
        Map<String, String> sKeyDetails = new HashMap<>();
        for (int i = 1; i < sKey.length; ++i) {
            sKeyDetails.put(Character.toString(prefix.charAt(i - 1)), sKey[i]);
        }
        System.out.println("sKeyDetails = " + sKeyDetails);
    }

}
