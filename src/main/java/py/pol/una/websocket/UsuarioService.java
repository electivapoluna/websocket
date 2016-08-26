package py.pol.una.websocket;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
import py.pol.una.Jugador;
import py.pol.una.Partidas;
import py.pol.una.Usuario;

@ServerEndpoint("/usuarioserver")
public class UsuarioService {

    // Usuarios conectados
    private static Map<Session, Usuario> usuarios = Collections.synchronizedMap(new HashMap<Session, Usuario>());

    @OnOpen
    public void onOpen(Session sesion) {
        System.out.println(sesion.getId() + " conexión abierta");
    }

    @OnClose
    public void onClose(Session sesion) throws IOException {
        System.out.println(sesion.getId() + " conexión cerrada");
        Usuario usuario = usuarios.get(sesion);
        /* if (usuario != null) { // Si no esta registrado
            Set<Session> oponentes = Partidas.jugadores.get(sesion);
            if (oponentes != null) { // Si no tiene oponentes
                for (Session oponente : oponentes) {
                    Partidas.jugadores.get(oponente).remove(sesion);
                }
                for (Map.Entry<String, Jugador[]> entry : Partidas.partidas.entrySet()) {
                    System.out.println("Entro para eliminar notificar las partidas");
                    String clave = entry.getKey();
                    Jugador[] jugadores = entry.getValue();
                    boolean existe = false;
                    for (Jugador jugador : jugadores) {
                        if (jugador.getNombre().equals(usuario.getNombre())) {
                            existe = true;
                        }
                    }
                    if (existe) {
                        for (Jugador jugador : jugadores) {
                            JSONObject respuesta = new JSONObject();
                            respuesta.put("tipo", "sesion-cerrada");
                            jugador.getSesion().getBasicRemote().sendText(respuesta.toJSONString());
                        }
                    }
                }

            }
        } */
        Partidas.jugadores.remove(sesion);
        usuarios.remove(sesion);
        this.enviarListaUsuarios(); // actualizar la lista de usuarios conectados
    }

    @OnMessage
    public void onMessage(String mensaje, Session sesion) throws IOException {
        JSONParser parser = new JSONParser();
        try {
            JSONObject peticion = (JSONObject) parser.parse(mensaje);
            String tipo = (String) peticion.get("tipo");
            switch (tipo) {
                case "agregar-usuario": // Para agregar un usuario
                    if (!this.existeUsuario((String) peticion.get("nombre"))) {
                        usuarios.put(sesion, new Usuario((String) peticion.get("nombre"))); // registra al usuario
                        Partidas.iniciarListaOponentes(sesion);

                        JSONObject respuesta = new JSONObject();
                        respuesta.put("tipo", "agregar-usuario");
                        respuesta.put("resultado", true);
                        sesion.getBasicRemote().sendText(respuesta.toJSONString());

                        this.enviarListaUsuarios();
                    } else { // si ya existe se rechaza
                        JSONObject respuesta = new JSONObject();
                        respuesta.put("tipo", "agregar-usuario");
                        respuesta.put("resultado", false);
                        sesion.getBasicRemote().sendText(respuesta.toJSONString());
                    }
                    break;
                case "peticion-juego": // Pedir que inicie una partida.
                    String idOponente = (String) peticion.get("id_oponente");
                    Session sesionOponente = this.getSesion(idOponente);
                    // se puede iniciar
                    if (!Partidas.existeOponente(sesion, sesionOponente)) { // No hay una partida con el oponente
                        // Preparar los datos de los jugadores
                        Usuario usuario = (Usuario) usuarios.get(sesion);
                        Usuario usuarioOponente = (Usuario) usuarios.get(sesionOponente);
                        Jugador jugador = new Jugador(usuario.getNombre());
                        Jugador jugadorOponente = new Jugador(usuarioOponente.getNombre());

                        String clave = Partidas.establecerPartida(jugador, jugadorOponente);
                        Partidas.establecerOponente(sesion, sesionOponente);
                        Partidas.establecerOponente(sesionOponente, sesion);

                        // responde al usuario que pide iniciar la partida
                        JSONObject respuesta = new JSONObject();
                        respuesta.put("tipo", "peticion-juego");

                        JSONObject jsonJugador = new JSONObject();
                        jsonJugador.put("nombre", jugadorOponente.getNombre());

                        respuesta.put("oponente", jsonJugador);
                        respuesta.put("resultado", clave);
                        sesion.getBasicRemote().sendText(respuesta.toJSONString());

                        // avisa al oponente para iniciar el juego
                        respuesta = new JSONObject();
                        respuesta.put("tipo", "iniciar-juego");

                        jsonJugador = new JSONObject();
                        jsonJugador.put("nombre", jugador.getNombre());

                        respuesta.put("oponente", jsonJugador);
                        respuesta.put("resultado", clave);

                        sesionOponente.getBasicRemote().sendText(respuesta.toJSONString());
                    } else { // No se puede iniciar la partida
                        // responde al usuario que pide iniciar la partida
                        JSONObject respuesta = new JSONObject();
                        respuesta.put("tipo", "peticion-juego");
                        respuesta.put("resultado", false);
                        sesion.getBasicRemote().sendText(respuesta.toJSONString());
                    }
                    break;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @OnError
    public void onError(Session sesion, Throwable thr) {
        System.out.println(thr.getMessage());
    }

    /**
     * Verifica si ya existe un usuario.
     *
     * @param nombre Nombre de usuario
     * @return true si existe, false en caso contrario
     */
    private boolean existeUsuario(String nombre) {
        for (Map.Entry<Session, Usuario> entry : usuarios.entrySet()) {
            Usuario usuario = entry.getValue();

            if (usuario.getNombre().equals(nombre)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Envia la lista de usuarios conectados.
     */
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
                        obj.put("sesion", entry.getKey().getId());
                        listaUsuarios.add(obj);
                    }
                }
                respuesta.put("resultado", listaUsuarios);
                if (sesionUsuario.isOpen()) {
                    sesionUsuario.getBasicRemote().sendText(respuesta.toJSONString());
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public Session getSesion(String idSesion) {
        Set<Session> sesiones = usuarios.keySet();

        for (Session sesion : sesiones) {
            if (sesion.getId().equals(idSesion)) {
                return sesion;
            }
        }

        return null;
    }

    public static void partidaTerminada(String nombreJugador, String nombreOponente) {
        Session sesionJugador = null;
        Session sesionOponente = null;

        for (Map.Entry<Session, Usuario> entry : usuarios.entrySet()) {
            Usuario usuario = entry.getValue();
            Session sesion = entry.getKey();

            if (usuario.getNombre().equals(nombreJugador)) {
                sesionJugador = sesion;
            } else if (usuario.getNombre().equals(nombreOponente)) {
                sesionOponente = sesion;
            }
        }

        Partidas.eliminarOponente(sesionJugador, sesionOponente);
        Partidas.eliminarOponente(sesionOponente, sesionJugador);
    }
}
