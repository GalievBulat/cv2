package view;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import serverclient.model.User;
import view.controllers.GameSpaceController;

import java.io.IOException;

public class GameViewExecution extends Application {

    private GameSpaceController mainSceneController;
    private User user;

    public void main(User user) {
        this.user = user;
        launch();
    }

    @Override
    public void stop(){
        System.out.println("Stage is closing");
    }


    @Override
    public void start(Stage primaryStage){
        Parent root;
        Stage stage = new Stage();
        FXMLLoader loader;
        try {
            loader = new FXMLLoader(getClass().getResource("../game.fxml"));
            root = loader.load();
            //root.getStylesheets().add("-fx-background-color: #648506;");
            mainSceneController = loader.getController();
            stage.setMinHeight(600);
            stage.setMinWidth(800);
            stage.setTitle("Чат");
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }



}
