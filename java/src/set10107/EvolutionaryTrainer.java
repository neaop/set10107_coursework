package set10107;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class EvolutionaryTrainer extends NeuralNetwork {

    static CLParser.CrossoverType cross;
    static CLParser.MutationType mutation;
    static CLParser.SelectionType selection;

    private EvolutionaryTrainer() {
        super();
    }

    public static void main(String[] args) {
        CLParser.initParser();
        CLParser.parseArgs(args);
        DataLogger.writeData("iteration,generation,seed,best\r\n");

        int iteration;

        for (iteration = 0; iteration < 50; ++iteration) {
            System.out.println("\nIteration: " + iteration);
            Parameters.seed++;
            EvolutionaryTrainer nn = new EvolutionaryTrainer();

            double[] bestWeights = nn.train(iteration);
            System.out.println("Training complete");

            nn.setWeights(bestWeights);
            double trainAcc = nn.testNetwork(Parameters.trainData);
            System.out.println("Accuracy on training data = " + trainAcc);

            double testAcc = nn.testNetwork(Parameters.testData);
            System.out.println("Accuracy on test data = " + testAcc);
            // System.out.println("\nEnd NN training demo");
            DataLogger.writeData(iteration + ",final," + Parameters.seed + "," + testAcc + "\r\n");
        }
        DataLogger.closeFile();
    }

    private double[] train(int iteration) {
        Individual[] population = initialise();
        Individual bestIndividual = getBest(population);

        int gen = 0;
        Individual parent1 = null;
        Individual parent2 = null;
        boolean done = false;
        while (gen < Parameters.maxGeneration && !done) {
            DataLogger.writeData(iteration + "," + gen + "," + Parameters.seed + "," + bestIndividual.error + "\r\n");

            switch (selection) {
                case RANDOM:
                    parent1 = selectRandom(population);
                    parent2 = selectRandom(population);
                case ELITE:
                    parent1 = selectElite(population, 0);
                    parent2 = selectElite(population, 1);
                case ROULETTE:
                    parent1 = selectRoulette(population);
                    parent2 = selectRoulette(population);
                case TOURNAMENT:
                    parent1 = selectTournament(population, Parameters.tournamentSize);
                    parent2 = selectTournament(population, Parameters.tournamentSize);
            }

            Individual[] children = null;
            switch (cross) {
                case CLONE:
                    children = crossoverClone(parent1, parent2);
                case UNIFORM:
                    children = crossoverUniform(parent1, parent2);
                case SINGLE:
                    children = crossoverSingle(parent1, parent2);
                case DOUBLE:
                    children = crossoverDouble(parent1, parent2);
            }

            switch (mutation) {
                case NONE:
                case FLIP:
                case BOUNDARY:
                    mutateBoundaryMultiply(children);
            }

            evaluateIndividuals(children);

            replace(children[0], children[1], population);

            if(Parameters.immigration) {
                injectImmigrant(population);
            }

            bestIndividual = getBest(population);

            if (bestIndividual.error < Parameters.exitError) {
                done = true;
            }
            ++gen;
        }
        return bestIndividual.chromosome;
    }

    private void evaluateIndividuals(Individual[] individuals) {
        for (Individual individual : individuals) {
            individual.error = meanSquaredError(Parameters.trainData, individual.chromosome);
        }
    }

    private Individual getBest(Individual[] population) {
        Individual bestIndividual = null;
        for (Individual individual : population) {
            if (bestIndividual == null) {
                bestIndividual = individual.copy();
            } else if (individual.error < bestIndividual.error) {
                bestIndividual = individual.copy();
            }
        }
        return bestIndividual;
    }

    private Individual[] initialise() {
        Individual[] population = new Individual[Parameters.popSize];
        for (int i = 0; i < population.length; ++i) {
            population[i] = new Individual();
            population[i].error = meanSquaredError(Parameters.trainData, population[i].chromosome);
        }
        return population;
    }

    private Individual selectRandom(Individual[] population) {
        int popSize = population.length;
        return population[Parameters.random.nextInt(popSize)].copy();
    }

    private Individual selectElite(Individual[] population, int index) {
        Arrays.sort(population);
        return population[index].copy();
    }

    private Individual selectTournament(Individual[] population, int tournamentSize) {
        int popSize = population.length;
        ArrayList<Individual> tournament = new ArrayList<>();
        for (int i = 0; i < tournamentSize; ++i) {
            tournament.add(population[Parameters.random.nextInt(popSize)].copy());
        }
        Collections.sort(tournament);
        return tournament.get(0).copy();
    }

    private Individual selectRoulette(Individual[] population) {
        double errorSum = 0;
        int chosenIndex = population.length - 1;
        Arrays.sort(population);

        for (Individual ind : population) {
            errorSum += ind.error;
        }
        double randomValue = Parameters.random.nextDouble() * errorSum;
        for (int i = 0; i < population.length; ++i) {
            randomValue -= population[i].error;
            if (randomValue <= 0) {
                chosenIndex = i;
            }
        }
        return population[chosenIndex].copy();
    }

    private Individual[] crossoverSingle(Individual parent1, Individual parent2) {
        Individual child1 = new Individual();
        Individual child2 = new Individual();
        int numGenes = parent1.chromosome.length;
        int cross = Parameters.random.nextInt(numGenes);

        for (int i = 0; i < cross; ++i) {
            child1.chromosome[i] = parent1.chromosome[i];
            child2.chromosome[i] = parent2.chromosome[i];
        }
        for (int i = cross; i < numGenes; ++i) {
            child1.chromosome[i] = parent2.chromosome[i];
            child2.chromosome[i] = parent1.chromosome[i];
        }

        Individual[] result = new Individual[2];
        result[0] = child1;
        result[1] = child2;

        return result;
    }

    private Individual[] crossoverDouble(Individual parent1, Individual parent2) {
        Individual child1 = new Individual();
        Individual child2 = new Individual();
        int numGenes = parent1.chromosome.length;
        int cross1 = Parameters.random.nextInt(numGenes);
        int cross2 = cross1;
        int min, max;

        while (cross2 == cross1) {
            cross2 = Parameters.random.nextInt(numGenes);
        }

        min = Math.min(cross1, cross2);
        max = Math.max(cross1, cross2);

        for (int i = 0; i < min; ++i) {
            child1.chromosome[i] = parent1.chromosome[i];
            child2.chromosome[i] = parent2.chromosome[i];
        }
        for (int i = min; i < max; ++i) {
            child1.chromosome[i] = parent2.chromosome[i];
            child2.chromosome[i] = parent1.chromosome[i];
        }
        for (int i = max; i < numGenes; ++i) {
            child1.chromosome[i] = parent1.chromosome[i];
            child2.chromosome[i] = parent2.chromosome[i];
        }

        Individual[] result = new Individual[2];
        result[0] = child1;
        result[1] = child2;

        return result;
    }

    private Individual[] crossoverUniform(Individual parent1, Individual parent2) {
        Individual child1 = new Individual();
        Individual child2 = new Individual();
        int numGenes = parent1.chromosome.length;
        int cross = Parameters.random.nextInt(2);

        for (int i = 0; i < numGenes; ++i) {
            if (cross < 1) {
                child1.chromosome[i] = parent1.chromosome[i];
                child2.chromosome[i] = parent2.chromosome[i];
            } else {
                child1.chromosome[i] = parent2.chromosome[i];
                child2.chromosome[i] = parent1.chromosome[i];
            }
            cross = Parameters.random.nextInt(2);
        }
        Individual[] result = new Individual[2];
        result[0] = child1;
        result[1] = child2;

        return result;
    }

    private Individual[] crossoverClone(Individual parent1, Individual parent2) {
        int numGenes = parent1.chromosome.length;
        Individual child1 = new Individual();
        Individual child2 = new Individual();

        for (int i = 0; i < numGenes; ++i) {
            child1.chromosome[i] = parent1.chromosome[i];
        }
        for (int i = 0; i < numGenes; ++i) {
            child2.chromosome[i] = parent2.chromosome[i];
        }
        Individual[] result = new Individual[2];
        result[0] = child1;
        result[1] = child2;
        return result;
    }

    private void mutateBoundaryMultiply(Individual[] children) {
        int operation;
        double chance;
        for (Individual child : children) {
            for (int i = 0; i < child.chromosome.length; ++i) {
                chance = Parameters.random.nextDouble();
                operation = Parameters.random.nextInt(2);
                if (chance <= Parameters.mutateRate) {
                    child.chromosome[i] = (operation < 1) ?
                            child.chromosome[i] * -Parameters.mutateChange :
                            child.chromosome[i] * Parameters.mutateChange;
                }
                checkChromosome(child);
            }
        }
    }

    private void mutateBoundaryAddition(Individual[] children) {
        int operation;
        double chance;
        for (Individual child : children) {
            for (int i = 0; i < child.chromosome.length; ++i) {
                chance = Parameters.random.nextDouble();
                operation = Parameters.random.nextInt(2);
                if (chance <= Parameters.mutateRate) {
                    child.chromosome[i] = (operation < 1) ?
                            child.chromosome[i] - Parameters.mutateChange :
                            child.chromosome[i] + Parameters.mutateChange;
                }
                checkChromosome(child);
            }
        }
    }

    private void checkChromosome(Individual child) {
        for (Double gene : child.chromosome) {
            if (gene < Parameters.minGene) {
                gene = Parameters.minGene;
            } else if (gene > Parameters.maxGene) {
                gene = Parameters.maxGene;
            }
        }
    }

    private void replace(Individual child1, Individual child2, Individual[] population) {
        int popSize = population.length;
        Arrays.sort(population);
        population[popSize - 1] = child1;
        population[popSize - 2] = child2;
    }

    private void injectImmigrant(Individual[] population) {
        Individual immigrant = new Individual();
        evaluateIndividuals(new Individual[]{immigrant});
        Arrays.sort(population);
        population[population.length - 3] = immigrant;
    }

    private static void showVector(double[] vector, int valsPerRow, int decimals, boolean newLine) {
        for (int i = 0; i < vector.length; ++i) {
            if (i % valsPerRow == 0) {
                System.out.println("");
            }
            if (vector[i] >= 0.0) {
                System.out.print(" ");
            }
            System.out.print(vector[i] + " ");
        }
        if (newLine) {
            System.out.println("");
        }
    }

    private static void showMatrix(double[][] matrix, int numRows, int decimals, boolean newLine) {
        for (int i = 0; i < numRows; ++i) {
            System.out.print(i + ": ");
            for (int j = 0; j < matrix[i].length; ++j) {
                if (matrix[i][j] >= 0.0) {
                    System.out.print(" ");
                } else {
                    System.out.print("-");
                }
                System.out.print(Math.abs(matrix[i][j]) + " ");
            }
            System.out.println("");
        }
        if (newLine) {
            System.out.println("");
        }
    }

}
