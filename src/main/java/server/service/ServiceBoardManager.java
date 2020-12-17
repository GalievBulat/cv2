package server.service;

import server.model.Unit;

import java.util.Optional;

public class ServiceBoardManager {
    public final static byte BOARD_COLUMNS = 16;
    public final static byte BOARD_ROWS = 16;
    private final Unit[][] units = new Unit[BOARD_COLUMNS][BOARD_ROWS];
    public void addUnit(Unit unit,byte column,byte row){
        if(validateCoordinates(column,row) && units[column][row]==null){
            units[column][row] = unit;
        }
    }
    public void remove(byte column, byte row){
        if (validateCoordinates(column, row)){
            units[column][row] = null;
        }
    }
    public boolean checkIfExists(byte column, byte row){
        if (validateCoordinates(column, row)){
            return units[column][row]!=null;
        } return false;
    }
    public boolean checkIfExists(int column, int row){
        if (validateCoordinates((byte) column,(byte) row)){
            return units[column][row]!=null;
        } return false;
    }
    private boolean validateCoordinates(byte column,byte row){
        return (row>=0 && column>=0 && row<BOARD_ROWS &&
                column< BOARD_COLUMNS);
    }
    public Optional<Unit> get(byte column,byte row){
        if(units[column][row] != null) {
            return Optional.of(units[column][row]);
        } else return Optional.empty();
    }
    public byte getNeighboursCount(byte column,byte row){
        byte count = 0;
        if (checkIfExists( column+1,row)) count++;
        if (checkIfExists( column-1,row)) count++;
        if (checkIfExists(column,  row+1)) count++;
        if (checkIfExists(column,  row-1)) count++;
        if (checkIfExists( column+1, row+1)) count++;
        if (checkIfExists( column+1, row-1)) count++;
        if (checkIfExists( column-1, row+1)) count++;
        if (checkIfExists( column-1, row-1)) count++;
        return count;
    }
}
