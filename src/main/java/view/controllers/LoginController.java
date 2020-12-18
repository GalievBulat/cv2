package view.controllers;

import protocol.ClientCommunication;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import server.model.User;
import view.GameViewExecution;
import view.interfaces.OnLogInListener;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class LoginController implements Initializable {
    @FXML
    public Button bn_login;
    @FXML
    public ChoiceBox<Integer> chb_room;
    @FXML
    public TextField tf_name;
    @FXML
    public Button bn_join;
    private User user;
    private OnLogInListener onLogInListener;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bn_login.setOnMouseClicked(event ->{
            String name = tf_name.getText();
            if (user!=null) {
                user.setName(name);
                setRooms(user.getRooms());
                //TODO if rooms are empty
                bn_join.setOnMouseClicked(event1 ->{
                    onLogInListener.onLogIn(user,chb_room.getValue());
                });
            }
        });
    }
    public void setUser(User u){
        user = u;
    }
    private void setRooms(int[] rooms){
        chb_room.setItems(new ObservableListWrapper<>(Arrays.stream(rooms).boxed().collect(Collectors.toList())));
    }
    public void setOnLogInListener(OnLogInListener onLogInListener) {
        this.onLogInListener = onLogInListener;
    }

}
