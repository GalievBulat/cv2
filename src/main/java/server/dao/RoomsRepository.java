package server.dao;

import server.service.maintainance.Room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomsRepository {
    private final Map<Integer,Room> rooms = new HashMap<>();
     public RoomsRepository(){
         Room room = new Room();
         room.setId(0);
         rooms.put(0,room);
    }

    public  List<Room> getRooms() {
        return new ArrayList<>(rooms.values());
    }
    public Room getRoom(int num){
        return rooms.get(num);
    }
    public void add(Room room){
        rooms.put(rooms.size(),room);
    }
    public List<Room> findVacantRooms(){
        List<Room> roomsList = new ArrayList<>();
        for (Room room: rooms.values()){
            if (room.isVacant()){
                roomsList.add(room);
            }
        }
        return roomsList;
    }
}
