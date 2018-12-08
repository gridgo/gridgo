var gameManager = (function() {
	// we have socketClient as global object
	var socketClient = window.socketClient;
	if (!socketClient) {
		throw "Socket client not found";
	}

	var alreadyJoined = false;
	var userName = undefined;
	var gameList = [];

	function renderGameList() {
		var html = "";
		for (var i = 0; i < gameList.length; i++) {
			var game = gameList[i];
			var players = [];
			for ( var player in game.playerList) {
				players.push(player)
			}
			var button = '<button onclick="return gameManager.join(' + game.gameId + ');">Join</button>';
			var info = '<span>Game ' + game.gameId + ": " + players.join(", ") + "</span>";
			html += '<li id="game' + game.gameId + '">' + info + ((alreadyJoined || players.length > 1) ? "" : button) + '</li>';
		}
		var gameListContainer = document.getElementById("gameList");
		gameListContainer.innerHTML = html;
	}

	function onMessage(msg) {
		switch (msg.cmd) {
		case "loggedIn":
			alreadyJoined = false;
			userName = msg.userName;
			gameList = msg.gameList;
			renderGameList();
			break;
		case "loggedOut":
			alreadyJoined = false;
			userName = undefined;
			gameList = [];
			renderGameList();
			break;
		case "gameDeleted":
			var index = -1;
			for (var i = 0; i < gameList.length; i++) {
				if (gameList[i].gameId == msg.gameId) {
					index = i;
					break;
				}
			}
			if (index >= 0) {
				gameList.splice(index, 1);
				renderGameList();
			}
			break;
		case "playerJoinGame":
			var joinedUser = msg.userName;

			if (joinedUser == userName) {
				alreadyJoined = true;
			}

			if (msg.newGame) {
				gameList.push({
					gameId : msg.gameId,
					playerList : msg.playerList
				})
			} else {
				for (var i = 0; i < gameList.length; i++) {
					var game = gameList[i];
					if (game.gameId == msg.gameId) {
						for ( var player in msg.playerList) {
							game.playerList[player] = msg.playerList[player];
						}
						console.log("user join to existing game: " + game.gameId + ", playerList: " + game.playerList);
					}
				}
			}
			renderGameList();
			break;
		case "playerQuitGame":
			if (msg.userName == userName) {
				alreadyJoined = false;
			}
			for (var i = 0; i < gameList.length; i++) {
				var game = gameList[i];
				if (game.gameId == msg.gameId) {
					game.playerList = msg.playerList
				}
			}
			renderGameList();
		}
	}

	socketClient.subscribe(onMessage)

	return {
		create : function() {
			socketClient.send({
				cmd : "game",
				data : {
					action : "create"
				}
			})
		},
		join : function(gameId) {
			socketClient.send({
				cmd : "game",
				data : {
					action : "join",
					gameId : gameId
				}
			})
		},
		quit : function() {
			socketClient.send({
				cmd : "game",
				data : {
					action : "quit",
				}
			})
		}
	}
})()