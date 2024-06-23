import util.Util;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileOutputStream;
import java.security.KeyStore;

public class KeyStoreGenerator {
    public static void main(String[] args) throws Exception {
        // Create a KeyStore
        KeyStore keyStore = KeyStore.getInstance("JCEKS");
        keyStore.load(null, null);

        // Generate an AES key
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        SecretKey secretKey = keyGen.generateKey();

        // Set the KeyStore password
        char[] keyStorePassword = "3DS#PLP".toCharArray();

        // Create a KeyStore entry
        KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(keyStorePassword);
        KeyStore.SecretKeyEntry skEntry = new KeyStore.SecretKeyEntry(secretKey);
        keyStore.setEntry("DSO", skEntry, protParam);

        // Save the KeyStore to DSO.Authenticate
        try (FileOutputStream fos = new FileOutputStream(new File(Util.getOutputFileDir(KeyStoreGenerator.class.getName()), "DSO.Authenticate"))) {
            keyStore.store(fos, keyStorePassword);
        }
    }
}
