package py.pol.una;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.websocket.Session;

/**
 * Jugadores y una lista de oponentes
 *
 * @author Sergio Alcaraz
 */
public class Partidas {

    // Registrar usuarios con sus oponentes
    public static Map<Session, Set<Session>> jugadores = Collections.synchronizedMap(new HashMap<Session, Set<Session>>());

    // Registrar partidas/juegos que se estan ejecutando
    public static Map<String, Jugador[]> partidas = Collections.synchronizedMap(new HashMap<String, Jugador[]>());

    /**
     * Verifica la existencia en la lista el oponente
     *
     * @param jugador
     * @param oponente
     * @return
     */
    public static boolean existeOponente(Session jugador, Session oponente) {
        return jugadores.get(jugador).contains(oponente);
    }

    public static void establecerOponente(Session jugador, Session oponente) {
        Set<Session> oponentes = jugadores.get(jugador);
        oponentes.add(oponente);
    }

    public static void eliminarOponente(Session jugador, Session oponente) {
        Set<Session> oponentes = jugadores.get(jugador);
        oponentes.remove(oponente);
    }

    public static String establecerPartida(Jugador jugador, Jugador oponente) {
        String clave = UUID.randomUUID().toString();

        Jugador[] jugadores = new Jugador[2];
        jugadores[0] = jugador;
        jugadores[1] = oponente;

        partidas.put(clave, jugadores);

        return clave;
    }

    public static void eliminarPartida(String clave) {
        partidas.remove(clave);
    }

    public static void iniciarListaOponentes(Session sesion) {
        jugadores.put(sesion, new HashSet<Session>()); // Lista de jugadores con sus oponentes
    }
}
