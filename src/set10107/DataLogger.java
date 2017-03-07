package set10107;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DataLogger {

    private static String dataDirName = "data/";
    private static FileWriter fw;

    /* writes data to output file */

    static synchronized void writeData(String string) throws IOException {
        fw.write(string);
        fw.flush();
    }

    static void createDataLocation() throws IOException {
        boolean result = false;
        File dataDir = new File(dataDirName);
        if (!dataDir.exists()) {
            result = dataDir.mkdirs();
        }

        if (result) {
            System.out.println("Data directory created.");
        }

        String FILE_PATH = String.format("%s/%d_data.csv",
                dataDir.getPath(), System.nanoTime());
        fw = new FileWriter(FILE_PATH, true);
    }

}
