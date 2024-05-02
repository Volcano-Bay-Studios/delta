var WebSocketServer = require('ws').Server
  , wss = new WebSocketServer({ port: 8081 });

wss.on('connection', function connection(ws) {
    console.log("Connection!");
  ws.on('message', function incoming(message) {
    console.log(message);
    wss.broadcast(message, ws);
  });
});

wss.broadcast = function broadcast(msg, sender) {
    wss.clients.forEach(function each(client) {
      if (client !== sender) {
        client.send(msg)
      } else {
        //client.send(JSON.stringify("value","pB"))
      }
    });
};