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
}) => {
	const makeMove = (x, y) => {
		axios
			.post("http://localhost:8080/game/makeMove", {
				player: player,
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
				}
			});
	};
	return (
		<td row={rowKey} column={columnKey}>
			<button
				onClick={() => makeMove(columnKey, rowKey)}
				disabled={gameWinner}
				className={"cell" + cell}
			>
				{cell}
			</button>
		</td>
	);
};

export default BoardCell;
