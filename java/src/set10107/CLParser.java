package set10107;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import java.io.IOException;

class CLParser {

    enum SelectionType {
        RANDOM, ELITE, TOURNAMENT, ROULETTE
    }

    enum CrossoverType {
        SINGLE, DOUBLE, UNIFORM, CLONE
    }

    enum MutationType {
        BOUNDARY, FLIP, NONE
    }

    private static ArgumentParser parser;

    private static Namespace res;

    static void initParser() {
        parser = ArgumentParsers.newArgumentParser("set10107_coursework");
        parser.description("A genetic algorithm to train a neural network.");
        parser.addArgument("data-set")
                .type(String.class)
                .choices("A", "B", "C");
        parser.addArgument("--selection", "-s")
                .type(SelectionType.class)
                .setDefault(SelectionType.RANDOM);
        parser.addArgument("--crossover", "-c")
                .type(CrossoverType.class)
                .setDefault(CrossoverType.CLONE);
        parser.addArgument("--mutation", "-m")
                .type(MutationType.class)
                .setDefault(MutationType.NONE);
        parser.addArgument("--mutation-rate", "--mr", "-r")
                .type(Double.class)
                .setDefault(0.01);
        parser.addArgument("--mutation-change", "--mc")
                .type(Double.class)
                .setDefault(0.01);
        parser.addArgument("--hidden-layer", "--hl")
                .type(Integer.class)
                .setDefault(5);
        parser.addArgument("--population", "-p")
                .type(Integer.class)
                .setDefault(50);
        parser.addArgument("--max-gene", "--max")
                .type(Double.class)
                .setDefault(5.0);
        parser.addArgument("--min-gene", "--min")
                .type(Double.class)
                .setDefault(-5.0);
        parser.addArgument("--seed")
                .type(Integer.class)
                .setDefault(999);
        parser.addArgument("--tournament-size", "--ts")
                .type(Integer.class)
                .setDefault(2);
        parser.addArgument("--save-directory", "--sd")
                .type(String.class)
                .setDefault("data");
        parser.addArgument("--immigrant", "-i")
                .action(Arguments.storeFalse());
    }

    static void parseArgs(String[] args) {
        try {
            res = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            System.out.println("Error parsing args.");
            System.exit(1);
        }
        try {
            setArgs();
        } catch (IOException e) {
            System.out.println("Error setting args.");
            System.exit(1);
        }
    }

    private static void setArgs() throws IOException {
        Parameters.setDataSet(res.get("data_set").toString());
        Parameters.maxGene = (double) res.get("max_gene");
        Parameters.minGene = (double) res.get("min_gene");
        Parameters.seed = (int) res.get("seed");
        Parameters.numHidden = (int) res.get("hidden_layer");
        Parameters.mutateRate = res.get("mutation_rate");
        Parameters.mutateChange = res.get("mutation_change");
        Parameters.popSize = res.get("population");
        Parameters.immigration=res.get("immigrant");
        EvolutionaryTrainer.selection = res.get("selection");
        EvolutionaryTrainer.mutation = res.get("mutation");
        EvolutionaryTrainer.cross = res.get("crossover");
        DataLogger.setSaveDirectory(res.get("save_directory"));
        DataLogger.createDataLocation(res.get("data_set"));

        DataLogger.writeData(res.toString() + "\n");
    }

}
