package py.pol.una;

import javax.websocket.Session;

/**
 *
 * @author Sergio Alcaraz
 */
public class Jugador extends Usuario {

    private Session sesion;

    public Jugador(String nombre) {
        super(nombre);
    }

    public Jugador(String nombre, String simbolo, Session sesion) {
        super(nombre);
        this.sesion = sesion;
    }

    public Session getSesion() {
        return this.sesion;
    }

    public void setSesion(Session sesion) {
        this.sesion = sesion;
    }
}
