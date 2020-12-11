package view.services;

public class BoardManager {
    public final static int BOARD_COLUMNS = 16;
    public final static int BOARD_ROWS = 16;
    boolean [][] allies = new boolean[BOARD_COLUMNS][BOARD_ROWS];
    boolean [][] enemies = new boolean[BOARD_COLUMNS][BOARD_ROWS];
    public boolean addAlly(int column,int row){
        if(validateCoordinates(column,row) && !allies[column][row] && !enemies[column][row]){
            allies[column][row] = true;
            return true;
        }
        return false;
    }
    public boolean addEnemy(int column,int row){
        if(validateCoordinates(column,row) && !allies[column][row] && !enemies[column][row]){
            enemies[column][row] = true;
            return true;
        }
        return false;
    }
    public void remove(int column, int row){
        if (validateCoordinates(column, row)){
            allies[column][row] = false;
            enemies[column][row] = false;
        }
    }
    public boolean checkIfExists(int column, int row){
        if (validateCoordinates(column, row)){
            return allies[column][row] || enemies[column][row];
        } return false;
    }
    public boolean checkIfAlly(int column, int row){
        if (validateCoordinates(column, row)){
            return allies[column][row];
        } return false;
    }
    public boolean checkIfEnemy(int column, int row){
        if (validateCoordinates(column, row)){
            return enemies[column][row];
        } return false;
    }
    private boolean validateCoordinates(int column,int row){
        return (row>=0 && column>=0 && row<BOARD_ROWS &&
                column< BOARD_COLUMNS);
    }
}
