package james.springboot.spring_game.Models;

import james.springboot.spring_game.Exceptions.InvalidOpenessStateException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@Slf4j
public class ScoreTest {
  private final int X_IN_A_ROW = 5;

  @Test
  public void whenAScoreIsCreated_allValuesAre0() {
    Score testScore = new Score();
    int[][] scoreFromTest = testScore.getScore();
    Assertions.assertAll(
        () -> Assertions.assertArrayEquals(scoreFromTest[0], new int[]{0, 0, 0, 0, 0, 0}),
        () -> Assertions.assertArrayEquals(scoreFromTest[1], new int[]{0, 0, 0, 0, 0, 0}),
        () -> Assertions.assertArrayEquals(scoreFromTest[2], new int[]{0, 0, 0, 0, 0, 0})
    );
  }

  @Test
  public void whenAScoreIsCreatedWithAXInARowOf5_theScoreObjectContains3ArraysOfLength6() {
    Score testScore = new Score();
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
    Score originalScore = new Score();
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
    Score originalScore = new Score();
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
    Score originalScore = new Score();
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
    Score originalScore = new Score();
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
    Score originalScore = new Score();
    Score newScore = originalScore.clone();
    newScore.incrementScore(Openness.CLOSED, 0);
    Assertions.assertAll(
        () -> Assertions.assertEquals(0, originalScore.getScore()[0][0]),
        () -> Assertions.assertEquals(1, newScore.getScore()[0][0])
    );
  }

  @Test
  public void winCheck() {
    Score notWon = new Score();
    Score hasWon = new Score();
    notWon.incrementScore(Openness.CLOSED, 4);
    hasWon.incrementScore(Openness.OPEN, 5);
    Assertions.assertAll(
        () -> Assertions.assertFalse(notWon.winCheck()),
        () -> Assertions.assertTrue(hasWon.winCheck())
    );
  }

  @Test
  void decreaseOpeness() {
    Score originalScore = new Score();
    originalScore.incrementScore(Openness.SEMI, 3);
    int[][] scoreFromTest = originalScore.getScore();
    originalScore.decreaseOpeness(new Pair<>(Openness.SEMI, 3));
    Assertions.assertAll(
        () -> Assertions.assertArrayEquals(new int[]{0, 0, 0, 1, 0, 0}, scoreFromTest[0]),
        () -> Assertions.assertArrayEquals(new int[]{0, 0, 0, 0, 0, 0}, scoreFromTest[1]),
        () -> Assertions.assertArrayEquals(new int[]{0, 0, 0, 0, 0, 0}, scoreFromTest[2])
    );
  }

  @Test
  public void calculateScore_0IndexDoesntIncreaseScore() {
    Score score1 = new Score();
    Score score2 = new Score();
    score1.incrementScore(Openness.CLOSED, 0);
    score1.incrementScore(Openness.SEMI, 0);
    score1.incrementScore(Openness.OPEN, 0);
    Assertions.assertEquals(score1.calculateScore(false), score2.calculateScore(false));
  }

  @Test
  public void calculateScore_5IndexHasScoreOf1000() {
    Score score1 = new Score();
    score1.incrementScore(Openness.CLOSED, 5);
    score1.incrementScore(Openness.SEMI, 5);
    score1.incrementScore(Openness.OPEN, 5);
    Assertions.assertEquals(3000, score1.calculateScore(false));
  }

  @Test
  public void calculateScore_nextPlayerScoreIsHigher() {
    for (Openness openness : new Openness[]{Openness.CLOSED, Openness.SEMI, Openness.OPEN}) {
      for (int scoreIndex = 1; scoreIndex < X_IN_A_ROW; scoreIndex++) {
        try {
          Score score1 = new Score();
          Score score2 = new Score();
          score1.incrementScore(openness, scoreIndex);
          score2.incrementScore(openness, scoreIndex);
          Assertions.assertTrue(score1.calculateScore(false) > score2.calculateScore(true));
        } catch (AssertionError e) {
          log.info("Failed on openness: " + openness + ", length: " + scoreIndex);
          throw e;
        }
      }
    }
  }

  //Tests that moving to a higher amount of openess always increases a score
  @Test
  public void calculateScore_higherOpennessScoreIsHigher() {
    for (Openness openness : new Openness[]{Openness.CLOSED, Openness.SEMI}) {
      Openness secondOpeness = openness == Openness.CLOSED ? Openness.SEMI : Openness.OPEN;
      for (int scoreIndex = 1; scoreIndex < X_IN_A_ROW; scoreIndex++) {
        for (boolean currentPlayer : new boolean[]{false, true}) {
          try {
            Score score1 = new Score();
            Score score2 = new Score();
            score1.incrementScore(openness, scoreIndex);
            score2.incrementScore(secondOpeness, scoreIndex);
            Assertions.assertTrue(score1.calculateScore(currentPlayer) < score2.calculateScore(currentPlayer));
          } catch (AssertionError e) {
            log.info("Failed on openness: " + openness + ", length: " + scoreIndex);
            throw e;
          }
        }
      }
    }
  }
}
