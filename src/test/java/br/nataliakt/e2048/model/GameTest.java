package br.nataliakt.e2048.model;

import br.nataliakt.e2048.ga.Chromosome;
import br.nataliakt.e2048.ga.Generation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private static final int GENERATION_LENGTH = 10;
    private static final int CHROMOSOME_LENGTH = 6;
    private static final int GENE_LIMIT = 4;
    private static final double GENE_MUTATION = 0.005;
    private static Generation generation;

    @BeforeEach
    void setUp() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        generation = new Generation(GENE_LIMIT, GENE_MUTATION, CHROMOSOME_LENGTH, GENERATION_LENGTH, Game.class);
    }

    @Test
    void swipe() {
        Game game = (Game) generation.get(0);
        System.out.println(game);
        for (int movement : game.getGeneList()) {
            MovimentEnum movimentEnum = MovimentEnum.find(movement);
            System.out.println(movimentEnum);
            game.swipe(movimentEnum);
            System.out.println(game);
        }
    }
}