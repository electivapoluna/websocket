/**
 * Manejar el tablero y flujo del juego
 */
function Tablero() {
    "use strict"
    var _tateti = new TaTeTi()
    var _jugador
    var _oponente
    var conexion = new Conexion('ws://localhost:8080/websocket/juegoserver')
    var that = this

    /**
     * Genera el tablero vacio
     */
    this.generarTablero = function () {
        $('#tablero').empty()
        for (var fila = 0; fila < 3; fila++) {
            for (var columna = 0; columna < 3; columna++) {
                $('#tablero').append('<li><div id="' + fila + '-' + columna + '" class="celda marcable"></div></li>')
            }
        }

    }

    function presentacion(event) {
        conexion.enviar(JSON.stringify({
            tipo: "iniciar",
            nombre: _jugador.getNombre(),
            clave: _jugador.getClave()
        }))
    }

    function marcar(event, celdaMarcada) {
        var turnoActual = _tateti.getTurno()
        var celda = celdaMarcada ? celdaMarcada.split('-') : $(this).attr('id').split('-')
        var celdaNodo = celdaMarcada ? $('#' + celdaMarcada) : $(this)
        if (_tateti.marcar(celda[0], celda[1])) {
            var turnoSiguiente = _tateti.getTurno()
            celdaNodo.toggleClass('celda-marcado-' + turnoActual)
            celdaNodo.toggleClass('marcable')
            celdaNodo.off('click') // Desvincular el evento de esta celda
            $('#jugador').toggleClass('jugador-' + turnoActual)
            $('#jugador').toggleClass('jugador-' + turnoSiguiente)
            if (turnoActual === _jugador.getSimbolo()) {
                conexion.enviar(JSON.stringify({
                    tipo: "marcar",
                    celda: $(this).attr('id'),
                    clave: _jugador.getClave(),
                    nombre: _oponente.getNombre()
                }))
                $('.marcable').off('click')
            } else {
                $('.marcable').on('click', marcar)
            }
        }

        console.log(_tateti.getTurno())
        // revisa si ya hay ganador
        var ganador = _tateti.ganador()
        if (ganador === null) {
            $('.resultado').html('Empate')
            $('#info-turno').empty()
        } else if (ganador !== false) {
            $('.resultado').html('El ganador es ' + (_jugador.getSimbolo() === ganador ? _jugador.getNombre() : _oponente.getNombre()) + '<span class="jugador-' + ganador + '"></span>')
            $('.marcable').off('click').toggleClass('marcable') // Desvincular el evento click a todas las celdas y quitar la clase marcable
            $('#info-turno').empty()
        }
    }

    this.iniciar = function (jugador, oponente) {
        _jugador = jugador
        _oponente = oponente
        conexion.abrir()
        conexion.onOpen(presentacion)

        _tateti.iniciar()
        that.generarTablero()
        var infoJugadores = '<p>' + _jugador.getNombre() + '<span class="jugador-' + _jugador.getSimbolo() + '"></span></p>'
        infoJugadores += '<p>' + _oponente.getNombre() + '<span class="jugador-' + _oponente.getSimbolo() + '"></span></p>'
        $('#info-jugadores').html(infoJugadores)
        $('#info-turno').html('<p>Es el turno de <span id="jugador" class="jugador-' + _tateti.getTurno() + '"></span></p>')

        if (_tateti.getTurno() === _jugador.getSimbolo()) {
            $('.marcable').on('click', marcar)
        }

        conexion.onMessage(function (event) {
            var respuesta = JSON.parse(event.data)
            switch (respuesta.tipo) {
                case 'marcar':
                    console.log("Marcado solicitado por el servidor")
                    marcar(event, respuesta.celda)
                    break
                case 'jugador-salio':
                    alert('El oponente salio de la partida')
                    $(window).off('beforeunload')
                    break
                case 'sesion-cerrada':
                    alert('Juego bloqueado, un usuario cerro su sesion')
                    $('.marcable').off('click')
                    $(window).off('beforeunload')
                    break
            }
        })
        console.log(_tateti.getTurno())
        $('.resultado').empty()
    }

    $(window).on('beforeunload', function () {
        conexion.enviar(JSON.stringify({
            tipo: "jugador-salio",
            clave: _jugador.getClave()
        }))
        return undefined
    })
}
