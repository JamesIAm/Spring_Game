package james.springboot.spring_game.Models;

import james.springboot.spring_game.Exceptions.InvalidOpenessStateException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ScoreTest {
  private final int X_IN_A_ROW = 5;

  @Test
  public void whenAScoreIsCreated_allValuesAre0() {
    Score testScore = new Score(X_IN_A_ROW);
    int[][] scoreFromTest = testScore.getScore();
    Assertions.assertAll(
        () -> Assertions.assertArrayEquals(scoreFromTest[0], new int[]{0, 0, 0, 0, 0, 0}),
        () -> Assertions.assertArrayEquals(scoreFromTest[1], new int[]{0, 0, 0, 0, 0, 0}),
        () -> Assertions.assertArrayEquals(scoreFromTest[2], new int[]{0, 0, 0, 0, 0, 0})
    );
  }

  @Test
  public void whenAScoreIsCreatedWithAXInARowOf5_theScoreObjectContains3ArraysOfLength6() {
    Score testScore = new Score(X_IN_A_ROW);
    int[][] scoreFromTest = testScore.getScore();
    Assertions.assertAll(
        () -> Assertions.assertEquals(3, scoreFromTest.length),
        () -> Assertions.assertEquals(6, scoreFromTest[0].length),
        () -> Assertions.assertEquals(6, scoreFromTest[1].length),
        () -> Assertions.assertEquals(6, scoreFromTest[2].length)
    );
  }

  @Test
  public void whenAScoreIsCreatedFromAnExistingScore_theTwoScoresHaveTheSameValues() {
    Score originalScore = new Score(X_IN_A_ROW);
    originalScore.incrementScore(Openness.CLOSED, 1);
    originalScore.incrementScore(Openness.SEMI, 3);
    originalScore.incrementScore(Openness.OPEN, 5);
    Score copiedScore = new Score(originalScore.getScore());
    Assertions.assertAll(
        () -> Assertions.assertEquals(originalScore.getXInARow(), copiedScore.getXInARow()),
        () -> Assertions.assertArrayEquals(originalScore.getScore()[0], copiedScore.getScore()[0]),
        () -> Assertions.assertArrayEquals(originalScore.getScore()[1], copiedScore.getScore()[1]),
        () -> Assertions.assertArrayEquals(originalScore.getScore()[2], copiedScore.getScore()[2])
    );
  }

  @Test
  public void whenIncreaseScoreIsCalled_withOpen1AndOpen2_scoreOpen4IsIncremented() throws InvalidOpenessStateException {
    Openness previousOpenness1 = Openness.OPEN;
    Openness previousOpenness2 = Openness.OPEN;
    int previousLength1 = 1;
    int previousLength2 = 2;
    Score originalScore = new Score(X_IN_A_ROW);
    originalScore.incrementScore(previousOpenness1, previousLength1);
    originalScore.incrementScore(previousOpenness2, previousLength2);
    originalScore.increaseScore(new Pair<>(previousLength1, previousOpenness1), new Pair<>(previousLength2, previousOpenness2));
    int[][] scoreFromTest = originalScore.getScore();
    Assertions.assertAll(
        () -> Assertions.assertArrayEquals(new int[]{0, 0, 0, 0, 0, 0}, scoreFromTest[0]),
        () -> Assertions.assertArrayEquals(new int[]{0, 0, 0, 0, 0, 0}, scoreFromTest[1]),
        () -> Assertions.assertArrayEquals(new int[]{0, 0, 0, 0, 1, 0}, scoreFromTest[2])
    );
  }

  @Test
  public void whenIncreaseScoreIsCalled_withSemi1AndOpen3_scoreSemi5IsIncremented() throws InvalidOpenessStateException {
    Openness previousOpenness1 = Openness.SEMI;
    Openness previousOpenness2 = Openness.OPEN;
    int previousLength1 = 1;
    int previousLength2 = 3;
    Score originalScore = new Score(X_IN_A_ROW);
    originalScore.incrementScore(previousOpenness1, previousLength1);
    originalScore.incrementScore(previousOpenness2, previousLength2);
    originalScore.increaseScore(new Pair<>(previousLength1, previousOpenness1), new Pair<>(previousLength2, previousOpenness2));
    int[][] scoreFromTest = originalScore.getScore();
    Assertions.assertAll(
        () -> Assertions.assertArrayEquals(new int[]{0, 0, 0, 0, 0, 0}, scoreFromTest[0]),
        () -> Assertions.assertArrayEquals(new int[]{0, 0, 0, 0, 0, 1}, scoreFromTest[1]),
        () -> Assertions.assertArrayEquals(new int[]{0, 0, 0, 0, 0, 0}, scoreFromTest[2])
    );
  }

  @Test
  public void whenIncreaseScoreIsCalled_withSemi2AndSemi1_scoreClosed3IsIncremented() throws InvalidOpenessStateException {
    Openness previousOpenness1 = Openness.SEMI;
    Openness previousOpenness2 = Openness.SEMI;
    int previousLength1 = 2;
    int previousLength2 = 1;
    Score originalScore = new Score(X_IN_A_ROW);
    originalScore.incrementScore(previousOpenness1, previousLength1);
    originalScore.incrementScore(previousOpenness2, previousLength2);
    originalScore.increaseScore(new Pair<>(previousLength1, previousOpenness1), new Pair<>(previousLength2, previousOpenness2));
    int[][] scoreFromTest = originalScore.getScore();
    Assertions.assertAll(
        () -> Assertions.assertArrayEquals(new int[]{0, 0, 0, 0, 1, 0}, scoreFromTest[0]),
        () -> Assertions.assertArrayEquals(new int[]{0, 0, 0, 0, 0, 0}, scoreFromTest[1]),
        () -> Assertions.assertArrayEquals(new int[]{0, 0, 0, 0, 0, 0}, scoreFromTest[2])
    );
  }

  @Test
  public void cloneStopsChangesFromReflectingInThePreviousArray() {
    Score originalScore = new Score(X_IN_A_ROW);
    Score newScore = originalScore.clone();
    newScore.incrementScore(Openness.CLOSED, 0);
    Assertions.assertAll(
        () -> Assertions.assertEquals(0, originalScore.getScore()[0][0]),
        () -> Assertions.assertEquals(1, newScore.getScore()[0][0])
    );
  }

  @Test
  public void winCheck() {
    Score notWon = new Score(X_IN_A_ROW);
    Score hasWon = new Score(X_IN_A_ROW);
    notWon.incrementScore(Openness.CLOSED, 4);
    hasWon.incrementScore(Openness.OPEN, 5);
    Assertions.assertAll(
        () -> Assertions.assertFalse(notWon.winCheck()),
        () -> Assertions.assertTrue(hasWon.winCheck())
    );
  }

}
