import React from "react";
import BoardCell from "./BoardCell";

const BoardRow = ({
	rowKey,
	row,
	updateBoard,
	player,
	setPlayer,
	gameWinner,
	setGameWinner,
	computerWorking,
	setComputerWorking,
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
						setPlayer={setPlayer}
						gameWinner={gameWinner}
						setGameWinner={setGameWinner}
						computerWorking={computerWorking}
						setComputerWorking={setComputerWorking}
					/>
				);
			})}
		</tr>
	);
};

export default BoardRow;
