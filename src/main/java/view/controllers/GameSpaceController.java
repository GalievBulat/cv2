package view.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import view.dao.CardsRepository;
import view.dao.FiguresRepository;
import view.interfaces.OnClickListener;
import view.interfaces.ViewManipulations;
import view.models.Card;
import view.models.Figure;
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
    private Card selectedCard;
    private Figure selectedFigure;
    private ViewManipulations boardManipulatingHandler = null;
    private ViewManipulations cardsManipulatingHandler = null;
    private ListManipulations listManipulations = null;
    private FiguresRepository figuresRepository = null;
    private CardsRepository cardsRepository;
    private final Map<Figure,Figure> attackingMap = new HashMap<>();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cardsManipulatingHandler = new GridManipulations(cards);
        boardManipulatingHandler = new GridManipulations(board);
        listManipulations = new ListManipulations(messagesList);
        figuresRepository = new FiguresRepository(boardManipulatingHandler, new BoardManager());

        figuresRepository.addEnemyFigure(new Figure(0,5,14));

        cardsRepository =  new CardsRepository(new OnClickListener() {
            @Override
            public void onClick(Card card) {
                if (selectedCard != null) {
                    selectedCard.getView().setStyle("-fx-background-color: white");
                }
                selectedCard = card;
                card.getView().setStyle("-fx-border-color: black");
            }
        },cardsManipulatingHandler);
        setUpTheBoard();
        board.setOnMouseClicked((event -> {
            Node node = event.getPickResult().getIntersectedNode();
            if (GridPane.getColumnIndex(node) != null && GridPane.getRowIndex(node)!=null ) {
                int column = GridPane.getColumnIndex(node);
                int row = GridPane.getRowIndex(node);
                if (figuresRepository.checkIfAlly(column,row)){
                    selectedFigure = figuresRepository.findFigure(column,row).orElseThrow(RuntimeException::new);
                } else {
                    if (selectedCard != null && row == BoardManager.BOARD_ROWS - 1 && !figuresRepository.checkIfExists(column, row)) {
                        figuresRepository.addAlliedFigure(new Figure(selectedCard.getUnit().getId(), column, row));
                        //worrying about it
                        cardsRepository.remove(selectedCard);
                        selectedCard = null;
                    } else if (selectedFigure != null &&
                            Math.abs(selectedFigure.getRow() - row) <= 1 && Math.abs(selectedFigure.getColumn() - column) <= 1) {
                        if (figuresRepository.checkIfEnemy(column, row)) {
                            attackingMap.put(selectedFigure, figuresRepository.findFigure(column, row).orElseThrow(RuntimeException::new));
                            listManipulations.append(selectedFigure.getUnit().getName() + " is attacking " +
                                    figuresRepository.findFigure(column, row).orElseThrow(RuntimeException::new).getUnit().getName());
                        } else if (!figuresRepository.checkIfExists(column, row)) {
                            figuresRepository.removeFigure(selectedFigure);
                            Figure newFigure = new Figure(selectedFigure.getUnit().getId(), column, row);
                            figuresRepository.addAlliedFigure(newFigure);
                            selectedFigure = null;
                        }
                    } else{
                        return;
                    }
                    //TODO("Оправка на сервак")
                }
            }
        }));
    }
    private void setUpTheBoard(){
        for (int i = 0; i < BoardManager.BOARD_COLUMNS; i++) {
            board.addColumn(i);
            ColumnConstraints constraints = new ColumnConstraints(30);
            constraints.setPercentWidth(80/BoardManager.BOARD_ROWS);
            constraints.setHalignment(HPos.CENTER);
            board.getColumnConstraints().add(constraints);
        }
        for (int i = 0; i < BoardManager.BOARD_ROWS; i++) {
            board.addRow(i);
            RowConstraints constraints = new RowConstraints(30);
            constraints.setPercentHeight(80/BoardManager.BOARD_ROWS);
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


}
