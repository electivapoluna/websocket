package py.pol.una.websocket;

import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import py.pol.una.Jugador;
import py.pol.una.Partidas;

@ServerEndpoint("/juegoserver")
public class JuegoService {

    @OnOpen
    public void onOpen(Session sesion) {
        System.out.println(sesion.getId() + ": entro al juego");
    }

    @OnClose
    public void onClose(Session sesion) {
        System.out.println(sesion.getId() + " salio del juego");
    }

    @OnMessage
    public void onMessage(String mensaje, Session sesion) throws IOException {
        JSONParser parser = new JSONParser();
        try {
            JSONObject peticion = (JSONObject) parser.parse(mensaje);
            String tipo = (String) peticion.get("tipo");
            switch (tipo) {
                case "iniciar": {
                    String nombre = (String) peticion.get("nombre");
                    String clave = (String) peticion.get("clave");
                    Jugador[] jugadores = Partidas.partidas.get(clave);
                    for (Jugador jugador : jugadores) {
                        if (jugador.getNombre().equals(nombre)) {
                            jugador.setSesion(sesion);
                        }
                    }
                    break;
                }
                case "marcar": {
                    String nombre = (String) peticion.get("nombre");
                    String clave = (String) peticion.get("clave");
                    Jugador[] jugadores = Partidas.partidas.get(clave);
                    for (Jugador jugador : jugadores) {
                        if (jugador.getNombre().equals(nombre)) {
                            Session sesionOponente = jugador.getSesion();
                            JSONObject respuesta = new JSONObject();
                            respuesta.put("tipo", "marcar");
                            respuesta.put("celda", (String) peticion.get("celda"));
                            sesionOponente.getBasicRemote().sendText(respuesta.toJSONString());
                        }
                    }
                    break;
                }
                case "jugador-salio": {
                    String clave = (String) peticion.get("clave");
                    Jugador[] jugadores = Partidas.partidas.get(clave);
                    if (jugadores != null) {
                        for (Jugador jugador : jugadores) {
                            if (!jugador.getSesion().getId().equals(sesion.getId())) {
                                Session sesionOponente = jugador.getSesion();
                                JSONObject respuesta = new JSONObject();
                                respuesta.put("tipo", "jugador-salio");
                                sesionOponente.getBasicRemote().sendText(respuesta.toJSONString());
                            }
                        }
                        String jugador = jugadores[0].getNombre();
                        String oponente = jugadores[1].getNombre();
                        Partidas.eliminarPartida(clave);
                        UsuarioService.partidaTerminada(jugador, oponente);
                    }
                    break;

                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @OnError
    public void onError(Session sesion, Throwable thr) {
        System.out.println(thr.getMessage());
    }
}
