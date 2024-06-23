package check;

import javax.crypto.Cipher;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.security.Key;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 安装流程
 */

public class CheckInstallLicence {


    public static void main(String[] args) throws Exception {

        // setting env veriable sCommand
        String sDBUser = "";
        String mCommand = "add property DSODBUser on program emxReadSpinnerAgent value \"" + sDBUser + "\"";

        // 查找.lic，
        // 确定lic文件名是否正确
        //读取.LIC文件 写入byte数组
        FileInputStream inputStream = new FileInputStream("Spinner_R2022x_HU8.lic");
        byte[] bytes = new byte[inputStream.available()];
        int offset = 0;

        for (int numRead; offset < bytes.length && (numRead = inputStream.read(bytes, offset, bytes.length - offset)) >= 0; offset += numRead) {
        }

        //读取privateKey，比对
        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("privateKey.key"));
        Key privateKey = (Key) objectInputStream.readObject();
        System.out.println("privateKey.getAlgorithm() = " + privateKey.getAlgorithm());
//        System.out.println("privateKey.getEncoded() = " + privateKey.getEncoded());
//        System.out.println("privateKey.getEncoded() = " + new String(privateKey.getEncoded()));
        System.out.println("privateKey.getFormat() = " + privateKey.getFormat());
        System.out.println(privateKey);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(2, privateKey);
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


        // buildKey
        String sKeyPrefix = "";
        String sKeyValue = "";
        String sBuildKey;

        String key;
        for (Iterator<String> var7 = sKeyDetails.keySet()
                .iterator(); var7.hasNext(); sKeyValue = sKeyValue + "!" + sKeyDetails.get(key)) {
            key = var7.next();
            sKeyPrefix = sKeyPrefix + key;
        }

        sBuildKey = sKeyPrefix + sKeyValue;
        //set env
        String sLicenseKey = "set env DSOEncryptedLicenseKey \"" + sBuildKey + "\"";


    }

}
