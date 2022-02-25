import React from "react";
import axios from "axios";

const BoardCell = ({
	columnKey,
	rowKey,
	cell,
	updateBoard,
	player,
	setPlayer,
}) => {
	const makeMove = (x, y) => {
		axios
			.post("http://localhost:8080/game/makeMove", {
				player: player,
				x: x,
				y: y,
			})
			.then((res) => updateBoard())
			.then(() => {
				if (player === 1) {
					setPlayer(2);
				} else {
					setPlayer(1);
				}
			});
	};
	return (
		<td row={rowKey} column={columnKey}>
			<button onClick={() => makeMove(columnKey, rowKey)}>{cell}</button>
		</td>
	);
};

export default BoardCell;
