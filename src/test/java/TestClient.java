import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import org.junit.jupiter.api.Test;
import view.interfaces.ViewManipulations;
import view.services.BoardManager;
import view.services.GridManipulations;

import static org.junit.jupiter.api.Assertions.*;


public class TestClient {
    @Test
    public void boardManagerAddingTest(){
        BoardManager boardManager = new BoardManager();
        boardManager.addAlly(1,1);
        assertTrue(
                boardManager.checkIfAlly(1,1));
    }
    @Test
    public void boardManagerRemovingTest(){
        BoardManager boardManager = new BoardManager();
        boardManager.addAlly(1,1);
        boardManager.remove(1,1);
        assertFalse(
                boardManager.checkIfAlly(1,1));
    }
    @Test
    public void viewManipulatorViewAdding(){
        ViewManipulations viewManipulations = new GridManipulations(new GridPane());
        Node circle = new Circle();
        viewManipulations.add(circle,1);
        assertTrue(viewManipulations.getAll().contains(circle));
    }
}
