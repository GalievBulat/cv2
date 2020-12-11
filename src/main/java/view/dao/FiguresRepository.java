package view.dao;

import view.interfaces.ViewManipulations;
import view.models.Figure;
import view.services.BoardManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FiguresRepository {
    private final ViewManipulations service;
    private final BoardManager boardManager;
    private final List<Figure> figuresList = new ArrayList<>();
    public FiguresRepository(ViewManipulations manipulator, BoardManager manager){
        this.service = manipulator;
        this.boardManager = manager;
    }
    public void addAlliedFigure(Figure figure){
        if(boardManager.addAlly(figure.getColumn(),figure.getRow())) {
            figuresList.add(figure);
            service.add(figure.getView(), figure.getColumn(), figure.getRow());
        }else
            throw new IllegalArgumentException();
    }
    public void addEnemyFigure(Figure figure){
        if(boardManager.addEnemy(figure.getColumn(),figure.getRow())) {
            figuresList.add(figure);
            service.add(figure.getView(), figure.getColumn(), figure.getRow());
        }else
            throw new IllegalArgumentException();
    }
    public void removeFigure(Figure figure){
        figuresList.remove(figure);
        service.remove(figure.getView());
        boardManager.remove(figure.getColumn(),figure.getRow());
    }
    public Optional<Figure> findFigure(int column, int row){
        for (Figure figure: figuresList){
            if (figure.getColumn() == column && figure.getRow() == row){
                return Optional.of(figure);
            }
        }
        return Optional.empty();
    }

    public boolean checkIfAlly(int column, int row){
        return boardManager.checkIfAlly(column,row);
    }
    public boolean checkIfExists(int column, int row){
        return boardManager.checkIfExists(column,row);
    }
    public boolean checkIfEnemy(int column, int row){
        return boardManager.checkIfEnemy(column,row);
    }
}
