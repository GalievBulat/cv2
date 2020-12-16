package server.dao;

import server.model.UnitType;

import java.util.ArrayList;
import java.util.List;

public class UnitTypeRepository {
    private final List<UnitType> types = new ArrayList<>();
    {
        types.add(new UnitType.Builder().id(0).name("Infantry").damage((byte) 10).health((byte) 100).create());
        types.add(new UnitType.Builder().id(1).name("Infantry").damage((byte) 15).health((byte) 120).create());
        types.add(new UnitType.Builder().id(2).name("artillery").damage((byte) 15).health((byte) 120).create());
        types.add(new UnitType.Builder().id(3).name("cavalry").damage((byte) 15).health((byte) 120).create());
    }
    public UnitType find (int id){
        return types.get(id);
    }
}
