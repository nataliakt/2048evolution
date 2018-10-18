package br.nataliakt.e2048.ga;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

/**
 * Represents a gene list
 * Each gene starts in 0 and ends in geneLimit
 * @author Natalia Kelim Thiel
 * @version 1.0.0
 */
public class Chromosome {

    private final int id;
    private final List<Integer> geneList;
    private final Generation generation;
    private int fitness;

    /**
     * Constructor with a existent gene list
     * @param generation
     * @param geneList
     */
    public Chromosome(Generation generation, List<Integer> geneList) {
        id = NextId.nextId();
        this.generation = generation;
        this.geneList = geneList;
        updateFitness();
    }

    /**
     * Constructor with a new empty gene list
     * @param generation
     */
    public Chromosome(Generation generation) {
        id = NextId.nextId();
        this.generation = generation;
        geneList = new ArrayList<>();
        fitness = 0;
    }

    /**
     * Update the fitness value
     */
    private void updateFitness() {
        fitness = getGeneList().mapToInt(gene -> gene ^ 2).sum();
    }

    /**
     * Do the mutation
     */
    public void mutation() {
        for (int i = 0; i < geneList.size(); i++) {
            double chance = ThreadLocalRandom.current().nextDouble();
            if (chance >= generation.getMutation()) {
                continue;
            }

            int mut;
            do {
                mut = ThreadLocalRandom.current().nextInt(generation.getGeneLimit());
            } while (mut == geneList.get(i));
            geneList.set(i, mut);
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
    public Stream<Integer> getGeneList() {
        return geneList.stream();
    }

    /**
     * Insert a new gene into the list
     * @param gene
     */
    public void add(Integer gene) {
        geneList.add(gene);
        updateFitness();
    }

    /**
     * Return a gene in the index position
     * @param index
     * @return
     */
    public Integer get(int index) {
        return geneList.get(index);
    }

    /**
     * Number of genes in the list
     * @return
     */
    public int size() {
        return geneList.size();
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
        getGeneList().forEach(gene -> {
            stringBuilder.append(gene);
            stringBuilder.append(" ");
        });
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
