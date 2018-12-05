var socketClient = (function() {

	var socket = undefined;
	var userNameInput = undefined;
	var playBtn = undefined;
	var userName = undefined;

	var userNamePattern = /[a-z0-9_]+/ig;

	function connect() {
		userNameInput = document.getElementById("userNameInput");
		playBtn = document.getElementById("playBtn");

		if (!userNamePattern.test(userNameInput.value)) {
			alert("Invalid username, must contain only letters, numbers and underscore (_)");
			return;
		}

		userName = userNameInput.value;

		socket = new WebSocket("ws://localhost:8889/tiktactoe");
		socket.onerror = onError;
		socket.onclose = onDisconnected;
		socket.onopen = onConnected;
		socket.onmessage = onMessage;
	}

	function close() {
		socket.close();
		socket = undefined;
	}

	function onError(err) {
		console.error("Socket error", err);
		onDisconnected();
	}

	function onConnected() {
		userNameInput.disabled = true;
		playBtn.innerHTML = "Quit";

		send({
			cmd : "login",
			userName : userName
		});
	}

	function onDisconnected() {
		userNameInput.disabled = false;
		playBtn.innerHTML = "Play";
		userName = undefined;
		socket = null;
	}

	var subscribers = [];

	function onMessage(msg) {
		var data = JSON.parse(msg.data);
		for (var i = 0; i < subscribers.length; i++) {
			subscribers[i](data[2]);
		}
	}

	function send(msg) {
		if (socket != null) {
			socket.send(typeof msg == "object" ? JSON.stringify(msg) : msg);
		} else {
			alert("Disconnected");
		}
	}
	return {
		toggleConnect : function() {
			(socket ? close : connect)();
		},
		send : send,
		subscribe : function(sub) {
			subscribers.push(sub)
		},
		getUserName : function() {
			return userName;
		}
	};
})();