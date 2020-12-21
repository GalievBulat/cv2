package view.model;

public class User {
    private String name;
    private int[] roomsAvailable;
    private int currentChat = -1;

    public User() {
    }

    public User(String name, int[] roomsAvailable) {
        this.name = name;
        this.roomsAvailable = roomsAvailable;
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

    public int[] getRoomsAvailable() {
        return roomsAvailable;
    }

    public void setRoomsAvailable(int[] roomsAvailable) {
        this.roomsAvailable = roomsAvailable;
    }
}
