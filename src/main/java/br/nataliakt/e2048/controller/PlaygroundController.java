package br.nataliakt.e2048.controller;

import br.nataliakt.e2048.ga.Chromosome;
import br.nataliakt.e2048.ga.Generation;
import br.nataliakt.e2048.model.Game;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;

public class PlaygroundController {

    private static final int GENERATIONS_LIMIT = 10000; // Number of generations
    private static final double MUTATION = 0.005;
    private static final int CHROMOSOME_SIZE = 500; // Number of moviments
    private static final int GENERATION_SIZE = 20; // Simultaneous games


    @FXML
    private Label generationLabel;

    @FXML
    private Label bestFitnessLabel;

    @FXML
    private Label bestNumberLabel;

    @FXML
    private Label bestMovimentLabel;

    @FXML
    private FlowPane playgroundPane;

    private Generation<Game> generation;

    @FXML
    public void initialize() {
        try {
            generation = new Generation(4, MUTATION, CHROMOSOME_SIZE, GENERATION_SIZE, Game.class);
            start();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void start() {
        generation.getChromosomeList().forEach(chromosome -> {
            Platform.runLater(() -> {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/game.fxml"));

                AnchorPane root;
                try {
                    root = (AnchorPane) loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

                GameController controller = loader.<GameController>getController();
                controller.setGame((Game) chromosome);

                playgroundPane.getChildren().add(root);
            });
        });

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    updateScreenValues(false);
                });
                if (!isRunning()) {
                    timer.cancel();
                    nextGeneration();
                }
            }
        }, 1000, 1000);
    }

    private void nextGeneration() {
        Platform.runLater(() -> {
            updateScreenValues(true);
            if (generation.getId() == GENERATIONS_LIMIT) {
                return;
            }

            playgroundPane.getChildren().clear();
            generation = generation.nextGeneration(Game.class);
            generationLabel.setText(String.valueOf(generation.getId()));

            start();
        });
    }

    private boolean isRunning() {
        boolean running = generation.getChromosomeList().anyMatch(game -> game.isRunning());
        return running;
    }

    private void updateScreenValues(boolean print) {
        Game bestFitness = generation.getChromosomeList().max(
                Comparator.comparingInt(Chromosome::getFitness)).get();
        int oldBestFitness = Integer.parseInt(bestFitnessLabel.getText());
        if (bestFitness.getFitness() > oldBestFitness) {
            bestFitnessLabel.setText(String.valueOf(bestFitness.getFitness()));
        }
        final int[] max = {2};
        generation.getChromosomeList().forEach(game -> {
            for (SimpleIntegerProperty[] line : game.getBoard()) {
                for (SimpleIntegerProperty element : line) {
                    if (max[0] < element.get()) {
                        max[0] = element.get();
                    }
                }
            }
        });
        if (Integer.parseInt(bestNumberLabel.getText()) < max[0]) {
            bestNumberLabel.setText(String.valueOf(max[0]));
        }

        Game bestMoviment = generation.getChromosomeList().max(
                Comparator.comparingInt(c -> c.getMoviments().get())).get();
        int oldBestMoviment = Integer.parseInt(bestMovimentLabel.getText());
        if (bestMoviment.getMoviments().get() > oldBestMoviment) {
            bestMovimentLabel.setText(String.valueOf(bestMoviment.getMoviments().get()));
        }
        if (print) {
            System.out.println("Geração: " + generation.getId());
            System.out.println("Melhor Fitness Geração: " + bestFitness.getFitness());
            System.out.println("Melhor Número Geração: " + max[0]);
            System.out.println("Melhor Movimento Geração: " + bestMoviment.getMoviments().get());
            System.out.println("Melhor Fitness: " + bestFitnessLabel.getText());
            System.out.println("Melhor Número: " + bestNumberLabel.getText());
            System.out.println("Melhor Movimento: " + bestMovimentLabel.getText());
//            System.out.println(generation);
            System.out.println();
            System.out.println();
        }
    }

}
