package protocol.data;

public enum CommandData {
    GAME_OVER("/go"), MOVE("/mv"), REMOVE("/rv"), OTHER(""),
    ATTACK("/at"), DEPLOY("/dp"), CARD_GIVING("/cd"),
    CONNECT("/c"), ERROR("/er"), ENTER("/e"),STOP("/s"),
    DISCONNECT("/l"), SHUTDOWN("/sd"), DATA("/d"), INIT("/i"),
    MESSAGE("/m");
    private final String command;

    CommandData(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
    public static CommandData determineCommand(String command){
        String firstPart = command.split(" ")[0];
        for (CommandData commandData: CommandData.values()){
            if(firstPart.equals(commandData.command))
                return commandData;
        }
        return CommandData.OTHER;
    }
}
