package br.nataliakt.e2048.ga;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

/**
 * Represents the individuals of a generation
 * @author Natalia Kelim Thiel
 * @version 1.0.0
 */
public class Generation {

    private final int id;
    private final int geneLimit;
    private final double mutation;
    private final int chromosomeSize;
    private List<Chromosome> chromosomeList;
    private int totalFitness;

    /**
     * Constructor with a new empty chromosome list
     * @param geneLimit
     * @param mutation
     */
    public Generation(int geneLimit, double mutation, int chromosomeSize) {
        assert geneLimit > 0;

        id = NextId.nextId();
        this.geneLimit = geneLimit;
        this.mutation = mutation;
        this.chromosomeSize = chromosomeSize;
        chromosomeList = new ArrayList<>();
        totalFitness = 0;
    }

    /**
     * Next generation
     * @return
     */
    public Generation nextGeneration() {
        List<Pair> parents = getParents();
        Generation next = new Generation(geneLimit, mutation, chromosomeSize);
        // Get the new children
        parents.stream().forEach(parent ->
            next.addAll(chrossover((Chromosome) parent.getKey(), (Chromosome) parent.getValue()))
        );

        // Apply the mutation
        next.getChromosomeList().forEach(Chromosome::mutation);

        return next;
    }

    /**
     * Make two children with the parents feature
     * @param mom
     * @param dad
     * @return
     */
    protected List<Chromosome> chrossover(Chromosome mom, Chromosome dad) {
        int cut = ThreadLocalRandom.current().nextInt(1, mom.size() - 2);
        List<Integer> children1 = new ArrayList<>();
        List<Integer> children2 = new ArrayList<>();

        for (int i = 0; i < cut; i++) {
            children1.add(mom.get(i));
            children2.add(dad.get(i));
        }

        for (int i = cut; i < mom.size(); i++) {
            children1.add(dad.get(i));
            children2.add(mom.get(i));
        }

        Chromosome chromosome1 = new Chromosome(this, children1);
        Chromosome chromosome2 = new Chromosome(this, children2);

        return Arrays.asList(chromosome1, chromosome2);
    }

    /**
     * Random parents to build a next generation
     * @return
     */
    protected List<Pair> getParents() {
        List<Pair> parents = new ArrayList<>();
        do {
            Pair newParent = new Pair(getRouletteRandom(), getRouletteRandom());
            if (isNewParentValid(newParent, parents)) {
                parents.add(newParent);
            }
        } while (parents.size() * 2 < chromosomeList.size());

        return parents;
    }

    /**
     * Find a chromosome by roulette method
     * @return
     */
    protected Chromosome getRouletteRandom() {
        double p = ThreadLocalRandom.current().nextDouble();

        Chromosome parent = chromosomeList.get(chromosomeList.size() - 1);
        double total = 0;
        for (int i = 0; i < chromosomeList.size(); i++) {
            Chromosome c = chromosomeList.get(i);
            double value = c.getFitness() / (double) totalFitness;
            total += value;
            if (total >= p) {
                parent = c;
                break;
            }
        }
        return parent;
    }

    /**
     * Validade if the new pair can be included on the parents list
     * @param newParent
     * @param parents
     * @return
     */
    protected boolean isNewParentValid(Pair newParent, List<Pair> parents) {
        boolean valid = true;
        if (newParent.getKey().equals(newParent.getValue())) {
            valid = false;
            return valid;
        }
        for(Pair parent : parents) {
            if ((parent.getKey().equals(newParent.getKey()) &&
                    parent.getValue().equals(newParent.getValue())) ||
                (parent.getKey().equals(newParent.getValue()) &&
                        parent.getValue().equals(newParent.getKey()))) {
                valid = false;
                break;
            }
        }
        return valid;
    }

    /**
     * Sum all the fitness
     */
    private void updateTotalFitness() {
        totalFitness = getChromosomeList().mapToInt(chromosome -> chromosome.getFitness()).sum();
    }

    /**
     * Gene list as a stream for a unique use
     * @return
     */
    public Stream<Chromosome> getChromosomeList() {
        return chromosomeList.stream();
    }

    /**
     * Insert a new chromosome into the list
     * @param chromosome
     */
    public void add(Chromosome chromosome) {
        chromosomeList.add(chromosome);
        updateTotalFitness();
    }

    /**
     * Insert chromosomes into the list
     * @param c
     */
    public void addAll(Collection c) {
        chromosomeList.addAll(c);
        updateTotalFitness();
    }

    /**
     * Number of chromosomes in the list
     * @return
     */
    public int size() {
        return chromosomeList.size();
    }

    /**
     * The id unique of generation
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * The limit of gene representation
     * @return
     */
    public int getGeneLimit() {
        return geneLimit;
    }

    /**
     * The mutation chance
     * @return
     */
    public double getMutation() {
        return mutation;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Generation ");
        stringBuilder.append(id);
        stringBuilder.append(" (F: ");
        stringBuilder.append(totalFitness);
        stringBuilder.append(", L: ");
        stringBuilder.append(geneLimit);
        stringBuilder.append(")");
        getChromosomeList().forEach(chromosome -> {
            stringBuilder.append("\n  ");
            stringBuilder.append(chromosome);
        });
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
