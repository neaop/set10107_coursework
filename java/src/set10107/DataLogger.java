package set10107;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

class DataLogger {

    private static String saveDirectory = "data";
    private static FileWriter fw;

    static synchronized void writeData(String string) {
        try {
            fw.write(string);
        } catch (IOException e) {
            System.out.println("Error writing data.");
        }
        try {
            fw.flush();
        } catch (IOException e) {
            System.out.println("Error flushing file");
        }
    }

    static synchronized void writeDetails() {
        writeData(String.format("hidden, geneMin, geneMax, population, mutRate, mutChange \r\n" +
                        "%1d, %2f, %3f, %4d, %5f, %6f \r\n",
                Parameters.numHidden, Parameters.minGene, Parameters.maxGene, Parameters.popSize,
                Parameters.mutateRate, Parameters.mutateChange));
    }

    static void createDataLocation(String dataSet) {
        boolean result = false;
        String dataDirName = String.format("%1$s/%2$s", saveDirectory, dataSet);
        File dataDir = new File(dataDirName);
        if (!dataDir.exists()) {
            result = dataDir.mkdirs();
        }

        if (result) {
            System.out.println("Data directory created.");
        }

        String FILE_PATH = String.format("%1s/%2d_data.csv",
                dataDir.getPath(), System.nanoTime());
        try {
            fw = new FileWriter(FILE_PATH, true);
        } catch (IOException e) {
            System.out.println("Error creating  data location.");
            System.exit(1);
        }
    }

    static void closeFile() {
        try {
            fw.flush();
        } catch (IOException e) {
            System.out.println("Error flushing file.");
            System.exit(1);
        }
        try {
            fw.close();
        } catch (IOException e) {
            System.out.println("Error closing file.");
            System.exit(1);
        }
    }

    static void setSaveDirectory(String saveDirectory) {
        DataLogger.saveDirectory = saveDirectory;
    }

}
