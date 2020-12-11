package view.models;

import javafx.scene.Node;
import javafx.scene.shape.Circle;

public class Figure {
    private final Node circle;
    private int column;
    private int row;
    private final Unit unit;
    public Figure(int id,int column,int row){
        this.unit = new Unit.Builder().id(id).name(Units.values()[id].name()).create();
        circle = new Circle(15);
        this.column = column;
        this.row = row;
    }
    public Node getView(){
        return circle;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }
    public Unit getUnit() {
        return unit;
    }
}
