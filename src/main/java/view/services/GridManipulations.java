package view.services;

import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import view.interfaces.ViewManipulations;

import java.util.List;

public class GridManipulations implements ViewManipulations {
    private final GridPane pane;
    public GridManipulations(GridPane pane){
        this.pane = pane;
    }
    @Override
    public void add(Node node, int column, int row){
        pane.add(node,column,row);
    }
    @Override
    public void add(Node node, int index) {
        pane.add(node,index,0);
    }
    @Override
    public void remove(Node node){
        pane.getChildren().remove(node);
    }
    public List<Node> getAll(){
        return pane.getChildren();
    }
}
