import React, { useState } from "react";
import axios from "axios";

const Board = () => {
	const [boardState, setBoardState] = useState([]);
	const updateBoard = () => {
		axios
			.get("http://localhost:8080/game/getState")
			.then((res) => {
				console.log(res.data);
				setBoardState(res.data);
			})
			.catch((err) => console.error(err));
	};
	const makeMove = (x, y) => {
		axios
			.post("http://localhost:8080/game/makeMove", {
				player: 1,
				x: x,
				y: y,
			})
			.then((res) => updateBoard());
	};
	return (
		<>
			{boardState.map((row, rowKey) => {
				return (
					<tr key={rowKey} row={rowKey}>
						{row.map((cell, columnKey) => {
							return (
								<td
									key={columnKey}
									row={rowKey}
									column={columnKey}
								>
									<button>{cell}</button>
								</td>
							);
						})}
					</tr>
				);
			})}
			<button onClick={() => updateBoard()}>asd</button>
		</>
	);
};

export default Board;
