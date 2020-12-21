package view.interfaces;

import javafx.scene.Node;

import java.util.List;

public interface ViewManipulations {
    void add(Node node, int column, int row);
    void add(Node node, int index);
    void remove(Node node);
    List<Node> getAll();
}
