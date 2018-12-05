var gameManager = (function() {
	// we have socketClient as global object
	var socketClient = window.socketClient;
	if (!socketClient) {
		throw "Socket client not found";
	}

	function onMessage(msg) {
		switch (msg.cmd) {
		case "userJoinGame":
			var userName = msg.userName;
			if (userName == socketClient.getUserName()) {
				if (msg.creator) {
					console.log("Create game success, gameId: " + msg.gameId)
				} else {
					console.log("Joined to game id: " + msg.gameId);
				}
			}
			break;
		}
	}

	function create() {
		socketClient.send({
			cmd : "game",
			data : {
				action : "create"
			}
		})
	}

	socketClient.subscribe(onMessage)

	return {
		create : create
	}
})()