package br.nataliakt.e2048.ga;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents a gene list
 * Each gene starts in 0 and ends in geneLimit
 * @author Natalia Kelim Thiel
 * @version 1.0.0
 */
public class Chromosome {

    private final int id;
    private final int[] geneList;
    private final Generation generation;
    private int fitness;

    /**
     * Constructor with a existent gene list
     * @param generation
     * @param geneList
     */
    public Chromosome(Generation generation, int[] geneList) {
        id = NextId.nextId();
        this.generation = generation;
        this.geneList = geneList;
        updateFitness();
    }

    /**
     * Update the fitness value
     */
    public void updateFitness() {
        fitness = geneList.length;
    }

    /**
     * Do the mutation
     */
    public void mutation() {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        for (int i = 0; i < geneList.length; i++) {
            double chance = r.nextDouble();
            if (chance >= generation.getMutation()) {
                continue;
            }

            int mut;
            do {
                mut = r.nextInt(generation.getGeneLimit());
            } while (mut == geneList[i]);
            geneList[i] = mut;
        }
    }

    /**
     * Default fitness implementation summing the square of genes
     * @return
     */
    public int getFitness() {
        return fitness;
    }

    /**
     * Gene list as a stream for a unique use
     * @return
     */
    public int[] getGeneList() {
        return geneList;
    }

    /**
     * Return a gene in the index position
     * @param index
     * @return
     */
    public Integer get(int index) {
        return geneList[index];
    }

    /**
     * Number of genes in the list
     * @return
     */
    public int size() {
        return geneList.length;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("#");
        stringBuilder.append(id);
        stringBuilder.append(" [ ");
        for (int i = 0; i < geneList.length; i++) {
            stringBuilder.append(geneList[i]);
            stringBuilder.append(" ");
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    private static class NextId {
        private static int LAST_ID = 0;

        public synchronized static int nextId() {
            LAST_ID++;
            return LAST_ID;
        }


    }
}
