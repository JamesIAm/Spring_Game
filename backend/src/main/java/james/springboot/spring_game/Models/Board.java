package james.springboot.spring_game.Models;

import james.springboot.spring_game.Exceptions.GameOverException;
import james.springboot.spring_game.Exceptions.InvalidCellStateException;
import james.springboot.spring_game.Exceptions.InvalidPlayerIdException;
import james.springboot.spring_game.Utilities.Utilities;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Board {
  private final int[][] board;
  private static final int BOARD_SIZE = 10;
  private static final List<List<Pair<Integer, Integer>>> HORIZONTAL_COORDS = defineHorizontalCoords();
  private static final List<List<Pair<Integer, Integer>>> VERTICAL_COORDS = defineVerticalCoords();
  private static final List<List<Pair<Integer, Integer>>> UP_DIAG_COORDS = defineUpDiagCoords();
  private static final List<List<Pair<Integer, Integer>>> DOWN_DIAG_COORDS = defineDownDiagCoords();

  public Board() {
    board = new int[BOARD_SIZE][BOARD_SIZE];
  }

  //TODO: make private
  public Board(int[][] newBoard) {
    board = newBoard;
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

  public Move findValidMove() throws GameOverException {

    for (int y = 0; y < BOARD_SIZE; y++) {
      for (int x = 0; x < BOARD_SIZE; x++) {
        if (board[y][x] == 0) {
          return new Move(x, y);
        }
      }
    }
    throw new GameOverException(0);
  }

  public static List<List<Pair<Integer, Integer>>> defineHorizontalCoords() {
    List<List<Pair<Integer, Integer>>> horizontalCoords = new ArrayList<>();
    for (int y = 0; y < BOARD_SIZE; y++) {
      ArrayList<Pair<Integer, Integer>> lineOfMoves = new ArrayList<>();
      for (int x = 0; x < BOARD_SIZE; x++) {
        lineOfMoves.add(new Pair<>(x, y));

      }
      horizontalCoords.add(lineOfMoves);
    }
    return horizontalCoords;
  }

  public static List<List<Pair<Integer, Integer>>> defineVerticalCoords() {
    List<List<Pair<Integer, Integer>>> verticalCoords = new ArrayList<>();
    for (int x = 0; x < BOARD_SIZE; x++) {
      ArrayList<Pair<Integer, Integer>> lineOfMoves = new ArrayList<>();
      for (int y = 0; y < BOARD_SIZE; y++) {
        lineOfMoves.add(new Pair<>(x, y));

      }
      verticalCoords.add(lineOfMoves);
    }
    return verticalCoords;
  }

  public static List<List<Pair<Integer, Integer>>> defineDownDiagCoords() {
    List<List<Pair<Integer, Integer>>> downDiagCoords = new ArrayList<>();
    for (int startY = BOARD_SIZE - 1; startY >= 0; startY--) {
      ArrayList<Pair<Integer, Integer>> lineOfMoves = new ArrayList<>();
      for (int y = startY, x = 0; y < BOARD_SIZE; y++, x++) {
        lineOfMoves.add(new Pair<>(x, y));
      }
      downDiagCoords.add(lineOfMoves);
    }
    for (int startX = 1; startX < BOARD_SIZE; startX++) {
      ArrayList<Pair<Integer, Integer>> lineOfMoves = new ArrayList<>();
      for (int x = startX, y = 0; x < BOARD_SIZE; y++, x++) {
        lineOfMoves.add(new Pair<>(x, y));
      }
      downDiagCoords.add(lineOfMoves);
    }
    return downDiagCoords;
  }

  public static List<List<Pair<Integer, Integer>>> defineUpDiagCoords() {
    List<List<Pair<Integer, Integer>>> upDiagCoords = new ArrayList<>();
    for (int startY = 0; startY < BOARD_SIZE; startY++) {
      ArrayList<Pair<Integer, Integer>> lineOfMoves = new ArrayList<>();
      for (int y = startY, x = 0; y >= 0; y--, x++) {
        lineOfMoves.add(new Pair<>(x, y));
      }
      upDiagCoords.add(lineOfMoves);
    }
    for (int startX = 1; startX < BOARD_SIZE; startX++) {
      ArrayList<Pair<Integer, Integer>> lineOfMoves = new ArrayList<>();
      for (int x = startX, y = BOARD_SIZE - 1; x < BOARD_SIZE; y--, x++) {
        lineOfMoves.add(new Pair<>(x, y));
      }
      upDiagCoords.add(lineOfMoves);
    }
    return upDiagCoords;
  }
}
