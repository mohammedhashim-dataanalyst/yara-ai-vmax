/**
 * Optional WebSocket relay for Yara real-time channel.
 * Run: node server/relay.js  (needs `npm i ws`)
 * Point the app at it for low-latency message streaming.
 */
const { WebSocketServer } = require('ws');

const wss = new WebSocketServer({ port: 8080 });
console.log('Yara relay listening on ws://0.0.0.0:8080');

wss.on('connection', (ws) => {
  console.log('client connected');
  ws.on('message', (msg) => {
    // echo / broadcast to other clients — wire your own brain here
    wss.clients.forEach(c => {
      if (c !== ws && c.readyState === 1) c.send(msg.toString());
    });
  });
  ws.on('close', () => console.log('client disconnected'));
});
