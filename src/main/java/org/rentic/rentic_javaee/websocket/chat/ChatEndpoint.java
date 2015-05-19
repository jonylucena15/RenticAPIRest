package org.rentic.rentic_javaee.websocket.chat;

import org.rentic.rentic_javaee.model.Conversa;
import org.rentic.rentic_javaee.model.Missatge;
import org.rentic.rentic_javaee.model.Objecte;
import org.rentic.rentic_javaee.service.ConversaService;
import org.rentic.rentic_javaee.util.FromJSONObject;
import org.rentic.rentic_javaee.util.ToJSON;

import java.io.IOException;
import java.util.LinkedList;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;


/**
 * Created by Jony Lucena on 13/05/2015.
 */
/*
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
*/



@ServerEndpoint(value="/chat/{chat-id}")
public class ChatEndpoint {

    @EJB
    ConversaService conversaService;


    @Inject
    ToJSON toJSON;


    public static class missatgeConversa {
        public String missatge;
        public String dataHora;
        public Boolean enviat;
        public Long userId;
    }

    private static final LinkedList<Session> clients = new LinkedList<Session>();

    @OnOpen
    public void onOpen(Session session) {
        clients.add(session);
    }

    @OnMessage
    public void onMessage(String message, Session client,@PathParam("chat-id") String chatId) throws Exception {
        missatgeConversa m= FromJSONObject.getObject(missatgeConversa.class, message);

        if(clients.size()==2) {
            if (clients.get(0)!= client)
                clients.get(0).getBasicRemote().sendText(toJSON.Object(m));
            else
                clients.get(1).getBasicRemote().sendText(toJSON.Object(m));

            conversaService.addMissatge(chatId,m.userId,m.missatge,false);
        }else
            conversaService.addMissatge(chatId,m.userId,m.missatge,false);
    }


    @OnClose
    public void onClose(Session peer) {
        clients.remove(peer);
    }
}