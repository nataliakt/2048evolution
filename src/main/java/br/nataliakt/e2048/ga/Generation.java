package br.nataliakt.e2048.ga;

import javafx.util.Pair;

import java.lang.reflect.InvocationTargetException;
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
public class Generation <T extends Chromosome> {

    private final int id;
    private final int geneLimit;
    private final double mutation;
    private final int chromosomeSize;
    private List<T> chromosomeList;
    private int totalFitness;

    /**
     * Constructor with a new empty chromosome list
     * @param geneLimit
     * @param mutation
     */
    public Generation(int geneLimit, double mutation, int chromosomeSize) {
        assert geneLimit > 0;
        assert mutation >= 0 && mutation <= 1;
        assert chromosomeSize > 0;

        id = NextId.nextId();
        this.geneLimit = geneLimit;
        this.mutation = mutation;
        this.chromosomeSize = chromosomeSize;
        chromosomeList = new ArrayList<>();
        totalFitness = 0;
    }

    /**
     * Constructor with a new random chromosome
     * @param geneLimit
     * @param mutation
     * @param chromosomeSize
     * @param generationSize
     */
    public Generation(int geneLimit, double mutation, int chromosomeSize, int generationSize, Class<T> classObject) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this(geneLimit, mutation, chromosomeSize);
        randomGeneration(generationSize, classObject);
    }

    public void randomGeneration(int generationSize, Class<T> classObject) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        for (int c = 0; c < generationSize; c++) {
            int[] geneList = new int[chromosomeSize];
            for (int g = 0; g < chromosomeSize; g++) {
                geneList[g] = ThreadLocalRandom.current().nextInt(geneLimit);
            }
            T chromosome = (T) classObject.getDeclaredConstructor(Generation.class, int[].class).newInstance(this, geneList);
            chromosomeList.add(chromosome);
        }
    }

    /**
     * Next generation
     * @return
     */
    public Generation nextGeneration(Class classObject) {
        List<Pair> parents = getParents();
        Generation next = new Generation(geneLimit, mutation, chromosomeSize);
        // Get the new children
        parents.stream().parallel().forEach(parent ->
                {
                    try {
                        List<Chromosome> chromosomes = chrossover((T) parent.getKey(), (T) parent.getValue(), classObject);
                        next.addAll(chromosomes);
                        for (Chromosome chromosome : chromosomes) {
                            chromosome.mutation();
                        }
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    }
                }
        );

        return next;
    }

    /**
     * Make two children with the parents feature
     * @param mom
     * @param dad
     * @return
     */
    protected List<Chromosome> chrossover(Chromosome mom, Chromosome dad, Class classObject) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        int[] children1 = new int[chromosomeSize];
        int[] children2 = new int[chromosomeSize];
        int nCuts = 2;
        int[] cut = new int[nCuts];
        for (int i = 0; i < nCuts; i++) {
            cut[i] = r.nextInt(1, mom.size() - 2);
        }
        Arrays.sort(cut);

        for (int i = -1; i < nCuts; i++) {
            int initial = 0;
            int limit = mom.size();
            if (i != -1) {
                initial = cut[i];
            }
            if (i != nCuts - 1) {
                limit = cut[i + 1];
            }
            for (int c = initial; c < limit; c++) {
                if (i % 2 == 0) {
                    children1[c] = mom.get(c);
                    children2[c] = dad.get(c);
                } else {
                    children2[c] = mom.get(c);
                    children1[c] = dad.get(c);
                }
            }
        }
//        for (int i = 0; i < cut; i++) {
//            children1[i] = mom.get(i);
//            children2[i] = dad.get(i);
//        }
//
//        for (int i = cut; i < mom.size(); i++) {
//            children1[i] = dad.get(i);
//            children2[i] = mom.get(i);
//        }

        T chromosome1 = (T) classObject.getDeclaredConstructor(Generation.class, int[].class).newInstance(this, children1);
        T chromosome2 = (T) classObject.getDeclaredConstructor(Generation.class, int[].class).newInstance(this, children2);
//        Chromosome chromosome1 = new Chromosome(this, children1);
//        Chromosome chromosome2 = new Chromosome(this, children2);

        return Arrays.asList(chromosome1, chromosome2);
    }

    /**
     * Random parents to build a next generation
     * @return
     */
    protected List<Pair> getParents() {
        updateTotalFitness();
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
        try {
            totalFitness = getChromosomeList().mapToInt(chromosome -> chromosome.getFitness()).sum();
        } catch (Exception e) {
            System.err.println("Fitness não atualizado");
        }
    }

    /**
     * Gene list as a stream for a unique use
     * @return
     */
    public Stream<T> getChromosomeList() {
        return chromosomeList.stream();
    }

    /**
     * Insert a new chromosome into the list
     * @param chromosome
     */
    public void add(T chromosome) {
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
     * The chromosome in a index position
     * @param index
     * @return
     */
    public Chromosome get(int index) {
        return chromosomeList.get(index);
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
        private static int LAST_ID = -1;

        public synchronized static int nextId() {
            LAST_ID++;
            return LAST_ID;
        }
    }
}
