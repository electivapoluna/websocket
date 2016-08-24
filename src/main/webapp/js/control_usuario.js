$(document).ready(function () {
    "use strict"
    var usuario

    var webSocket = new Conexion('ws://localhost:8080/websocket/usuarioserver');
    webSocket.abrir()

    function cargarLista(listaUsuarios) {
        var lista = '<p class="titulo-lista">Usuarios conectados</p><ul>'
        if (listaUsuarios.length === 0) {
            lista = '<p class="titulo-lista">No hay usuarios conectados</p><ul>'
        }
        for (var i = 0; i < listaUsuarios.length; i++) {
            lista += '<li>' + listaUsuarios[i].nombre + '</li>'
        }
        lista += '</ul>'
        $('#lista-usuarios').html(lista)
    }

    $('#btn-iniciar').on('click', function () {
        var nombre = $('input[name="nombre"]').val()
        if (nombre.length > 1 && nombre.length <= 16) {
            var mensaje = {
                tipo: 'agregar-usuario',
                nombre: nombre
            }
            webSocket.enviar(JSON.stringify(mensaje))
        } else {
            alert('El nombre de usuario debe tener 2 a 16 caracteres')
        }
    })

    webSocket.onMessage(function (event) {
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
        }
    })

    webSocket.onClose(function (event) {
        $('#lista-usuarios').empty()
        $('#info-usuario').empty()
    })
})