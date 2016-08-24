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
        <div id="info-turno">
        </div>
        <ul id="tablero"></ul>
        <div class="resultado">
        </div>
        <button id="btn-iniciar">Volver a jugar</button>
        <button id="btn-jugar">Jugar</button>
        <script src="./jquery/jquery-3.1.0.min.js"></script>
        <script src="./tateti/tateti.js"></script>
        <script src="./tateti/tablero.js"></script>
        <script>
            var tablero = new Tablero()
            tablero.iniciar()
            tablero.eventoBtnIniciar()
        </script>
        <script>
            $(
                $('#btn-jugar').on('click', function () {
                window.open('http://localhost:8080/websocket', 'TaTeTi', 'directories=no,titlebar=no,toolbar=no,location=no,status=no,menubar=no,scrollbars=no,resizable=no,width=400,height=350,_blank')
            })
                )
        </script>
    </body>
</html>
