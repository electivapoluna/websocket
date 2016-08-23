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

@ServerEndpoint("/usuarioserver")
public class UsuarioService {

    private static Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());

    @OnOpen
    public void onOpen(Session session) {
        System.out.println(session.getId() + " has opened a connection");
    }

    @OnClose
    public void onClose(Session peer) {
        System.out.println(peer.getId() + " has closed a connection");
        // peers.remove(peer);
    }

    @OnMessage
    public void onMessage(String mensaje, Session session) throws IOException {
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
