package set10107;

import set10107.data.Reader;

import java.util.Random;

public class Parameters {

    static int numInput = 1;
    static int numHidden = 6;
    static int numOutput = 1;
    final static double minGene = -5; // specifies minimum and maximum weight values
    final static double maxGene = 5;
    final static int popSize = 50;
    final static int maxGeneration = 10000;
    final static double exitError = 0.0; // terminate if MSE smaller than this value
    final static double mutateRate = 0.20; // mutation rate for mutation operator
    final static double mutateChange = 0.01; // mutation rate for mutation operator
    static long seed = 999;
    static double[][] trainData;
    static double[][] testData;
    static int numGenes;
    final static Random random = new Random(seed);

    static void setDataSet(String dataSet) {
        trainData = Reader.getTrainingData(dataSet);
        testData = Reader.getTestData(dataSet);
        if (dataSet.equals("C")) {
            numInput = 2;
        }
    }

    public String toString() {
        return String.format("hidden, geneMin, geneMax, population, mutRate, mutChange, seed \r\n" +
                        "%1d, %2f, %3f, %4d, %5f, %6f, %7d",
                numHidden, minGene, maxGene, popSize, mutateRate, mutateChange, seed);
    }
}
