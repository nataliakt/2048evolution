package br.nataliakt.e2048;

import br.nataliakt.e2048.controller.GameController;
import br.nataliakt.e2048.ga.Generation;
import br.nataliakt.e2048.model.Game;
import br.nataliakt.e2048.model.MovimentEnum;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/playground.fxml"));
        Parent root = (Parent) loader.load();
        primaryStage.setTitle("2048evolution");
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
