package server.dao;

import server.service.maintainance.Room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomsRepository {
    private final Map<Integer,Room> rooms = new HashMap<>();
     public RoomsRepository(){
         Room room = new Room(0);
         rooms.put(0,room);
         rooms.put(1,new Room(1));
         rooms.put(2,new Room(2));
         rooms.put(3,new Room(3));
         rooms.put(4,new Room(4));
         rooms.put(5,new Room(5));

    }

    public  List<Room> getRooms() {
        return new ArrayList<>(rooms.values());
    }
    public Room getRoom(int num){
        return rooms.get(num);
    }
    public void add(Room room){
        rooms.put(room.getId(),room);
    }
    public List<Room> getVacantRooms(){
        List<Room> roomsList = new ArrayList<>();
        for (Room room: rooms.values()){
            if (room.isVacant()){
                roomsList.add(room);
            }
        }
        if(roomsList.size() == 0){
            Room newRoom = new Room(rooms.size());
            add(newRoom);
            roomsList.add(newRoom);
        }
        return roomsList;
    }
}
