<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Ta-Te-Ti</title>
    <link rel="stylesheet" href="./css/estilo.css" charset="utf-8">
</head>
<body>
    <header>
        <h1>Ta-Te-Ti</h1>
    </header>
    <div id="form-usuario">
        <label>Nombre de usuario</label>
        <input name="nombre" type="text" class="campo-entrada" placeholder="usuario_ejemplo" />
        <button id="btn-iniciar" class="btn btn-verde">Ingresar</button>
    </div>
    <script src="./js/websocket.js"></script>
    <script src="./jquery/jquery-3.1.0.min.js"></script>
    <script>
        $(document).ready(function() {
            $('#btn-iniciar').on('click', function() {
                var nombre = $('input[name="nombre"]').val()
                if (nombre.length > 3) {
                    mensaje = {
                        tipo: 'agregar-usuario',
                        nombre: $('input[name="nombre"]').val()
                    }
                    webSocket.enviar(JSON.stringify(mensaje))
                } else {
                    alert('El nombre de usuario debe tener mas de 3 caracteres')
                }

            })
            webSocket = new Conexion('ws://localhost:8080/websocket/usuarioserver');
            webSocket.abrir()
            webSocket.onMessage(function(event) {
                console.log(event.data)
            })
        })
    </script>
</body>
</html>
