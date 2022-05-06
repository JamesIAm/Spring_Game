package james.springboot.spring_game.Models;

import james.springboot.spring_game.Exceptions.InvalidCellStateException;
import james.springboot.spring_game.Exceptions.InvalidPlayerIdException;
import james.springboot.spring_game.Utilities.Utilities;

public class Board {
  int[][] board;
  private final int BOARD_SIZE = 10;

  public Board() {
    board = new int[BOARD_SIZE][BOARD_SIZE];
  }

  private Board(int[][] newBoard) {
    board = newBoard;
  }

  public int[][] getBoard() {
    return this.board;
  }

  public void makeMove(int x, int y, int id) throws InvalidPlayerIdException, InvalidCellStateException {
    if (id == 0) {
      throw new InvalidPlayerIdException(String.format("Id of %d is no valid", id));
    }
    if (board[y][x] == 0) {
      board[y][x] = id;
    } else {
      throw new InvalidCellStateException(String.format("Cell (%d, %d) can not be changed as it currently has a value of %d", x, y, board[y][x]));
    }
  }

  public void undoMove(int x, int y) throws InvalidCellStateException {
    if (board[y][x] != 0) {
      board[y][x] = 0;
    } else {
      throw new InvalidCellStateException(String.format("Cell (%d, %d) can not be reverted to 0 as it currently has a value of %d", x, y, board[y][x]));
    }
  }

  public Board deepCopyBoard() {
    return new Board(Utilities.deepCopyDoubleIntArray(board));
  }
}
