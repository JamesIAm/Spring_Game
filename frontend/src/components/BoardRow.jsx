import React from "react";
import BoardCell from "./BoardCell";

const BoardRow = ({ rowKey, row, updateBoard, player, setPlayer }) => {
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
					/>
				);
			})}
		</tr>
	);
};

export default BoardRow;
