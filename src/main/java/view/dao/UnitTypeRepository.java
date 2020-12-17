package view.dao;

import view.model.UnitType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UnitTypeRepository {
    private final List<UnitType> types = new ArrayList<>();
    {
        types.add(new UnitType.Builder().id(0).name("Infantry").image("src/main/resources/xd.jpg").create());
        types.add(new UnitType.Builder().id(1).name("artillery").image("src/main/resources/xd.jpg").create());
        types.add(new UnitType.Builder().id(2).name("cavalry").image("src/main/resources/xd.jpg").create());
    }
    public UnitType find (int id){
        return types.get(id);
    }
    public UnitType getRandom(){
        return types.get(new Random().nextInt(types.size()));
    }
}
