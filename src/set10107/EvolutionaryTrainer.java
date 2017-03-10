package set10107;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class EvolutionaryTrainer extends NeuralNetwork {

    private EvolutionaryTrainer() {
        super();
    }

    public static void main(String[] args) {

        String dataSet = "A";
        Parameters.setDataSet(dataSet);

        System.out.println("\nThe training data is:");
        // showMatrix(Parameters.trainData, Parameters.trainData.length, 1, true);

        System.out.println("The test data is:");
        // showMatrix(Parameters.testData, Parameters.testData.length, 1, true);

        /*
          train the NN using our EA
         */
        EvolutionaryTrainer nn = new EvolutionaryTrainer();
        System.out.println("\nBeginning training");
        double[] bestWeights = nn.train();

        /*
          Show best weights found
         */
        System.out.println("Training complete");
        System.out.println("\nFinal weights and bias values:");
        // showVector(bestWeights, 10, 3, true);

        /*
          Show accuracy on training data
         */
        nn.setWeights(bestWeights);
        double trainAcc = nn.testNetwork(Parameters.trainData);
        System.out.print("\nAccuracy on training data = ");
        System.out.println(trainAcc);

        /*
          Show accuracy on unseen test data
         */
        double testAcc = nn.testNetwork(Parameters.testData);
        System.out.print("\nAccuracy on test data = ");
        System.out.println(testAcc);
        System.out.println("\nEnd NN training demo");

    }

    private double[] train() {
        /*
           initialize the population
         */
        Individual[] population = initialise();

        /*
          used to store a copy of the best Individual in the population
         */
        Individual bestIndividual = getBest(population);

        /*
          main EA processing loop
         */
        int gen = 0;
        boolean done = false;
        while (gen < Parameters.maxGeneration && done == false) {

            /*
               this is a skeleton EA - you need to add the methods
               you can also change the EA if you want
             */

            //Select 2 good Individuals
            Individual parent1 = selectBogo(population); // 2 good Individuals
            Individual parent2 = selectBogo(population);

            //Generate 2 new children by crossover (includes call to mutation)
            Individual[] children = reproduce(parent1, parent2);

            //Evaluate the new individuals
            evaluateIndividuals(children);

            //Replace (sorts the population and replaces the two worst individuals)
            replace(children[0], children[1], population);

            //introduce some diversity
            injectImmigrant(population);

            //check that the best hasn't improved
            bestIndividual = getBest(population);

            // System.out.println(gen + "\t" + bestIndividual);

            //check our termination criteria
            if (bestIndividual.error < Parameters.exitError) {
                done = true;
            }
            ++gen;
        }
        return bestIndividual.chromosome;
    } // Train

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

    private Individual selectBogo(Individual[] population) {
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
        int numGenes = parent1.chromosome.length;
        int cross = Parameters.random.nextInt(numGenes);

        Individual child1 = new Individual();
        Individual child2 = new Individual();

        for (int i = 0; i < cross; ++i) {
            child1.chromosome[i] = parent1.chromosome[i];
            child2.chromosome[i] = parent2.chromosome[i];
        }
        for (int i = cross; i < numGenes; ++i) {
            child1.chromosome[i] = parent2.chromosome[i];
            child2.chromosome[i] = parent1.chromosome[i];
        }
        mutate(child1);
        mutate(child2);

        Individual[] result = new Individual[2];
        result[0] = child1;
        result[1] = child2;

        return result;
    }

    private Individual[] crossoverDouble(Individual parent1, Individual parent2) {
        int numGenes = parent1.chromosome.length;
        int cross1 = Parameters.random.nextInt(numGenes);
        int cross2 = cross1;
        while (cross2 == cross1) {
            cross2 = Parameters.random.nextInt(numGenes);
        }

        int min = Math.min(cross1, cross2);
        int max = Math.max(cross1, cross2);

        Individual child1 = new Individual();
        Individual child2 = new Individual();

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
        mutate(child1);
        mutate(child2);

        Individual[] result = new Individual[2];
        result[0] = child1;
        result[1] = child2;

        return result;
    }

    private Individual[] crossoverUniform(Individual parent1, Individual parent2, int n) {
        int numGenes = parent1.chromosome.length;

        Individual child1 = new Individual();
        Individual child2 = new Individual();

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

        mutate(child1);
        mutate(child2);

        Individual[] result = new Individual[2];
        result[0] = child1;
        result[1] = child2;

        return result;
    }

    private Individual[] reproduce(Individual parent1, Individual parent2) {
        int numGenes = parent1.chromosome.length;

        Individual child1 = new Individual();
        Individual child2 = new Individual();

        for (int i = 0; i < numGenes; ++i) {
            child1.chromosome[i] = parent1.chromosome[i];
        }

        for (int i = 0; i < numGenes; ++i) {
            child1.chromosome[i] = parent2.chromosome[i];
        }

        mutate(child1);
        mutate(child2);

        Individual[] result = new Individual[2];
        result[0] = child1;
        result[1] = child2;

        return result;
    } // Reproduce

    private void mutate(Individual child) {
        double chance = Parameters.random.nextDouble();
        int operation = Parameters.random.nextInt(2);

        for (int i = 0; i < child.chromosome.length; ++i) {
            if (chance <= Parameters.mutateRate) {
                child.chromosome[i] = (operation < 1) ?
                        child.chromosome[i] * -Parameters.mutateChange :
                        child.chromosome[i] * Parameters.mutateChange;
            }

            if (child.chromosome[i] < Parameters.minGene) {
                child.chromosome[i] = Parameters.minGene;
            } else if (child.chromosome[i] > Parameters.maxGene) {
                child.chromosome[i] = Parameters.maxGene;
            }
        }
    }

    private void replace(Individual child1, Individual child2, Individual[] population) {
        // place child1 and child2 replacing two worst individuals
        int popSize = population.length;

        Arrays.sort(population);
        population[popSize - 1] = child1;
        population[popSize - 2] = child2;
    }

    private void injectImmigrant(Individual[] population) {
        Individual immigrant = new Individual();
        evaluateIndividuals(new Individual[]{immigrant});
        Arrays.sort(population);

        // replace third worst individual
        population[population.length - 3] = immigrant;
    }

    private static void showVector(double[] vector, int valsPerRow, int decimals, boolean newLine) {
        for (int i = 0; i < vector.length; ++i) {
            if (i % valsPerRow == 0)
                System.out.println("");
            if (vector[i] >= 0.0)
                System.out.print(" ");
            System.out.print(vector[i] + " ");
        }
        if (newLine)
            System.out.println("");
    }

    private static void showMatrix(double[][] matrix, int numRows, int decimals, boolean newLine) {
        for (int i = 0; i < numRows; ++i) {
            System.out.print(i + ": ");
            for (int j = 0; j < matrix[i].length; ++j) {
                if (matrix[i][j] >= 0.0)
                    System.out.print(" ");
                else
                    System.out.print("-");
                ;
                System.out.print(Math.abs(matrix[i][j]) + " ");
            }
            System.out.println("");
        }
        if (newLine)
            System.out.println("");
    }
}
