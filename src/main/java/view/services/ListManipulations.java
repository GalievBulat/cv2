package view.services;

import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import view.interfaces.ViewManipulations;


public class ListManipulations{
    private final ListView<String> listView;
    public ListManipulations(ListView<String> listView){
        this.listView = listView;
    }
    public void append(String text){
        listView.getItems().add(text);
    }
}