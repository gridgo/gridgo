var gameBoard = (function() {

	var socketClient = window.socketClient;
	if (!socketClient) {
		throw "Socket client not found";
	}

	var gameId = -1;
	var userName = undefined;
	var turn = undefined;
	var playerList = []
	var board = [ [ 0, 0, 0 ], [ 0, 0, 0 ], [ 0, 0, 0 ] ];

	function renderGameBoard(clearBackground) {
		for (var y=0; y<3; y++) {
			for (var x=0; x<3; x++ ){
				var char = String.fromCharCode(board[y][x]);
				var cell = document.getElementById("cell" + x + "" + y);
				cell.innerHTML = char;
				if (clearBackground) {
					cell.style.backgroundColor = "";
				}
			}
		}
		document.getElementById("playerInTurnLabel").innerHTML = "Turn: " + (turn ? turn : "N/A");
	}
	
	function clearBoard() {
		board = [ [ 0, 0, 0 ], [ 0, 0, 0 ], [ 0, 0, 0 ] ];
		renderGameBoard(true);
	}

	var displayNotification = (function() {
		var list = [];
		var timeoutId = undefined;

		function hideError() {
			list = [];
			render();
		}

		function render() {
			var html = [];

			for (var i = 0; i < list.length; i++) {
				html.push('<div>' + list[i] + '</div>');
			}

			var label = document.getElementById("notificationLabel");
			label.innerHTML = html.join("");

			if (timeoutId != undefined) {
				clearTimeout(timeoutId);
			}

			if (list.length > 0) {
				timeoutId = setTimeout(hideError, 2000);
			}
		}

		return function(msg) {
			if (msg) {
				list.push(msg);
				render();
			}
		}
		
	})();
	
	var timeoutToClearBoard = undefined;
	
	function onMessage(msg) {
		switch (msg.cmd) {
		case "loggedIn":
			gameId = -1;
			userName = msg.userName;
			clearBoard();
			break;
		case "loggedOut":
			turn = undefined;
			gameId = -1;
			userName = undefined;
			clearBoard();
			break;
		case "playerJoinGame":
			if (msg.userName == userName) {
				gameId = msg.gameId;
				playerList = msg.playerList;
				clearBoard();
			}
			break
		case "playerQuitGame":
			if (msg.userName == userName || msg.gameId == gameId) {
				turn = undefined;
				gameId = -1;
				playerList = msg.playerList;
				clearBoard();
			}
			break;
		case "startGame":
			playerList = msg.playerList;
			turn = msg.turn;
			board = [ [ 0, 0, 0 ], [ 0, 0, 0 ], [ 0, 0, 0 ] ];
			renderGameBoard();
			break;
		case "clearGame":
			turn = undefined;
			playerList = msg.playerList;
			clearBoard()
			break;
		case "playerMove":
			turn = msg.turn;
			var cell = msg.cell;
			board[cell.y][cell.x] = cell.char;
			renderGameBoard();
			
			if (msg.finish) {
				var winner = msg.winner;
				if (winner) {					
					displayNotification('Game over, winner: ' + msg.winner);
					var winnerLine = msg.winnerLine;
					for (var i= 0; i<winnerLine.length; i++) {
						document.getElementById('cell'+winnerLine[i][1] + "" + winnerLine[i][0]).style.backgroundColor = "cadetblue";
					}
				} else {
					displayNotification('Game over, draw');
				}
				
				timeoutToClearBoard = setTimeout(function() {
					clearBoard()
					timeoutToClearBoard = undefined;
				}, 1000)
			}
			break;
		}
	}

	socketClient.subscribe(onMessage);

	return {
		move : function(x, y) {
			if (timeoutToClearBoard) {
				displayNotification("Wait a moment");
				return;
			}
			socketClient.send({
				cmd : "game",
				data : {
					action : "move",
					x : x,
					y : y
				}
			})
		}
	}
})();