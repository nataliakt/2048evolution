package br.nataliakt.e2048;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(Main.class.getResource("C:\\Users\\natal\\IdeaProjects\\2048evolution\\src\\main\\java\\br\\nataliakt\\e2048\\view\\board.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 350));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
