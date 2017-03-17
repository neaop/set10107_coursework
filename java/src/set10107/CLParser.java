package set10107;

import net.sourceforge.argparse4j.ArgumentParsers;
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
                .type(Integer.class)
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
                .setDefault(5);
        parser.addArgument("--min-gene", "--min")
                .type(Double.class)
                .setDefault(-5);
        parser.addArgument("--seed")
                .type(Integer.class)
                .setDefault(999);
        parser.addArgument("--tournament-size", "--ts")
                .type(Integer.class)
                .setDefault(2);
    }

    static void parseArgs(String[] args) throws IOException {
        try {
            res = parser.parseArgs(args);
            setArgs();
        } catch (ArgumentParserException e) {
            e.printStackTrace();
        }
    }

    private static void setArgs() throws IOException {
        Parameters.setDataSet(res.get("data-set"));
        Parameters.maxGene = res.get("max");
        Parameters.minGene = res.get("min");
        Parameters.seed = res.get("seed");
        Parameters.numHidden = res.get("hl");
        Parameters.mutateRate = res.get("mr");
        Parameters.mutateChange = res.get("mc");
        Parameters.popSize = res.get("p");
        EvolutionaryTrainer.selection = res.get("s");
        EvolutionaryTrainer.mutation = res.get("m");
        EvolutionaryTrainer.cross = res.get("c");
        DataLogger.createDataLocation(res.get("data-set"));
    }

}
