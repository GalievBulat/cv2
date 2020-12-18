package view.services;

import protocol.ClientCommunication;
import javafx.application.Platform;
import javafx.util.Pair;
import protocol.data.Data;
import protocol.helper.CoordsParser;
import view.controllers.GameSpaceController;
import view.dao.UnitTypeRepository;
import view.model.Card;
import view.model.Message;

import java.util.List;


public class ServerListenerThread {
    private final ClientCommunication clientCommunication;
    private final GameSpaceController controller;
    private final UnitTypeRepository repository= new UnitTypeRepository();
    public ServerListenerThread(ClientCommunication clientCommunication, GameSpaceController controller) {
        this.clientCommunication = clientCommunication;
        this.controller = controller;
    }
    public void execute(){
        new Thread(()->listenToServer(clientCommunication,controller)).start();
    }
    private void listenToServer(ClientCommunication clientCommunication, GameSpaceController controller){
        while (clientCommunication.isAlive()){
            List<Message> messages = clientCommunication.getMessages();
            if (messages.size()>0)
            for (Message message: messages){
                //TODO
                System.out.println(message);
                String text = message.getText().trim();
                Data commandType = clientCommunication.commandHandler(text);
                if(commandType == Data.CONNECT) {
                    controller.setRole(clientCommunication.getNumFromCommand(text,1));
                } else if (commandType == Data.CARD_GIVING){
                    Platform.runLater(()-> {
                        controller.getCardsRepository()
                                .add(new Card( repository.find(clientCommunication.getNumFromCommand(text,1))),
                                controller.getCardsRepository().getSize());
                    });
                } else if(commandType == Data.DEPLOY) {
                        Pair<Byte,Byte> coords= clientCommunication.getCoords(text,1);
                        Platform.runLater(()-> {
                            controller.print(message.getUser() + ": deployed unit at" +
                                    coords.getKey() + ";" + coords.getValue() );
                            controller.deploy(message.getUser().equals(clientCommunication.getUser().getName()),
                                    clientCommunication.getNumFromCommand(text,2), coords.getKey(),coords.getValue());
                        });
                }else if (commandType == Data.MOVE){
                    Pair<Byte,Byte> coords1=clientCommunication.getCoords(text,1);
                    Pair<Byte,Byte> coords2=clientCommunication.getCoords(text,2);
                    Platform.runLater(()->{
                        controller.move(message.getUser().equals(clientCommunication.getUser().getName()),
                                coords1.getKey(),coords1.getValue(),
                                coords2.getKey(),coords2.getValue());
                    });
                } else if (commandType == Data.ATTACK){
                    Pair<Byte,Byte> coords1=clientCommunication.getCoords(text,1);
                    Pair<Byte,Byte> coords2=clientCommunication.getCoords(text,2);
                    Platform.runLater(()-> {
                        controller.attack(
                                coords1.getKey(), coords1.getValue(),
                                coords2.getKey(), coords2.getValue());
                        controller.print(message.getUser() + ": attacked unit at " +
                                coords2.getKey() + ";" + coords2.getValue() );
                    });
                } else if (commandType == Data.REMOVE){
                    Pair<Byte,Byte> coords=clientCommunication.getCoords(text,1);
                    Platform.runLater(()-> {
                        controller.remove(
                                coords.getKey(), coords.getValue());
                        controller.print(message.getUser() + ": destroyed unit at " +
                                coords.getKey() + ";" + coords.getValue() );
                    });
                }  else if (commandType == Data.GAME_OVER){
                    Platform.runLater(()-> {
                        controller.print("Game over " + message.getUser() + " won the game!");
                    });
                } else if (commandType == Data.OTHER) Platform.runLater(()-> {
                    controller.print(message.toString());
                });
            }
        }
    }
}
