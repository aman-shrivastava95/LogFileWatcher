// Create WebSocket connection.
const socket = new WebSocket("ws://localhost:8080/log");

// Connection opened
socket.addEventListener("open", (event) => {

});

// Listen for messages
socket.addEventListener("message", (event) => {
  const logs = document.getElementById("messages");
  logs.append(event.data)
  logs.append(document.createElement("br"))
});