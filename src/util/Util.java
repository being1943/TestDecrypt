package util;

import java.io.File;

public class Util {

    public static String getOutputFileDir(String path) {
        File file;
        if (path != null && !path.isEmpty()) {
            file = new File("C:\\git\\TestDecrypt\\output_files", path);
        } else file = new File("C:\\git\\TestDecrypt\\output_files");
        file.mkdirs();
        return file.getAbsolutePath();
    }

    public static String getOriginalFileDir() {
        return new File("C:\\git\\TestDecrypt\\original_files").getAbsolutePath();
    }
}
