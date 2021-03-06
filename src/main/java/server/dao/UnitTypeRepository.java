package server.dao;

import server.model.UnitType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UnitTypeRepository {
    private final List<UnitType> types = new ArrayList<>();
    {
        types.add(new UnitType.Builder().id(0).name("Infantry").damage((byte) 30).health((byte) 100).create());
        types.add(new UnitType.Builder().id(1).name("artillery").damage((byte) 35).health((byte) 120).create());
        types.add(new UnitType.Builder().id(2).name("cavalry").damage((byte) 45).health((byte) 120).create());
    }
    public UnitType find (int id){
        return types.get(id);
    }
    public UnitType getRandom(){
        return types.get(new Random().nextInt(types.size()));
    }
}
