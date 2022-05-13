package james.springboot.spring_game.Models;

import james.springboot.spring_game.Exceptions.GameOverException;
import james.springboot.spring_game.Exceptions.InvalidCellStateException;
import james.springboot.spring_game.Exceptions.InvalidPlayerIdException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@ExtendWith({MockitoExtension.class})
public class BoardTest {

  @Mock
  private Score score;

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
    //TODO: mock Utilities?
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

  @Test
  public void findValidMoveReturnsAMove() throws GameOverException {
    Assertions.assertInstanceOf(Move.class, board.findValidMove());
  }

  @Test
  public void findValidMoveReturnsAMoveWhereCellIs0() throws InvalidPlayerIdException, InvalidCellStateException, GameOverException {
    board.makeMove(0, 0, 1);
    Move suggestedMove = board.findValidMove();
    Assertions.assertEquals(0, board.getBoard()[suggestedMove.y][suggestedMove.x]);
  }

  @Test
  public void ifNoValidMovesValidMoveThrowsException() throws InvalidPlayerIdException, InvalidCellStateException {
    for (int y = 0; y < 10; y++) {
      for (int x = 0; x < 10; x++) {
        board.makeMove(y, x, 1);
      }
    }
    Assertions.assertThrows(GameOverException.class, () -> board.findValidMove());
  }

  private void assertLineOfPairsIsEqual(List<Pair<Integer, Integer>> expectedValues, List<Pair<Integer, Integer>> generatedValues) {
    for (int index = 0; index < expectedValues.size(); index++) {
      Assertions.assertEquals(expectedValues.get(index).a, generatedValues.get(index).a);
      Assertions.assertEquals(expectedValues.get(index).b, generatedValues.get(index).b);
      Assertions.assertEquals(expectedValues.size(), generatedValues.size());
    }
  }

  @Test
  public void defineHorizontalCoordsResultsInCorrectOrders() {
    List<Pair<Integer, Integer>> firstLine = List.of(
        new Pair<>(0, 0), new Pair<>(1, 0), new Pair<>(2, 0), new Pair<>(3, 0), new Pair<>(4, 0),
        new Pair<>(5, 0), new Pair<>(6, 0), new Pair<>(7, 0), new Pair<>(8, 0), new Pair<>(9, 0)
    );
    List<Pair<Integer, Integer>> lastLine = List.of(
        new Pair<>(0, 9), new Pair<>(1, 9), new Pair<>(2, 9), new Pair<>(3, 9), new Pair<>(4, 9),
        new Pair<>(5, 9), new Pair<>(6, 9), new Pair<>(7, 9), new Pair<>(8, 9), new Pair<>(9, 9)
    );
    List<List<Pair<Integer, Integer>>> generatedCoords = Board.defineHorizontalCoords();
    Assertions.assertAll(
        () -> assertLineOfPairsIsEqual(firstLine, generatedCoords.get(0)),
        () -> assertLineOfPairsIsEqual(lastLine, generatedCoords.get(9)),
        () -> Assertions.assertEquals(10, generatedCoords.size())
    );
  }

  @Test
  public void defineVerticalCoordsResultsInCorrectOrders() {
    List<Pair<Integer, Integer>> firstLine = List.of(
        new Pair<>(0, 0), new Pair<>(0, 1), new Pair<>(0, 2), new Pair<>(0, 3), new Pair<>(0, 4),
        new Pair<>(0, 5), new Pair<>(0, 6), new Pair<>(0, 7), new Pair<>(0, 8), new Pair<>(0, 9)
    );
    List<Pair<Integer, Integer>> lastLine = List.of(
        new Pair<>(9, 0), new Pair<>(9, 1), new Pair<>(9, 2), new Pair<>(9, 3), new Pair<>(9, 4),
        new Pair<>(9, 5), new Pair<>(9, 6), new Pair<>(9, 7), new Pair<>(9, 8), new Pair<>(9, 9)
    );
    List<List<Pair<Integer, Integer>>> generatedCoords = Board.defineVerticalCoords();
    Assertions.assertAll(
        () -> assertLineOfPairsIsEqual(firstLine, generatedCoords.get(0)),
        () -> assertLineOfPairsIsEqual(lastLine, generatedCoords.get(9)),
        () -> Assertions.assertEquals(10, generatedCoords.size())
    );
  }

  @Test
  public void defineDownDiagCoordsResultsInCorrectOrders() {
    List<Pair<Integer, Integer>> firstLine = List.of(
        new Pair<>(0, 9)
    );
    List<Pair<Integer, Integer>> lastLine = List.of(
        new Pair<>(9, 0)
    );
    List<Pair<Integer, Integer>> midLine = List.of(
        new Pair<>(0, 0), new Pair<>(1, 1), new Pair<>(2, 2), new Pair<>(3, 3), new Pair<>(4, 4),
        new Pair<>(5, 5), new Pair<>(6, 6), new Pair<>(7, 7), new Pair<>(8, 8), new Pair<>(9, 9)
    );
    List<List<Pair<Integer, Integer>>> generatedCoords = Board.defineDownDiagCoords();
    Assertions.assertAll(
        () -> assertLineOfPairsIsEqual(firstLine, generatedCoords.get(0)),
        () -> assertLineOfPairsIsEqual(lastLine, generatedCoords.get(18)),
        () -> assertLineOfPairsIsEqual(midLine, generatedCoords.get(9)),
        () -> Assertions.assertEquals(19, generatedCoords.size())
    );
  }

  @Test
  public void defineUpDiagCoordsResultsInCorrectOrders() {
    List<Pair<Integer, Integer>> firstLine = List.of(
        new Pair<>(0, 0)
    );
    List<Pair<Integer, Integer>> lastLine = List.of(
        new Pair<>(9, 9)
    );
    List<Pair<Integer, Integer>> midLine = List.of(
        new Pair<>(0, 9), new Pair<>(1, 8), new Pair<>(2, 7), new Pair<>(3, 6), new Pair<>(4, 5),
        new Pair<>(5, 4), new Pair<>(6, 3), new Pair<>(7, 2), new Pair<>(8, 1), new Pair<>(9, 0)
    );

    List<List<Pair<Integer, Integer>>> generatedCoords = Board.defineUpDiagCoords();
    Assertions.assertAll(
        () -> assertLineOfPairsIsEqual(firstLine, generatedCoords.get(0)),
        () -> assertLineOfPairsIsEqual(lastLine, generatedCoords.get(18)),
        () -> assertLineOfPairsIsEqual(midLine, generatedCoords.get(9)),
        () -> Assertions.assertEquals(19, generatedCoords.size())
    );
  }

  @Nested
  class countLinesTests {
    @Test
    public void countLinesInDirection_with1SquareInTheMiddle_callsIncrementScoreOpen_4Times() throws InvalidPlayerIdException, InvalidCellStateException {
      board.makeMove(5, 5, 1);
      board.countLines(1, score);
      Mockito.verify(score, times(4)).incrementScore(Openness.OPEN, 1);
      Mockito.verify(score, times(4)).incrementScore(any(), any());
    }

    @Test
    public void countLinesInDirection_with1SquareOnTheLeftEdge_callsIncrementScoreOpen_3TimesSemi_1TimesOpen() throws InvalidPlayerIdException, InvalidCellStateException {
      board.makeMove(0, 5, 1);
      board.countLines(1, score);
      Mockito.verify(score, times(1)).incrementScore(Openness.OPEN, 1);
      Mockito.verify(score, times(3)).incrementScore(Openness.SEMI, 1);
      Mockito.verify(score, times(4)).incrementScore(any(), any());
    }

    @Test
    public void countLinesInDirection_with1SquareOnTheRightEdge_callsIncrementScoreOpen_3TimesSemi_1TimesOpen() throws InvalidPlayerIdException, InvalidCellStateException {
      board.makeMove(9, 5, 1);
      board.countLines(1, score);
      Mockito.verify(score, times(1)).incrementScore(Openness.OPEN, 1);
      Mockito.verify(score, times(3)).incrementScore(Openness.SEMI, 1);
      Mockito.verify(score, times(4)).incrementScore(any(), any());
    }

    @Test
    public void countLinesInDirection_with1SquareInTheTopLeft_callsIncrementScoreOpen_3TimesSemi_1TimesClosed() throws InvalidPlayerIdException, InvalidCellStateException {
      board.makeMove(0, 0, 1);
      board.countLines(1, score);
      Mockito.verify(score, times(1)).incrementScore(Openness.CLOSED, 1);
      Mockito.verify(score, times(3)).incrementScore(Openness.SEMI, 1);
      Mockito.verify(score, times(4)).incrementScore(any(), any());
    }

    @Test
    public void countLinesInDirection_with1SquareInTheBottomRight_callsIncrementScoreOpen_3TimesSemi_1TimesClosed() throws InvalidPlayerIdException, InvalidCellStateException {
      board.makeMove(9, 9, 1);
      board.countLines(1, score);
      Mockito.verify(score, times(1)).incrementScore(Openness.CLOSED, 1);
      Mockito.verify(score, times(3)).incrementScore(Openness.SEMI, 1);
      Mockito.verify(score, times(4)).incrementScore(any(), any());
    }

    @Test
    public void countLinesInDirection_with2SquaresInTheMiddle_callsIncrementScoreOpen_7TimesOpen() throws InvalidPlayerIdException, InvalidCellStateException {
      board.makeMove(5, 5, 1);
      board.makeMove(5, 6, 1);
      board.countLines(1, score);
      Mockito.verify(score, times(6)).incrementScore(Openness.OPEN, 1);
      Mockito.verify(score, times(1)).incrementScore(Openness.OPEN, 2);
      Mockito.verify(score, times(7)).incrementScore(any(), any());
    }

    @Test
    public void countLinesInDirection_with2SquaresInTheMiddle_WithAGap_callsIncrementScoreOpen_7TimesOpen() throws InvalidPlayerIdException, InvalidCellStateException {
      board.makeMove(5, 4, 1);
      board.makeMove(5, 6, 1);
      board.countLines(1, score);
      Mockito.verify(score, times(8)).incrementScore(Openness.OPEN, 1);
      Mockito.verify(score, times(8)).incrementScore(any(), any());
    }

    @Test
    public void countLinesInDirection_with2SquaresAtTheTop_callsIncrementScoreOpen_7TimesOpen() throws InvalidPlayerIdException, InvalidCellStateException {
      board.makeMove(5, 0, 1);
      board.makeMove(5, 1, 1);
      board.countLines(1, score);
      Mockito.verify(score, times(4)).incrementScore(Openness.OPEN, 1);
      Mockito.verify(score, times(2)).incrementScore(Openness.SEMI, 1);
      Mockito.verify(score, times(1)).incrementScore(Openness.SEMI, 2);
      Mockito.verify(score, times(7)).incrementScore(any(), any());
    }

    @Test
    public void countLinesInDirection_with2SquaresAtTheTop_andAnOpponentBlocking_callsIncrementScoreOpen_7TimesOpen() throws InvalidPlayerIdException, InvalidCellStateException {
      board.makeMove(5, 0, 1);
      board.makeMove(5, 1, 1);
      board.makeMove(5, 2, 2);
      board.countLines(1, score);
      Mockito.verify(score, times(4)).incrementScore(Openness.OPEN, 1);
      Mockito.verify(score, times(2)).incrementScore(Openness.SEMI, 1);
      Mockito.verify(score, times(1)).incrementScore(Openness.CLOSED, 2);
      Mockito.verify(score, times(7)).incrementScore(any(), any());
    }

    @Test
    public void countLinesInDirection_with2SquaresInTheMiddle_andAnOpponentInBetween_callsIncrementScoreOpen_7TimesOpen() throws InvalidPlayerIdException, InvalidCellStateException {
      board.makeMove(5, 4, 1);
      board.makeMove(5, 6, 1);
      board.makeMove(5, 5, 2);
      board.countLines(1, score);
      Mockito.verify(score, times(6)).incrementScore(Openness.OPEN, 1);
      Mockito.verify(score, times(2)).incrementScore(Openness.SEMI, 1);
      Mockito.verify(score, times(8)).incrementScore(any(), any());
    }
  }

  @Test
  public void calculateLinesNextToPlayedMoveAndReduceTheirOpenness() throws InvalidPlayerIdException, InvalidCellStateException {
    board.makeMove(6, 5, 1);
    Pair<Openness, Integer> reduction = board.calculateLinesNextToPlayedMoveAndReduceTheirOpenness(1, 5, 5, 1, 0);
  }
}