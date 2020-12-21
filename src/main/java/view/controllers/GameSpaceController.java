package view.controllers;

import javafx.scene.control.Button;
import javafx.util.Pair;
import protocol.ClientCommunication;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import protocol.data.CommandData;
import view.dao.CardsRepository;
import view.dao.FiguresRepository;
import view.interfaces.OnLeaveListener;
import view.interfaces.ViewManipulations;
import view.model.Card;
import view.model.Figure;
import view.services.BoardManager;
import view.services.GridManipulations;
import view.services.ListManipulations;

import java.net.URL;
import java.util.*;

public class GameSpaceController implements Initializable {
    @FXML
    public GridPane board;
    @FXML
    public GridPane cards;
    @FXML
    public ListView<String> messagesList;
    @FXML
    public Button leave_button;
    private Card selectedCard;
    private Figure selectedFigure;
    private ViewManipulations boardManipulatingHandler = null;
    private ViewManipulations cardsManipulatingHandler = null;
    private ListManipulations listManipulations = null;
    private FiguresRepository figuresRepository = null;
    private CardsRepository cardsRepository;
    private ClientCommunication communication;
    private OnLeaveListener onLeaveListener;
    private int role = -1;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cardsManipulatingHandler = new GridManipulations(cards);
        boardManipulatingHandler = new GridManipulations(board);
        //TODO
        //cardsManipulatingHandler.add(new Card(new UnitTypeRepository().find(1)).getView(),0,0);
        listManipulations = new ListManipulations(messagesList);
        figuresRepository = new FiguresRepository(boardManipulatingHandler, new BoardManager());
        cardsRepository =  new CardsRepository(card -> {
            if (selectedCard != null) {
                selectedCard.getView().setStyle("-fx-background-color: white");
            }
            selectedCard = card;
            card.getView().setStyle("-fx-border-color: black");
        },cardsManipulatingHandler);
        setUpTheBoard();
        leave_button.setOnMouseClicked(event -> onLeaveListener.onLeave());
        board.setOnMouseClicked((event -> {
            Node node = event.getPickResult().getIntersectedNode();
            if (GridPane.getColumnIndex(node) != null && GridPane.getRowIndex(node)!=null ) {
                int column = GridPane.getColumnIndex(node);
                int row = GridPane.getRowIndex(node);
                if (figuresRepository.checkIfAlly(column,row)){
                    selectedFigure = figuresRepository.findFigure(column,row).orElseThrow(RuntimeException::new);
                } else {
                    if (selectedCard != null  && !figuresRepository.checkIfExists(column, row)) {
                        if(role !=-1) {
                            //worrying about it
                            if ((role == 1 && row == BoardManager.BOARD_ROWS - 1)||(role == 2 && row == 0)) {
                                cardsRepository.remove(selectedCard);
                                communication.sendCommandWithCoordsAndNum(CommandData.DEPLOY,selectedCard.getUnit().getId(),
                                        new Pair<>((byte) column,(byte) row));
                                selectedCard = null;
                            }
                        }
                    } else if (selectedFigure != null &&
                            Math.abs(selectedFigure.getRow() - row) <= 1 && Math.abs(selectedFigure.getColumn() - column) <= 1) {
                        if (figuresRepository.checkIfEnemy(column, row)) {
                            //attackingMap.put(selectedFigure, figuresRepository.findFigure(column, row).orElseThrow(RuntimeException::new));
                            communication.sendCommandWithCoords(CommandData.ATTACK,
                                    new Pair<>((byte)selectedFigure.getColumn(), (byte)selectedFigure.getRow()),
                                    new Pair<>((byte)column,(byte)row));
                        } else if (!figuresRepository.checkIfExists(column, row)) {
                            //figuresRepository.removeFigure(selectedFigure);
                            communication.sendCommandWithCoords(CommandData.MOVE,
                                    new Pair<>((byte) selectedFigure.getColumn(), (byte) selectedFigure.getRow()),
                                    new Pair<>((byte) column, (byte) row));
                            selectedFigure = null;
                        }
                    }
                }
            }
        }));
    }

    public void move(boolean self, int columnS, int rowS, int columnD, int rowD){
        //????
        if (figuresRepository.findFigure(columnS, rowS).isPresent()) {
            Figure prevFigure =figuresRepository.findFigure(columnS, rowS).get();
            figuresRepository.removeFigure(prevFigure);
            Figure newFigure = new Figure(prevFigure.getUnit().getId(), columnD, rowD);
            if (self)
                figuresRepository.addAlliedFigure(newFigure);
            else
                figuresRepository.addEnemyFigure(newFigure);
        }
    }
    public void attack(int columnA,int rowA, int columnV,int rowV){
        listManipulations.append(
                figuresRepository.findFigure(columnA, rowA)
                .orElseThrow(RuntimeException::new).getUnit().getName()
                + " is attacking " +
                figuresRepository.findFigure(columnV, rowV)
                .orElseThrow(RuntimeException::new).getUnit().getName());
    }
    public void deploy(boolean self,int cardId, int column,int row){
        Figure newFigure = new Figure(cardId, column, row);
        if (self)
            figuresRepository.addAlliedFigure(newFigure);
        else
            figuresRepository.addEnemyFigure(newFigure);
        listManipulations.append(newFigure.getUnit().getName() + " is deployed");
    }
    public void remove(int column,int row){
        figuresRepository.findFigure(column,row).ifPresent(figure-> {
            figuresRepository.removeFigure(figure);
            board.getChildren().remove(figure.getView());
        });
    }
    public void setRole(int roleId){
        this.role = roleId;
    }
    public void setClient(ClientCommunication clientCommunication) {
        this.communication = clientCommunication;
    }
    private void setUpTheBoard(){
        for (int i = 0; i < BoardManager.BOARD_COLUMNS; i++) {
            board.addColumn(i);
            ColumnConstraints constraints = new ColumnConstraints(30);
            constraints.setPercentWidth((double) 80/BoardManager.BOARD_ROWS);
            constraints.setHalignment(HPos.CENTER);
            board.getColumnConstraints().add(constraints);
        }
        for (int i = 0; i < BoardManager.BOARD_ROWS; i++) {
            board.addRow(i);
            RowConstraints constraints = new RowConstraints(30);
            constraints.setPercentHeight((double) 80/BoardManager.BOARD_ROWS);
            constraints.setValignment(VPos.CENTER);
            board.getRowConstraints().add(constraints);
        }
        for (int i = 0; i < BoardManager.BOARD_COLUMNS; i++) {
            for (int j = 0; j < BoardManager.BOARD_ROWS; j++) {
                Region region = new Region();
                boardManipulatingHandler.add(region,i,j);
            }
        }
    }
    public void print(String text){
        listManipulations.append(text);
    }

    public CardsRepository getCardsRepository() {
        return cardsRepository;
    }

    public void setOnLeaveListener(OnLeaveListener onLeaveListener) {
        this.onLeaveListener = onLeaveListener;
    }
}
