package view.model;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Figure {
    private final Circle circle;
    private int column;
    private int row;
    private final UnitType unitType;
    public Figure(int id,int column,int row){
        this.unitType = new UnitType.Builder().id(id).name(Units.values()[id].name()).create();
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
    public void setColour(int i){
        if (i == 1){
            circle.setFill(Color.WHITE);
        }else
            circle.setFill(Color.BLACK);
    }

    public void setRow(int row) {
        this.row = row;
    }
    public UnitType getUnit() {
        return unitType;
    }
}
