var userNameInput = document.getElementById("userNameInput");
var playBtn = document.getElementById("playBtn");

function connectToGame() {
	var ws = new WebSocket("ws://localhost:8889/tiktactoe");
}

function onConnected() {
	userNameInput.disabled = true;
	playBtn.innerHtml = "Quit";
}

function onDisconnected() {
	userNameInput.disabled = false;
	playBtn.innerHtml = "Play";
}