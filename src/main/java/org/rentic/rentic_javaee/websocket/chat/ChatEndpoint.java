package org.rentic.rentic_javaee.websocket.chat;

/**
 * Created by Jony Lucena on 13/05/2015.
 */
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/chat")
public class ChatEndpoint {
    @OnMessage
    public void message(String message, Session client) throws IOException, EncodeException {
        System.out.println("message: " + message);
        for (Session peer : client.getOpenSessions()) {
            System.out.println(peer.getBasicRemote().toString());
            peer.getBasicRemote().sendText(message);
        }
    }

}