package view.services;

import protocol.ClientCommunication;
import javafx.application.Platform;
import javafx.util.Pair;
import protocol.CoordsParser;
import view.controllers.GameSpaceController;
import view.dao.UnitTypeRepository;
import view.model.Card;
import view.model.Message;

import java.util.List;


public class ServerListenerThread {
    private final CoordsParser helper = new CoordsParser();
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
                if (text.charAt(0) == '/'){
                    String[] partsOfData = text.split(" ");
                    if(partsOfData[0].equals("/c")) {
                        controller.setRole(Integer.parseInt(partsOfData[1]));
                    } else if (partsOfData[0].equals("/cd")){
                        Platform.runLater(()-> {
                            controller.getCardsRepository().add(new Card( repository.find(Integer.parseInt(partsOfData[1]))),
                                    controller.getCardsRepository().getSize());
                        });
                    } else if(partsOfData[0].equals("/dp")) {
                            Pair<Byte,Byte> coords=helper.parseCoordinates(partsOfData[2]);
                            Platform.runLater(()-> {
                                controller.print(message.getUser() + ": deployed unit at" +
                                        coords.getKey() + ";" + coords.getValue() );
                                controller.deploy(message.getUser().equals(clientCommunication.getUser().getName()),
                                        Integer.parseInt(partsOfData[1]), coords.getKey(),coords.getValue());
                            });
                    }else if (partsOfData[0].equals("/mv")){
                        Pair<Byte,Byte> coords1=helper.parseCoordinates(partsOfData[1]);
                        Pair<Byte,Byte> coords2=helper.parseCoordinates(partsOfData[2]);
                        Platform.runLater(()->{
                            controller.move(message.getUser().equals(clientCommunication.getUser().getName()),
                                    coords1.getKey(),coords1.getValue(),
                                    coords2.getKey(),coords2.getValue());
                        });
                    } else if (partsOfData[0].equals("/at")){
                        Pair<Byte,Byte> coords1=helper.parseCoordinates(partsOfData[1]);
                        Pair<Byte,Byte> coords2=helper.parseCoordinates(partsOfData[2]);
                        Platform.runLater(()-> {
                            controller.attack(
                                    coords1.getKey(), coords1.getValue(),
                                    coords2.getKey(), coords2.getValue());
                            controller.print(message.getUser() + ": attacked unit at " +
                                    coords2.getKey() + ";" + coords2.getValue() );
                        });
                    } else if (partsOfData[0].equals("/rv")){
                        Pair<Byte,Byte> coords=helper.parseCoordinates(partsOfData[1]);
                        Platform.runLater(()-> {
                            controller.remove(
                                    coords.getKey(), coords.getValue());
                            controller.print(message.getUser() + ": destroyed unit at " +
                                    coords.getKey() + ";" + coords.getValue() );
                        });
                    }  else if (partsOfData[0].equals("/go")){
                        Platform.runLater(()-> {
                            controller.print("Game over " + message.getUser() + " won the game!");
                        });
                    }
                } else Platform.runLater(()-> {
                    controller.print(message.toString());
                });
            }
        }
    }
}
