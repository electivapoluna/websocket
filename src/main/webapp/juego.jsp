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
        <div id="info-jugadores"></div>
        <div id="info-turno"></div>
        <ul id="tablero"></ul>
        <div class="resultado">
        </div>

        <script src="./jquery/jquery-3.1.0.min.js"></script>
        <script src="./js/conexion.js"></script>
        <script src="./tateti/tateti.js"></script>
        <script src="./tateti/tablero.js"></script>
        <script>
            $(document).ready(function () {
                "use strict"
                var tablero = new Tablero()
                tablero.iniciar(jugador, oponente)
                tablero.eventoBtnIniciar()
            })
        </script>
    </body>
</html>
