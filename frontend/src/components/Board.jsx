import React, { useState } from "react";
import axios from "axios";
import BoardRow from "./BoardRow";

const Board = () => {
	const [boardState, setBoardState] = useState([]);
	const [player, setPlayer] = useState(1);
	const updateBoard = () => {
		axios
			.get("http://localhost:8080/game/getState")
			.then((res) => {
				console.log(res.data);
				setBoardState(res.data);
			})
			.catch((err) => console.error(err));
	};

	return (
		<>
			<h2>Current player: {player}</h2>
			<table>
				{boardState.map((row, rowKey) => {
					return (
						<BoardRow
							row={row}
							key={rowKey}
							rowKey={rowKey}
							updateBoard={updateBoard}
							player={player}
							setPlayer={setPlayer}
						/>
					);
				})}
			</table>
			<button onClick={() => updateBoard()}>Get board</button>
		</>
	);
};

export default Board;
