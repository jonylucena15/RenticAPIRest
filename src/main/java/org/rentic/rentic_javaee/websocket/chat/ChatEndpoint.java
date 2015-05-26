package org.rentic.rentic_javaee.websocket.chat;

import org.rentic.rentic_javaee.model.Missatge;
import org.rentic.rentic_javaee.service.ConversaService;
import org.rentic.rentic_javaee.util.FromJSONObject;
import org.rentic.rentic_javaee.util.ToJSON;

import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Jony Lucena.
 */
@ServerEndpoint(value="/chat/{chat-id}")
public class ChatEndpoint {

    @Inject
    ConversaService conversaService;

    @Inject
    ToJSON toJSON;

    public static class missatgeConversa {
        public String missatge;
        public Long userId;
    }

    private static final LinkedList<Session> clients = new LinkedList<Session>();

    @OnOpen
    public void onOpen(Session session) {
        clients.add(session);
    }

    @OnMessage
    public void onMessage(String message, Session client,@PathParam("chat-id") Long chatId) throws Exception {
        missatgeConversa m= FromJSONObject.getObject(missatgeConversa.class, message);
        conversaService.addMissatge(chatId,m.userId,m.missatge,false);

        if(clients.size()==2) {
            List<Missatge> missatges = (List<Missatge>) conversaService.obtenirMissatgesNoEnviats(m.userId, chatId);
            if (clients.get(0) != client)
                clients.get(0).getBasicRemote().sendText(toJSON.Object(missatges));
            else
                clients.get(1).getBasicRemote().sendText(toJSON.Object(missatges));
            conversaService.canviarEstatMissatges(missatges);
        }
    }


    @OnClose
    public void onClose(Session peer) {
        clients.remove(peer);
    }
}