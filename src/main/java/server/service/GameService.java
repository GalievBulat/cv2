package server.service;

import server.dao.UnitTypeRepository;
import server.model.Unit;

import java.util.*;

public class GameService {
    private final ServiceBoardManager boardManager = new ServiceBoardManager();
    UnitTypeRepository unitTypeRepository = new UnitTypeRepository();
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
    public boolean attack(boolean cl1,byte x1, byte y1, byte x2,byte y2){
        if (boardManager.get(x1, y1).isPresent() && boardManager.get(x2, y2).isPresent()) {
            Unit attacker = boardManager.get(x1, y1).get();
            Unit attacked = boardManager.get(x2, y2).get();
            if (cl1 && (units1.contains(attacker) && units2.contains(attacked))) {
                attacked.getAttacked(attacked.getDamage());
                if (attacked.getHealth()<=0){
                    boardManager.remove(x2,y2);
                    units2.remove(attacked);
                    return true;
                }
            }else if (!cl1 && (units2.contains(attacker) && units1.contains(attacked))){
                attacked.getAttacked(attacked.getDamage());
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

}
