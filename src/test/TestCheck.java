package test;

import util.Util;

import javax.crypto.Cipher;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;

public class TestCheck {

    public static void main(String[] args) throws Exception {
        //读取.LIC文件 写入byte数组
        FileInputStream inputStream = new FileInputStream(new File(Util.getOriginalFileDir(), "Spinner_R2022x_HU8.lic"));
        byte[] bytes = new byte[inputStream.available()];
        int offset = 0;

        for (int numRead; offset < bytes.length && (numRead = inputStream.read(bytes, offset, bytes.length - offset)) >= 0; offset += numRead) {
        }
        inputStream.close();
        //读取privateKey获取key
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

    }

}
