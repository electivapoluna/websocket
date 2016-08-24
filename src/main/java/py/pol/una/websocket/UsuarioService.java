package py.pol.una.websocket;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import py.pol.una.Usuario;

@ServerEndpoint("/usuarioserver")
public class UsuarioService {

    private static Map<Session, Usuario> usuarios = Collections.synchronizedMap(new HashMap<Session, Usuario>());

    @OnOpen
    public void onOpen(Session session) {
        System.out.println(session.getId() + " conexión abierta");
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println(session.getId() + " conexión cerrada");
        usuarios.remove(session);
        this.enviarListaUsuarios();
    }

    @OnMessage
    public void onMessage(String mensaje, Session session) throws IOException {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(mensaje);
            JSONObject peticion = (JSONObject) obj;
            String tipo = (String) peticion.get("tipo");
            switch (tipo) {
                case "agregar-usuario":
                    if (!this.existeUsuario((String) peticion.get("nombre"))) {
                        JSONObject respuesta = new JSONObject();
                        respuesta.put("tipo", "agregar-usuario");
                        respuesta.put("resultado", true);
                        usuarios.put(session, new Usuario((String) peticion.get("nombre")));
                        System.out.println(respuesta.toJSONString());
                        session.getBasicRemote().sendText(respuesta.toJSONString());
                        this.enviarListaUsuarios();
                    } else {
                        JSONObject respuesta = new JSONObject();
                        respuesta.put("tipo", "agregar-usuario");
                        respuesta.put("resultado", false);
                        System.out.println(respuesta.toJSONString());
                        session.getBasicRemote().sendText(respuesta.toJSONString());
                    }
                    break;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @OnError
    public void onError(Session session, Throwable thr) {
        System.out.println(thr.getMessage());
    }

    private boolean existeUsuario(String nombre) {
        for (Map.Entry<Session, Usuario> entry : usuarios.entrySet()) {
            Usuario usuario = entry.getValue();

            if (usuario.getNombre().equals(nombre)) {
                return true;
            }
        }
        return false;
    }

    private void enviarListaUsuarios() {
        try {
            for (Session sesionUsuario : usuarios.keySet()) {
                JSONObject respuesta = new JSONObject();
                respuesta.put("tipo", "cargar-lista-usuarios");
                JSONArray listaUsuarios = new JSONArray();
                for (Map.Entry<Session, Usuario> entry : usuarios.entrySet()) {
                    if (!sesionUsuario.getId().equals(entry.getKey().getId())) {
                        JSONObject obj = new JSONObject();
                        obj.put("nombre", entry.getValue().getNombre());
                        obj.put("session", entry.getKey().getId());
                        listaUsuarios.add(obj);
                    }
                }
                respuesta.put("resultado", listaUsuarios);
                sesionUsuario.getBasicRemote().sendText(respuesta.toJSONString());
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
