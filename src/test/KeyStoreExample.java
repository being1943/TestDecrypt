package test;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.FileOutputStream;
import java.security.KeyStore;

public class KeyStoreExample {
    public static void main(String[] args) throws Exception {
        // Create a KeyStore
        KeyStore keyStore = KeyStore.getInstance("JCEKS");
        keyStore.load(null, null);

        // Generate an AES key
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        SecretKey secretKey = keyGen.generateKey();

        // Create a KeyStore entry
        KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection("3DS#PLP".toCharArray());
        KeyStore.SecretKeyEntry skEntry = new KeyStore.SecretKeyEntry(secretKey);
        keyStore.setEntry("DSO", skEntry, protParam);

        // Store the KeyStore
        try (FileOutputStream fos = new FileOutputStream("DSO.Authenticate")) {
            keyStore.store(fos, "3DS#PLP".toCharArray());
        }
    }
}
