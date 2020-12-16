package server.service;

import server.dao.UnitTypeRepository;
import server.model.Unit;

import java.util.*;

public class GameService {
    private final ServiceBoardManager boardManager = new ServiceBoardManager();
    UnitTypeRepository unitTypeRepository = new UnitTypeRepository();
    private final LinkedList<Unit> units1 = new LinkedList<>();
    private final LinkedList<Unit> units2 = new LinkedList<>();
    public void move(boolean cl1,byte x1, byte y1, byte x2,byte y2){
        boardManager.get(x1,y1).ifPresent(unit-> {
            if(cl1 == unit.is1st()) {
                boardManager.remove(unit.x, unit.y);
                boardManager.addUnit(unit, x2, y2);
            }
        });
    }
    public void attack(boolean cl1,byte x1, byte y1, byte x2,byte y2){
        Unit attacker = boardManager.get(x1,y1).orElseThrow(RuntimeException::new);
        Unit attacked = boardManager.get(x2,y2).orElseThrow(RuntimeException::new);
        if (cl1 == attacker.is1st() && cl1 != attacked.is1st()) {
            attacked.getAttacked(attacked.getDamage());
        }
    }
    public void add(boolean cl1, int type, byte x,byte y) {
        Unit unit;
        if (cl1){
            unit = new Unit.Builder().type(unitTypeRepository.find(type)).create();
            units1.add(unit);
        }else {
            unit = new Unit.Builder().type(unitTypeRepository.find(type)).create();
            units2.add(unit);
        }
        boardManager.addUnit(unit,x, y);
    }

}
