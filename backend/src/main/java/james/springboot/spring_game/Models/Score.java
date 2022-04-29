package james.springboot.spring_game.Models;

import james.springboot.spring_game.Exceptions.InvalidOpenessStateException;
import james.springboot.spring_game.Utilities.Utilities;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Score {
  private int[][] score;
  private final int xInARow;
  //{CLOSED, SEMI, OPEN} {{Length=0, Length=1....Length=5},...}
  private final Integer[][] NEXT_PLAYER_WEIGHTS = {{0, 2, 3, 4, 5, 1000}, {0, 3, 5, 8, 400, 1000}, {0, 5, 20, 100, 500,
      1000}};// Indicates the weights that scores are multiplied by.
  // The scores represent how many lines of a certain length and certain openess
  // (whether the two ends are blocked) exist in the board
  private final Integer[][] CURRENT_PLAYER_WEIGHTS = {{0, 1, 1, 2, 3, 1000}, {0, 2, 3, 5, 50, 1000}, {0, 4, 10, 50, 200,
      1000}};

  public Score(int xInARow) {
    this.xInARow = xInARow;
    score = new int[3][xInARow + 1];
  }

  public Score(int[][] score) {
    this.score = score;
    xInARow = score[0].length - 1;
  }

  public int[][] getScore() {
    return this.score;
  }

  public int getXInARow() {
    return xInARow;
  }

  public void increaseScore(Pair<Integer, Openness> scoreDataToIncreasePositive, Pair<Integer, Openness> scoreDataToIncreaseNegative) throws InvalidOpenessStateException {
//    Openness prevOpennessPositive = Openness.addInts(1, scoreDataToIncreasePositive.b);
//    Openness prevOpennessNegative = Openness.addInts(1, scoreDataToIncreaseNegative.b);
    decrementScore(scoreDataToIncreasePositive.b, scoreDataToIncreasePositive.a);
    decrementScore(scoreDataToIncreaseNegative.b, scoreDataToIncreaseNegative.a);
    Openness newOpenness = Openness.combineLines(scoreDataToIncreaseNegative.b, scoreDataToIncreasePositive.b);
    Integer newLength = scoreDataToIncreaseNegative.a + scoreDataToIncreasePositive.a + 1;
    incrementScore(newOpenness, newLength);
  }

  private void decrementScore(Openness openness, Integer scoreIndex) {
    score[openness.intConversion][scoreIndex] -= 1;
  }

  public void incrementScore(Openness openness, Integer scoreIndex) {
    if (scoreIndex > xInARow) {
      scoreIndex = xInARow;
    }
    score[openness.intConversion][scoreIndex] += 1;
  }

  public Score clone() {
    return new Score(Utilities.deepCopyDoubleIntArray(score, 3, xInARow));
  }

  public boolean winCheck() {
    return score[0][xInARow] > 0 || score[1][xInARow] > 0 || score[2][xInARow] > 0;
  }

  public void decreaseOpeness(Pair<Openness, Integer> oldLineData) {
    int oldOpeness = oldLineData.a.intConversion;
    int oldLineLength = oldLineData.b;
    score[oldOpeness][oldLineLength] -= 1;
    score[oldOpeness - 1][oldLineLength] += 1;
  }

  public int calculateScore(boolean current) {
    int sum = 0;
//        log.debug((current ? "Just moved " : "About to move ") + Arrays.deepToString(score));
    if (current) {
      for (int openess = 0; openess < score.length; openess++) {
        for (int lineLength = 1; lineLength < xInARow + 1; lineLength++) {
          sum += score[openess][lineLength] * CURRENT_PLAYER_WEIGHTS[openess][lineLength];
        }
      }
    } else {
      for (int openess = 0; openess < score.length; openess++) {
        for (int lineLength = 1; lineLength < xInARow + 1; lineLength++) {
          sum += score[openess][lineLength] * NEXT_PLAYER_WEIGHTS[openess][lineLength];
        }
      }
    }
    return sum;
  }
}
