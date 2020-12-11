package view.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import serverclient.model.User;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    public Button bn_login;
    @FXML
    public ChoiceBox chb_room;
    @FXML
    public TextField tf_name;
    private User user;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bn_login.setOnMouseClicked(event ->{
            String name = tf_name.getText();
            user.setName(name);
            user.notify();
        });
    }
    public void setUser(User u){
        user = u;
    }
}
