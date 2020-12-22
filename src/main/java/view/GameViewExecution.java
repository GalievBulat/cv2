package view;


import protocol.ClientCommunication;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import protocol.data.CommandData;
import view.model.User;
import view.controllers.GameSpaceController;
import view.controllers.LoginController;
import view.services.ServerCommandsPresenterThread;

import java.io.IOException;

public class GameViewExecution extends Application {
    private LoginController loginController;
    private GameSpaceController gameController;
    private final User user;
    private Stage stage;

    public GameViewExecution(){
        this.user = new User();
    }


    @Override
    public void stop() {
        System.out.println("Stage is closing");
    }


    @Override
    public void start(Stage primaryStage) {
        stage = new Stage();
        Parent root;
        FXMLLoader loader;
        try {
            loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            root = loader.load();
            loginController = loader.getController();
            loginController.setUser(user);
            loginController.setOnLogInListener(this::startGame);
            stage.setMinHeight(400);
            stage.setMinWidth(600);
            stage.setTitle("Представтесь");
            stage.setScene(new Scene(root, 600, 400));
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void startGame(ClientCommunication clientCommunication, int roomId){
        Parent root;
        FXMLLoader loader;
        try {
            clientCommunication.sendCommandWithNum(CommandData.ENTER, roomId);
            loader = new FXMLLoader(getClass().getResource("/game.fxml"));
            root = loader.load();
            gameController = loader.getController();
            gameController.setClient(clientCommunication);
            gameController.setOnLeaveListener(()->{
                try {
                    clientCommunication.sendCommandWithCoords(CommandData.DISCONNECT);
                    clientCommunication.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                stage.close();
                start(stage);
            });
            loginController = null;
            ServerCommandsPresenterThread commandsPresenterThread =
                    new ServerCommandsPresenterThread(clientCommunication,gameController);
            commandsPresenterThread.executeInBackGround();
            stage.setMinHeight(800);
            stage.setMinWidth(900);
            stage.setTitle("Игра");
            stage.setScene(new Scene(root, 900, 800));
            stage.setOnCloseRequest(event -> clientCommunication.stop());
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
