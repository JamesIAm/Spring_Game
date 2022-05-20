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

  public Score countLines(int playerId, Score score) {
    this.countLinesInDirection(HORIZONTAL_COORDS, playerId, score);
    this.countLinesInDirection(VERTICAL_COORDS, playerId, score);
    this.countLinesInDirection(DOWN_DIAG_COORDS, playerId, score);
    this.countLinesInDirection(UP_DIAG_COORDS, playerId, score);
    return score;
  }

  private void countLinesInDirection(List<List<Pair<Integer, Integer>>> orderedItems, int playerId, Score score) {
    int length = 0;
    Openness openEnded = Openness.CLOSED;
    int lastNumber = -1; // ---------Represents the last number observed, to see if lines are open ended or not.
    // ---------At the start of every line it defaults to the -1, so lines
    // ---------that start at the edge of the board are not open ended
    for (List<Pair<Integer, Integer>> line : orderedItems) {
      for (Pair<Integer, Integer> cell : line) {
        int currentCell = board[cell.b][cell.a];
        //------------If next tile is this players id, increment the length
        if (currentCell == playerId) {
          //Found redundancy here: if (length == 0 && lastNumber == 0) { - Yay for testing
          if (lastNumber == 0) {
            openEnded = Openness.SEMI;
          }
          length += 1;
        } else if (length > 0) {
          if (openEnded == Openness.SEMI && currentCell == 0) {
            score.incrementScore(Openness.OPEN, length);
          } else if (openEnded == Openness.SEMI || currentCell == 0) {
            score.incrementScore(Openness.SEMI, length);
          } else {
            score.incrementScore(Openness.CLOSED, length);
          }
          length = 0;
          openEnded = Openness.CLOSED;
        }
        lastNumber = currentCell;

      }
      if (length > 0) {
        if (openEnded != Openness.CLOSED) {
          score.incrementScore(Openness.SEMI, length);
        } else {
          score.incrementScore(Openness.CLOSED, length);
        }
      }
      openEnded = Openness.CLOSED;
      length = 0;
      lastNumber = -1;
    }
  }

  /**
   * @param x
   * @param y
   * @param xChange
   * @param yChange
   * @return Triplet containing:
   * a: Whether the old line was open or closed.
   * b: The length of the old line
   * c: What ID the old line belonged to - Or 0 if there is no line next to it
   */
  public Triplet<Openness, Integer, Integer> findLinesNextToPlayedMove(int x, int y, Integer xChange, Integer yChange) {
    int firstY = y + yChange;
    int firstX = x + xChange;
    if (firstX >= BOARD_SIZE || firstX < 0 || firstY >= BOARD_SIZE || firstY < 0) {
      return new Triplet<>(Openness.CLOSED, 0, 0);
    }

    int id = board[y + yChange][x + xChange];
    for (int previousLineLength = 1; previousLineLength <= BOARD_SIZE; previousLineLength++) {// Check positive horizontal
      int newX = x + (xChange * previousLineLength);
      int newY = y + (yChange * previousLineLength);
      if (newX < BOARD_SIZE && newY < this.BOARD_SIZE && newX >= 0 && newY >= 0) {
        int value = board[newY][newX];
        if (value == 0) {
          // If it is now semi open. The previous line was open
//          this.priorityMoves.add(new Move(newX, newY));
          return new Triplet<>(Openness.OPEN, previousLineLength - 1, id);
        } else if (value != id) {
          // If it is now closed, the previous line was semi open
          return new Triplet<>(Openness.SEMI, previousLineLength - 1, id);
        }
      } else {
        return new Triplet<>(Openness.SEMI, previousLineLength - 1, id);
      }
    }
    return null;
  }
}
