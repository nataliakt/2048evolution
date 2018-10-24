package br.nataliakt.e2048.controller;

import br.nataliakt.e2048.model.Game;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;


public class GameController {

    @FXML
    private GridPane gameGrid;

    @FXML
    private Label scoreLabel;

    @FXML
    private Label movimentsLabel;

    private Game game;
    private boolean playAlone = false;

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            newGame();
        });
    }

    private void newGame() {
        scoreLabel.textProperty().bind(game.getScore().asString());
        movimentsLabel.textProperty().bind(game.getMoviments().asString());
        updateGrid();
        if (!playAlone) {
            game.start();
        }
    }

    private void updateGrid() {
        for (int i = 0; i < Game.WIDTH; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPercentWidth(100.0 / Game.WIDTH);
            colConst.setHalignment(HPos.CENTER);
            gameGrid.getColumnConstraints().add(colConst);
        }
        for (int i = 0; i < Game.HEIGHT; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setValignment(VPos.CENTER);
            rowConst.setPercentHeight(100.0 / Game.HEIGHT);
            gameGrid.getRowConstraints().add(rowConst);
        }
        for (int i = 0; i < Game.HEIGHT; i++) {
            for (int j = 0; j < Game.WIDTH; j++) {
                Label number = new Label();
                number.textProperty().bind(Bindings
                        .when(game.getBoard()[i][j].isEqualTo(0))
                        .then(new SimpleStringProperty(""))
                        .otherwise(game.getBoard()[i][j].asString()));
                gameGrid.add(number, j, i);
            }
        }
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    public void setPlayAlone(boolean playAlone) {
        this.playAlone = playAlone;
    }
}
