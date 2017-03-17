package set10107;

import java.util.Arrays;

class Individual implements Comparable<Individual> {

    double[] chromosome; // represents a solution
    double error; // smaller values are better for minimization

    Individual() {
        this.chromosome = new double[Parameters.numGenes];
        for (int i = 0; i < this.chromosome.length; i++) {
            this.chromosome[i] = (Parameters.maxGene - Parameters.minGene) * Parameters.random.nextDouble()
                    + Parameters.minGene;
        }
    }

    @Override
    public int compareTo(Individual other) {
        if (this.error < other.error)
            return -1;
        else if (this.error > other.error)
            return 1;
        else
            return 0;
    }

    Individual copy() {
        Individual copy = new Individual();
        copy.chromosome = Arrays.copyOf(chromosome, chromosome.length);
        copy.error = error;
        return copy;
    }

    @Override
    public String toString() {
        String str = "" + error + "\t";
        for (int i = 0; i < chromosome.length - 2; i++) {
            str += chromosome[i] + ", ";
        }
        str += chromosome[chromosome.length - 1];
        return str;
    }

}
