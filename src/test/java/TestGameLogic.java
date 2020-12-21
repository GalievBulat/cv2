import org.junit.jupiter.api.Test;
import protocol.ClientCommunication;
import server.model.User;
import server.service.GameService;
import view.model.Message;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static server.helper.Meta.CARDS_OVERALL_AMOUNT;

public class TestGameLogic {
    @Test
    public void gameServiceRejoinTest(){
        GameService gameService = new GameService();
        User player1 = new User();
        User player2 = new User();
        gameService.addPlayer(player1);
        gameService.addPlayer(player2);
        gameService.removePlayer(player2);
        gameService.addPlayer(player2);
        assertEquals(player2,gameService.getPlayer2());
    }
    @Test
    public void gameServiceFirstConnectedIsPlayer1(){
        GameService gameService = new GameService();
        User player1 = new User();
        gameService.addPlayer(player1);
        assertEquals(player1,gameService.getPlayer1());
    }
    @Test
    public void gameServiceMoveTest(){
        GameService gameService = new GameService();
        gameService.add(true,1,(byte) 1,(byte) 1);
        assertTrue(gameService.move(true,(byte) 1,(byte) 1,(byte) 2,(byte) 2));
    }

    @Test
    public void gameServiceGameOverTest(){
        GameService gameService = new GameService();
        gameService.setCards_given(CARDS_OVERALL_AMOUNT);
        assertTrue(gameService.isGameOver());
    }



}
