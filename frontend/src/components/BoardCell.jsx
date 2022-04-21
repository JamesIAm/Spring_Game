import React from "react";
import axios from "axios";

const BoardCell = ({
	columnKey,
	rowKey,
	cell,
	updateBoard,
	player,
	setPlayer,
	gameWinner,
	setGameWinner,
	computerWorking,
	setComputerWorking,
}) => {
	const makeMove = (x, y) => {
		setComputerWorking(true);
		setPlayer("Computer");
		axios
			.post(process.env.REACT_APP_BACKEND_HOST + "/game/makeMove", {
				player: 1,
				x: x,
				y: y,
			})
			.then((res) => updateBoard())
			.catch((err) => {
				let status = err.response.status;
				console.log(err.response);
				if (status === 417) {
					setGameWinner(err.response.data.message);
					console.log("sending true");
					updateBoard(true);
				} else {
					console.error(err);
					setComputerWorking(false);
					setPlayer("Human");
				}
			});
	};
	return (
		<td row={rowKey} column={columnKey}>
			<button
				onClick={() => makeMove(columnKey, rowKey)}
				disabled={gameWinner || computerWorking}
				className={"cell" + cell}
			>
				{cell}
			</button>
		</td>
	);
};

export default BoardCell;
