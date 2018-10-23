package br.nataliakt.e2048.ga;

import javafx.util.Pair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

class GenerationTest {

    private static final int GENERATION_LENGTH = 10;
    private static final int CHROMOSOME_LENGTH = 6;
    private static final int GENE_LIMIT = 4;
    private static final double GENE_MUTATION = 0.005;
    private static Generation generation;

    @BeforeAll
    static void setUp() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        generation = new Generation(GENE_LIMIT, GENE_MUTATION, CHROMOSOME_LENGTH, GENERATION_LENGTH, Chromosome.class);
        System.out.println(generation);
    }

    @Test
    void nextGeneration() {
        Generation next = generation.nextGeneration(Chromosome.class);
        System.out.println(next);
    }

    @Test
    void crossover() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Chromosome mom = generation.getRouletteRandom();
        Chromosome dad = generation.getRouletteRandom();
        List<Chromosome> children = generation.chrossover(mom, dad, Chromosome.class);

        assertEquals(2, children.size(), "Wrong number of childrem");
        assertEquals(mom.size(), children.get(0).size(), "Wrong number of genes in the first children");
        assertEquals(dad.size(), children.get(1).size(), "Wrong number of genes in the last children");
    }

    @Test
    void getParents() {
        List<Pair> parents = generation.getParents();

        assertEquals((GENERATION_LENGTH % 2 == 0 ? GENERATION_LENGTH / 2 : (GENERATION_LENGTH + 1) / 2),
                parents.size(), "Wrong size of parents");

        for (Pair parent1 : parents) {
            for (Pair parent2 : parents) {
                if (parent1.equals(parent2)) {
                    continue;
                }

                assertFalse(parent1.getKey().equals(parent2.getKey()) &&
                        parent1.getValue().equals(parent2.getValue()), "Equal parents");

                assertFalse(parent1.getKey().equals(parent2.getValue()) &&
                        parent1.getValue().equals(parent2.getKey()), "Inversal equal parents");
            }
        }
    }

    @Test
    void getRouletteRandom() {
        Chromosome chromosome = generation.getRouletteRandom();
        assertNotNull(chromosome);
    }
}