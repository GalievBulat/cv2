package view.services;

import client.Client;
import javafx.application.Platform;
import javafx.util.Pair;
import server.helper.IOHandler;
import view.controllers.GameSpaceController;
import view.models.Message;

import java.util.List;


public class ServerListenerThread {
    private final IOHandler helper = new IOHandler();
    private final Client client;
    private final GameSpaceController controller;
    public ServerListenerThread(Client client, GameSpaceController controller) {
        this.client = client;
        this.controller = controller;
    }
    public void execute(){
        new Thread(()->listenToServer(client,controller)).start();
    }
    private void listenToServer(Client client, GameSpaceController controller){
        while (client.isAlive()){
            List<Message> messages = client.getMessages();
            if (messages.size()>0)
            for (Message message: messages){
                //TODO
                System.out.println(message);
                String text = message.getText().trim();
                if (text.charAt(0) == '/'){
                    String[] partsOfData = text.split(" ");
                    if(partsOfData[0].equals("/c")) {
                        controller.setRole(Integer.parseInt(partsOfData[1]));
                    } else if(partsOfData[0].equals("/d")){
                        if(partsOfData[1].equals("dp")) {
                            Pair<Byte,Byte> coords=helper.parseCoordinates(partsOfData[3]);
                            Platform.runLater(()-> {
                                controller.deploy(message.getUser().equals(client.getUser().getName()),
                                        Integer.parseInt(partsOfData[2]), coords.getKey(),coords.getValue());
                            });
                        }else if (partsOfData[1].equals("mv")){
                            Pair<Byte,Byte> coords1=helper.parseCoordinates(partsOfData[2]);
                            Pair<Byte,Byte> coords2=helper.parseCoordinates(partsOfData[3]);
                            Platform.runLater(()->{
                                controller.move(message.getUser().equals(client.getUser().getName()),
                                        coords1.getKey(),coords1.getValue(),
                                        coords2.getKey(),coords2.getValue());
                            });
                        } else if (partsOfData[1].equals("at")){
                            Pair<Byte,Byte> coords1=helper.parseCoordinates(partsOfData[2]);
                            Pair<Byte,Byte> coords2=helper.parseCoordinates(partsOfData[3]);
                            Platform.runLater(()-> {
                                controller.attack(
                                        coords1.getKey(), coords1.getValue(),
                                        coords2.getKey(), coords2.getValue());
                            });
                        }
                    }
                } else Platform.runLater(()-> {
                    controller.print(message.toString());
                });
            }
        }
    }
}
