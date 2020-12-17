package server.service;

import server.dao.UnitTypeRepository;
import server.model.Unit;
import server.model.User;

import java.util.*;

import static server.helper.Meta.CARDS_OVERALL_AMOUNT;
import static server.helper.Meta.ENCIRCLEMENT_DAMAGE_COEFFICIENT;

public class GameService {
    private User player1;
    private User player2;
    private int cards_given = 0;
    private final ServiceBoardManager boardManager = new ServiceBoardManager();
    private final UnitTypeRepository unitTypeRepository = new UnitTypeRepository();
    private final LinkedList<Unit> units1 = new LinkedList<>();
    private final LinkedList<Unit> units2 = new LinkedList<>();
    public boolean move(boolean cl1,byte x1, byte y1, byte x2,byte y2){
        if (boardManager.get(x1,y1).isPresent()) {
            Unit unit = boardManager.get(x1, y1).get();
            if ((cl1 && units1.contains(unit)) || (!cl1 && units2.contains(unit))) {
                boardManager.remove(x1, y1);
                boardManager.addUnit(unit, x2, y2);
                return true;
            }
        }
        return false;
    }

    public UnitTypeRepository getUnitTypeRepository() {
        return unitTypeRepository;
    }

    public boolean attack(boolean cl1, byte x1, byte y1, byte x2, byte y2){
        if (boardManager.get(x1, y1).isPresent() && boardManager.get(x2, y2).isPresent()) {
            Unit attacker = boardManager.get(x1, y1).get();
            Unit attacked = boardManager.get(x2, y2).get();
            if (cl1 && (units1.contains(attacker) && units2.contains(attacked))) {
                if (boardManager.checkIfExists(x1,y1) )
                attacked.getAttacked((byte) ((ENCIRCLEMENT_DAMAGE_COEFFICIENT *(boardManager.getNeighboursCount(x2,y2)/8))*
                                        attacker.getDamage()));
                if (attacked.getHealth()<=0){
                    boardManager.remove(x2,y2);
                    units2.remove(attacked);
                    return true;
                }
            }else if (!cl1 && (units2.contains(attacker) && units1.contains(attacked))){
                attacked.getAttacked((byte) ((ENCIRCLEMENT_DAMAGE_COEFFICIENT *(boardManager.getNeighboursCount(x2,y2)/8))*
                        attacker.getDamage()));
                if (attacked.getHealth()<=0){
                    boardManager.remove(x2,y2);
                    units1.remove(attacked);
                    return true;
                }
            }
        }
        return false;
    }
    public boolean add(boolean cl1, int type, byte x,byte y) {
        Unit unit;
        if (cl1){
            unit = new Unit.Builder().type(unitTypeRepository.find(type)).create();
            units1.add(unit);
        }else {
            unit = new Unit.Builder().type(unitTypeRepository.find(type)).create();
            units2.add(unit);
        }
        boardManager.addUnit(unit,x, y);
        return true;
    }
    public void removePlayer(User user){
        if (player1 == user)
            player1 = null;
        if (player2 == user)
            player2 = null;
    }
    public int addPlayer(User user){
        if (player1 == null){
            player1 = user;
            return 1;
        }else if(player2 == null){
            player2 = user;
            return 2;
        }
        return -1;
    }

    public User getPlayer1() {
        return player1;
    }

    public User getPlayer2() {
        return player2;
    }

    public void setPlayer1(User player1) {
        this.player1 = player1;
    }

    public void setPlayer2(User player2) {
        this.player2 = player2;
    }

    public int getCards_given() {
        return cards_given;
    }

    public void setCards_given(int cards_given) {
        this.cards_given = cards_given;
    }

    private boolean checkGameOver(){
        if (cards_given>=CARDS_OVERALL_AMOUNT && (units1.isEmpty() || units2.isEmpty())){
            return true;
        }
        return false;
    }

    public boolean isGameOver() {
        return checkGameOver();
    }
}
