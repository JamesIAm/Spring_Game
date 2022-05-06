package james.springboot.spring_game.Models;

import james.springboot.spring_game.Exceptions.InvalidCellStateException;
import james.springboot.spring_game.Exceptions.InvalidPlayerIdException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BoardTest {
  Board board = new Board();

  @BeforeEach
  public void setUp() {
    board = new Board();
  }

  @Test
  public void newBoardIsAll0s() {
    Board board = new Board();
    int[][] boardRaw = board.getBoard();
    for (int[] line : boardRaw) {
      for (int cell : line) {
        Assertions.assertEquals(0, cell);
      }
    }
  }

  @ParameterizedTest
  @CsvSource({"1,8,3", "3,6,2", "5,4,1", "7,2,2", "9,0,3"})
  public void makeMoveResultsInTheCorrectChangeInTheBoard(int x, int y, int id) throws InvalidPlayerIdException, InvalidCellStateException {
    board.makeMove(x, y, id);
    Assertions.assertEquals(id, board.getBoard()[y][x]);
  }

  @Test
  public void makeMoveWithId0ThrowsException() {
    Assertions.assertThrows(InvalidPlayerIdException.class, () -> board.makeMove(0, 0, 0));
  }

  @Test
  public void makeMoveOnNot0CellThrowsException() throws InvalidPlayerIdException, InvalidCellStateException {
    board.makeMove(0, 0, 1);
    Assertions.assertThrows(InvalidCellStateException.class, () -> board.makeMove(0, 0, 2));
  }

  @Test
  public void undoMoveResultsIn0s() throws InvalidPlayerIdException, InvalidCellStateException {
    board.makeMove(0, 0, 1);
    board.undoMove(0, 0);
    Assertions.assertEquals(0, board.getBoard()[0][0]);
  }

  @Test
  public void undoMoveThrowsExceptionIfCellIs0() {
    Assertions.assertThrows(InvalidCellStateException.class, () -> board.undoMove(0, 0));
  }

  @Test
  public void cloneBoardHasTheSameValuesAsTheOriginal() throws InvalidPlayerIdException, InvalidCellStateException {
    board.makeMove(1, 2, 1);
    board.makeMove(3, 2, 2);
    board.makeMove(3, 5, 3);
    board.makeMove(7, 5, 4);
    Board newBoard = board.deepCopyBoard();
    int[][] boardRaw = newBoard.getBoard();
    Assertions.assertAll(
        () -> Assertions.assertEquals(1, boardRaw[2][1]),
        () -> Assertions.assertEquals(2, boardRaw[2][3]),
        () -> Assertions.assertEquals(3, boardRaw[5][3]),
        () -> Assertions.assertEquals(4, boardRaw[5][7])
    );
  }

  @Test
  public void cloneBoardAllowsForIndependentChangesFromOriginal() throws InvalidPlayerIdException, InvalidCellStateException {
    Board newBoard = board.deepCopyBoard();
    board.makeMove(1, 1, 1);
    board.makeMove(2, 3, 2);
    newBoard.makeMove(5, 4, 3);
    newBoard.makeMove(3, 6, 4);
    Assertions.assertAll(
        () -> Assertions.assertNotEquals(board.getBoard()[1][1], newBoard.getBoard()[1][1]),
        () -> Assertions.assertNotEquals(board.getBoard()[3][2], newBoard.getBoard()[3][2]),
        () -> Assertions.assertNotEquals(board.getBoard()[4][5], newBoard.getBoard()[4][5]),
        () -> Assertions.assertNotEquals(board.getBoard()[6][3], newBoard.getBoard()[6][3])

    );
  }
}