var gameBoard = (function() {

	var socketClient = window.socketClient;
	if (!socketClient) {
		throw "Socket client not found";
	}

	var alreadyJoined = false;
	var userName = undefined;
	var turn = undefined;
	var playerList = []
	var board = [ [ 0, 0, 0 ], [ 0, 0, 0 ], [ 0, 0, 0 ] ];

	function renderGameBoard() {
		for (var y=0; y<3; y++) {
			for (var x=0; x<3; x++ ){
				var char = String.fromCharCode(board[y][x]);
				document.getElementById("cell" + x + "" + y).innerHTML = char;
			}
		}
		document.getElementById("playerInTurnLabel").innerHTML = "Turn: " + turn;
	}
	
	function clearBoard() {
		board = [ [ 0, 0, 0 ], [ 0, 0, 0 ], [ 0, 0, 0 ] ];
		renderGameBoard();
	}

	function onMessage(msg) {
		switch (msg.cmd) {
		case "loggedIn":
			alreadyJoined = false;
			userName = msg.userName;
			clearBoard();
			break;
		case "loggedOut":
			alreadyJoined = false;
			userName = undefined;
			clearBoard();
			break;
		case "playerJoinGame":
			if (msg.userName == userName) {
				alreadyJoined = true;
				playerList = msg.playerList;
				clearBoard();
			}
			break
		case "playerQuitGame":
			if (msg.userName == userName) {
				alreadyJoined = false;
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
			playerList = msg.playerList;
			clearBoard()
			break;
		case "playerMove":
			turn = msg.turn;
			var cell = msg.cell;
			board[cell.y][cell.x] = cell.char;
			renderGameBoard();
			
			if (msg.finish) {
				turn = msg.winner;
				setTimeout(function() {
					alert("Game over, winner: " + msg.winner);
					clearBoard()
				}, 500)
			}
			break;
		}
	}

	socketClient.subscribe(onMessage);

	return {
		move : function(x, y) {
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