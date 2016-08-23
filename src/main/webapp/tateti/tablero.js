/**
 * Manejar el tablero y flujo del juego
 */
function Tablero() {
  var _tateti = new TaTeTi()
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

  this.iniciar = function () {
    _tateti.iniciar()
    that.generarTablero()
    $('#info-turno').html('<p>Es el turno de <span id="jugador" class="jugador-' + _tateti.getTurno() + '"></span></p>')

    // Manejador del juego
    $('.marcable').on('click', function (event) {
      var turnoActual = _tateti.getTurno()
      var celda = $(this).attr('id').split('-')
      if (_tateti.marcar(celda[0], celda[1])) {
        var turnoSiguiente = _tateti.getTurno()
        $(this).toggleClass('celda-marcado-' + turnoActual)
        $(this).toggleClass('marcable')
        $(this).off('click') // Desvincular el evento de esta celda
        $('#jugador').toggleClass('jugador-' + turnoActual)
        $('#jugador').toggleClass('jugador-' + turnoSiguiente)
      }
      // revisa si ya hay ganador
      var ganador = _tateti.ganador()
      if (ganador === null) {
        $('.resultado').html('Empate')
        $('#info-turno').empty()
      } else if (ganador !== false) {
        $('.resultado').html('El ganador es <span class="jugador-' + ganador + '"></span>')
        $('.marcable').off('click').toggleClass('marcable') // Desvincular el evento click a todas las celdas y quitar la clase marcable
        $('#info-turno').empty()
      }
    })

    $('.resultado').empty()
  }

  this.eventoBtnIniciar = function () {
    $('#btn-iniciar').on('click', function (event) {
      that.iniciar()
    })
  }
}
