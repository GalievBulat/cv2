package protocol.data;

public enum  Data {
    GAME_OVER("/go"), MOVE("/mv"), REMOVE("/rv"), OTHER(""),
    ATTACK("/at"), DEPLOY("/dp"), CARD_GIVING("/cd"),
    CONNECT("/c"), ERROR("/er"), ENTER("/e"),STOP("/s"),
    DISCONNECT("/l");
    private final String command;

    Data(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
