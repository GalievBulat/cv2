package server.model;

public class User {
    private String name;
    private int[] rooms;


    private int currentChat = -1;

    public User() {
    }

    public User(String name, int[] rooms) {
        this.name = name;
        this.rooms = rooms;
    }

    public int getCurrentChat() {
        return currentChat;
    }

    public void setCurrentChat(int currentChat) {
        //if(Arrays.stream(rooms).anyMatch((int chat)-> chat == currentChat))
        this.currentChat = currentChat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int[] getRooms() {
        return rooms;
    }

    public void setRooms(int[] rooms) {
        this.rooms = rooms;
    }
}
