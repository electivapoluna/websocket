package py.pol.una.websocket;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/chatserver")
public class ChatService {

    private static Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());

    @OnOpen
    public void onOpen(Session session) {
        System.out.println(session.getId() + " has opened a connection");
        try {
            peers.add(session);
            session.getBasicRemote().sendText("Connection Established");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session peer) {
        System.out.println(peer.getId() + " has closed a connection");
        peers.remove(peer);
    }

    @OnMessage
    public void broadcastFigure(String mensaje, Session session) throws IOException {
        System.out.println("broadcast message: " + mensaje + ". Peers #: " + peers.size());

        for (Session peer : peers) {
            if (!peer.equals(session)) {
                System.out.println("Enviando mensaje a " + peer.getId());
                peer.getBasicRemote().sendText(mensaje);
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable thr) {
        System.out.println(thr.getMessage());
    }
}
