package br.nataliakt.e2048;

import br.nataliakt.e2048.controller.GameController;
import br.nataliakt.e2048.ga.Generation;
import br.nataliakt.e2048.model.Game;
import br.nataliakt.e2048.model.MovimentEnum;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class PlayAlone  extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/game.fxml"));
        Parent root = (Parent) loader.load();

        Generation generation = new Generation(4, 0, 2, 1, Game.class);
        GameController controller = loader.getController();
        Game game = new Game(generation, new int[]{0,0});
        controller.setGame(game);
        controller.setPlayAlone(true);

        primaryStage.setTitle("2048");
        Scene scene = new Scene(root, 300, 350);
        primaryStage.setScene(scene);

        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case DOWN:
                        game.swipe(MovimentEnum.DOWN);
                        break;
                    case UP:
                        game.swipe(MovimentEnum.UP);
                        break;
                    case LEFT:
                        game.swipe(MovimentEnum.LEFT);
                        break;
                    case RIGHT:
                        game.swipe(MovimentEnum.RIGHT);
                        break;
                }
            }
        });
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }



}
