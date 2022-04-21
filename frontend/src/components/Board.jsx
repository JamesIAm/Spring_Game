import React, { useState } from "react";
import axios from "axios";
import BoardRow from "./BoardRow";
import TwinSpin from "react-cssfx-loading/lib/TwinSpin";

const Board = () => {
	const [boardState, setBoardState] = useState([]);
	const [player, setPlayer] = useState();
	const [gameWinner, setGameWinner] = useState(0);
	const [computerWorking, setComputerWorking] = useState(false);
	const updateBoard = (gameOver = false) => {
		console.log(gameOver);
		console.log(gameWinner !== 0 || gameOver);
		axios
			//Sends false, until game is over. Then sends true
			.get(
				process.env.REACT_APP_BACKEND_HOST + "/game/getState/" +
					(gameWinner !== 0 || gameOver).toString()
			)
			.then((res) => {
				console.log(res)
				setBoardState(res.data.board);
				setPlayer(res.data.currentPlayer);
			})
			.catch((err) => {
				let status = err.response.status;
				if (status === 417) {
					console.log(err.response.data.message);
					// console.error(err);
					setGameWinner(err.response.data.message);
				} else {
					console.error(err);
				}
			});
		setComputerWorking(false)
	};
	const resetBoard = () => {
		axios.post(process.env.REACT_APP_BACKEND_HOST + "/game/resetBoard").then(() => {
			updateBoard();
			setGameWinner(0);
		});
		setComputerWorking(false)
	};

	return (
		<>
			{gameWinner ? (
				<h2>Player {gameWinner} wins</h2>
				) : (
					<h2>Current player: {player}</h2>
					)}
			{computerWorking ? <TwinSpin style={{"position":"fixed"}} width={"20vw"} height={"20vw"}/> : <></>}
				
			<table>
				<tbody>
					{boardState.map((row, rowKey) => {
						return (
							<BoardRow
							row={row}
							key={rowKey}
							rowKey={rowKey}
							updateBoard={updateBoard}
							player={player}
							gameWinner={gameWinner}
							setGameWinner={setGameWinner}
							computerWorking={computerWorking}
							setComputerWorking={setComputerWorking}
							/>
							);
						})}
				</tbody>
			</table>
			<button onClick={() => updateBoard()}>Get board</button>
			<button onClick={() => resetBoard()}>Reset board</button>
		</>
	);
};

export default Board;
