import React from "react";
import BoardCell from "./BoardCell";

const BoardRow = ({
	rowKey,
	row,
	updateBoard,
	player,
	gameWinner,
	setGameWinner,
}) => {
	console.log(row);	
	return (
		<tr row={rowKey}>
			{row.map((cell, columnKey) => {
				return (
					<BoardCell
						rowKey={rowKey}
						key={columnKey}
						cell={cell}
						columnKey={columnKey}
						updateBoard={updateBoard}
						player={player}
						gameWinner={gameWinner}
						setGameWinner={setGameWinner}
					/>
				);
			})}
		</tr>
	);
};

export default BoardRow;
