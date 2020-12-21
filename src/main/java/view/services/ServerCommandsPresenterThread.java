package view.services;

import protocol.ClientCommunication;
import javafx.application.Platform;
import javafx.util.Pair;
import protocol.data.CommandData;
import view.controllers.GameSpaceController;
import view.dao.UnitTypeRepository;
import view.model.Card;
import view.model.Message;

import java.util.List;


public class ServerCommandsPresenterThread {
    private final ClientCommunication clientCommunication;
    private final GameSpaceController controller;
    private final UnitTypeRepository repository= new UnitTypeRepository();
    public ServerCommandsPresenterThread(ClientCommunication clientCommunication, GameSpaceController controller) {
        this.clientCommunication = clientCommunication;
        this.controller = controller;
    }
    public void executeInBackGround(){
        new Thread(()-> listenToServerAndExecuteCommands(clientCommunication,controller)).start();
    }
    private void listenToServerAndExecuteCommands(ClientCommunication clientCommunication, GameSpaceController controller){
        while (clientCommunication.isAlive()){
            List<Message> messages = clientCommunication.getMessages();
            if (messages.size()>0)
            for (Message message: messages){
                //TODO
                System.out.println(message);
                String text = message.getText().trim();
                CommandData commandType = CommandData.determineCommand(text);
                if(commandType == CommandData.CONNECT) {
                    controller.setRole(clientCommunication.getNumFromCommand(text,1));
                } else if (commandType == CommandData.CARD_GIVING){
                    Platform.runLater(()-> {
                        controller.getCardsRepository()
                                .add(new Card( repository.find(clientCommunication.getNumFromCommand(text,1))),
                                controller.getCardsRepository().getSize());
                    });
                } else if(commandType == CommandData.DEPLOY) {
                        Pair<Byte,Byte> coords= clientCommunication.getCoords(text,1);
                        Platform.runLater(()-> {
                            controller.deploy(message.getUser().equals(clientCommunication.getUser().getName()),
                                    clientCommunication.getNumFromCommand(text,2), coords.getKey(),coords.getValue());
                        });
                }else if (commandType == CommandData.MOVE){
                    Pair<Byte,Byte> coords1=clientCommunication.getCoords(text,1);
                    Pair<Byte,Byte> coords2=clientCommunication.getCoords(text,2);
                    Platform.runLater(()->{
                        controller.move(message.getUser().equals(clientCommunication.getUser().getName()),
                                coords1.getKey(),coords1.getValue(),
                                coords2.getKey(),coords2.getValue());
                    });
                } else if (commandType == CommandData.ATTACK){
                    Pair<Byte,Byte> coords1=clientCommunication.getCoords(text,1);
                    Pair<Byte,Byte> coords2=clientCommunication.getCoords(text,2);
                    Platform.runLater(()-> {
                        controller.attack(
                                coords1.getKey(), coords1.getValue(),
                                coords2.getKey(), coords2.getValue());
                        controller.print(message.getUser() + ": attacked unit at " +
                                coords2.getKey() + ";" + coords2.getValue() );
                    });
                } else if (commandType == CommandData.REMOVE){
                    Pair<Byte,Byte> coords=clientCommunication.getCoords(text,1);
                    Platform.runLater(()-> {
                        controller.remove(
                                coords.getKey(), coords.getValue());
                        controller.print(message.getUser() + ": destroyed unit at " +
                                coords.getKey() + ";" + coords.getValue() );
                    });
                }  else if (commandType == CommandData.GAME_OVER){
                    Platform.runLater(()-> {
                        controller.print("Game over " + message.getUser() + " won the game!");
                    });
                } else if (commandType == CommandData.OTHER) Platform.runLater(()-> {
                    controller.print(message.toString());
                });
            }
        }
    }
}
