function Conexion(url = '', subProtocol = []) {
  var _url = url
  var _subProtocol = subProtocol
  var _webSocket

  this.abrir = function() {
    // Ensures only one connection is open at a time
    if (_webSocket !== undefined && _webSocket.readyState !== WebSocket.CLOSED) {
      return
    }
    // Create a new instance of the websocket
    _webSocket = new WebSocket(_url, _subProtocol)
    _webSocket.onopen = function(event) {
      // For reasons I can't determine, onopen gets called twice
      // and the first time event.data is undefined.
      // Leave a comment if you know the answer.
      if (event.data === undefined) {
        return
      }

      console.log('Conexion abierta: ' + event.data)
    }

    _webSocket.onclose = function(event) {
      console.log('Conexion cerrada')
    }
  }

  this.enviar = function (mensaje) {
    _webSocket.send(mensaje)
  }

  this.cerrar = function() {
    _webSocket.close()
  }

  this.onOpen = function(callback) {
    _webSocket.onopen = callback
  }

  this.onClose = function(callback) {
    _webSocket.onclose = callback
  }

  this.onMessage = function (callback) {
    _webSocket.onmessage = callback
  }
}
