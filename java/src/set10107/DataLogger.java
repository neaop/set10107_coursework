package set10107;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DataLogger {

    private static FileWriter fw;

    static synchronized void writeData(String string) throws IOException {
        fw.write(string);
        fw.flush();
    }

    static synchronized void writeDetials() throws IOException {
        writeData(String.format("hidden, geneMin, geneMax, population, mutRate, mutChange \r\n" +
                        "%1d, %2f, %3f, %4d, %5f, %6f \r\n",
                Parameters.numHidden, Parameters.minGene, Parameters.maxGene, Parameters.popSize,
                Parameters.mutateRate, Parameters.mutateChange));

    }

    static void createDataLocation(String dataSet) throws IOException {
        boolean result = false;
        String dataDirName = String.format("data/%s", dataSet);
        File dataDir = new File(dataDirName);
        if (!dataDir.exists()) {
            result = dataDir.mkdirs();
        }

        if (result) {
            System.out.println("Data directory created.");
        }

        String FILE_PATH = String.format("%1s/%2d_data.csv",
                dataDir.getPath(), System.nanoTime());
        fw = new FileWriter(FILE_PATH, true);
    }

    static void closeFile() throws IOException {
        fw.flush();
        fw.close();
    }

}
