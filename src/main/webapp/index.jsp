<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <title>Ta-Te-Ti</title>
        <link rel="stylesheet" href="./css/estilo.css" charset="utf-8">
        <link rel="stylesheet" href="./css/lista.css" charset="utf-8">
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
        <div id="info-usuario"></div>
        <div id="lista-usuarios"></div>
        <script src="./jquery/jquery-3.1.0.min.js"></script>
        <script src="./js/conexion.js"></script>
        <script src="./js/usuario.js"></script>
        <script src="./js/control_usuario.js"></script>
    </body>
</html>
