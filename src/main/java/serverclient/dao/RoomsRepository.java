package serverclient.dao;

import serverclient.server.Room;

import java.util.ArrayList;
import java.util.List;

public class RoomsRepository {
    private final List<Room> rooms = new ArrayList<>();
    {
        rooms.add(new Room(0));
    }

    public  List<Room> getRooms() {
        return rooms;
    }
    public Room getRoom(int num){
        return rooms.get(num);
    }
    public void addRoom(){
        rooms.add(new Room(rooms.size()));
    }
    public Room findVacantRoom(){
        if(rooms.get(rooms.size()-1).isVacant()){
            return rooms.get(rooms.size()-1);
        } else{
            addRoom();
            return rooms.get(rooms.size()-1);
        }

    }
}
