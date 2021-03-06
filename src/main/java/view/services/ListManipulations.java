package view.services;

import javafx.scene.control.ListView;
import view.model.Message;


public class ListManipulations{
    private final ListView<String> listView;
    public ListManipulations(ListView<String> listView){
        this.listView = listView;
    }
    public void append(String text){
        listView.getItems().add(text);
    }
    public void printMessage(Message message){
        listView.getItems().add(message.getUser() + " \n " + message.getText());
    }
}