package view.model;



public class Message {
    private String userName;
    private String text;

    @Override
    public String toString() {
        String text = "";
        return text + "User: " + this.userName + "\nText: " + this.text ;
    }

    public Message(String name, String text) {
        userName = name;
        this.text = text;
    }

    public String getUser() {
        return userName;
    }

    public void setUser(String name) {
        this.userName = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
