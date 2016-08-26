function Usuario(nombre) {
    "use strict"
    var _nombre = nombre

    this.getNombre = function () {
        return _nombre
    }
}

function Jugador(nombre, simbolo, clave) {
    "use strict"
    Usuario.call(this, nombre)

    var _simbolo = simbolo
    var _clave = clave

    this.getSimbolo = function () {
        return _simbolo
    }

    this.getClave = function () {
        return _clave
    }
}

Jugador.prototype = Object.create(Usuario.prototype)
Jugador.prototype.constructor = Jugador;