/**
 * Manejar la interfaz principal
 */

$(document).ready(function () {
    "use strict"
    var usuario
    var urlJuego = 'http://localhost:8080/websocket/juego.jsp'
    var urlServicioUsuario = 'ws://localhost:8080/websocket/usuarioserver'

    var conexion = new Conexion(urlServicioUsuario);
    conexion.abrir()

    // muestra la lista de usuarios conectados actualmente
    function cargarLista(listaUsuarios) {
        var lista = '<p class="titulo-lista">Usuarios conectados</p><ul>'
        if (listaUsuarios.length === 0) {
            lista = '<p class="titulo-lista">No hay usuarios conectados</p><ul>'
        }
        for (var i = 0; i < listaUsuarios.length; i++) {
            lista += '<li class="oponente-elegir" data-oponente="' + listaUsuarios[i].sesion + '">' + listaUsuarios[i].nombre + '</li>'
        }
        lista += '</ul>'
        $('#lista-usuarios').html(lista)

        // Evento click para pedir inicio de una partida
        $('.oponente-elegir').on('click', function () {
            var mensaje = {
                tipo: "peticion-juego",
                id_oponente: $(this).attr('data-oponente')
            }

            conexion.enviar(JSON.stringify(mensaje))
        })
    }

    conexion.onMessage(function (event) {
        var respuesta = JSON.parse(event.data)
        switch (respuesta.tipo) {
            case 'agregar-usuario':
                if (respuesta.resultado === true) {
                    usuario = new Usuario($('input[name="nombre"]').val())
                    $('#form-usuario').empty()
                    $('#info-usuario').html(usuario.getNombre())
                } else {
                    alert('Nombre de usuario ya existe, intente con otro')
                }
                break
            case 'cargar-lista-usuarios':
                if (usuario !== undefined) {
                    cargarLista(respuesta.resultado)
                }
                break
            case 'peticion-juego':
                if (respuesta.resultado !== false) {
                    var jugador = new Jugador(usuario.getNombre(), 'x', respuesta.resultado)
                    var oponente = new Jugador(respuesta.oponente.nombre, 'o', respuesta.resultado)
                    var ventana = window.open(urlJuego, Math.random().toString(36), '_blank')
                    ventana.jugador = jugador
                    ventana.oponente = oponente
                } else {
                    alert('Ya se esta jugando una partida')
                }
                break
            case 'iniciar-juego':
                var jugador = new Jugador(usuario.getNombre(), 'o', respuesta.resultado)
                var oponente = new Jugador(respuesta.oponente.nombre, 'x', respuesta.resultado)
                var ventana = window.open(urlJuego, Math.random().toString(36), '_blank')
                ventana.jugador = jugador
                ventana.oponente = oponente
                break
        }
    })

    conexion.onClose(function (event) {
        $('#lista-usuarios').empty()
        $('#info-usuario').empty()
    })

    // Control de eventos del usuario
    $('#btn-iniciar').on('click', function () {
        var nombre = $('input[name="nombre"]').val()
        if (nombre.length > 1 && nombre.length <= 16) {
            var mensaje = {
                tipo: 'agregar-usuario',
                nombre: nombre
            }
            conexion.enviar(JSON.stringify(mensaje))
        } else {
            alert('El nombre de usuario debe tener 2 a 16 caracteres')
        }
    })
})