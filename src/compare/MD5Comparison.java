package compare;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Comparison {

    public static String getMD5Checksum(String filepath) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        try (FileInputStream fis = new FileInputStream(filepath)) {
            byte[] dataBytes = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, bytesRead);
            }
        }
        byte[] mdBytes = md.digest();
        // Convert the byte array to a hexadecimal string
        StringBuilder sb = new StringBuilder();
        for (byte b : mdBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String file1 = "C:\\Users\\caps\\Desktop\\SpinnerTest\\from_lib\\SpinnerBuild\\com\\ds\\dso\\license\\DSO.Authenticate";
        String file2 = "C:\\Users\\caps\\Desktop\\SpinnerTest\\fromexe\\SpinnerBuild\\com\\ds\\dso\\license\\DSO.Authenticate";

        try {
            String md5File1 = getMD5Checksum(file1);
            String md5File2 = getMD5Checksum(file2);

            System.out.println("MD5 of file 1: " + md5File1);
            System.out.println("MD5 of file 2: " + md5File2);

            if (md5File1.equals(md5File2)) {
                System.out.println("The files are identical.");
            } else {
                System.out.println("The files are different.");
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
