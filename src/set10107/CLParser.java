package set10107;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;


/**
 * Created by Sam on 15/03/2017.
 */
public class CLParser {

    public enum SelectionType {
        RANDOM, ELITE, TOURNAMENT, ROULETTE
    }

    public enum CrossoverType {
        SINGLE, DOUBLE, UNIFORM, CLONE
    }

    public enum MutationType {
        BOUNDARY, FLIP, NONE
    }


    public static ArgumentParser initParser() {
        ArgumentParser parser = ArgumentParsers.newArgumentParser("set10107_coursework");
        parser.description("A genetic algorithm to train a neural network.");
        parser.addArgument("dataset")
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
        parser.addArgument("--mutation-rate", "--mr")
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
        parser.addArgument("--max-gene","--max")
                .type(Double.class)
                .setDefault(5);
        parser.addArgument("--min-gene","--min")
                .type(Double.class)
                .setDefault(-5);
        parser.addArgument("--seed")
                .type(Integer.class)
                .setDefault(999);
        return parser;
    }
}
