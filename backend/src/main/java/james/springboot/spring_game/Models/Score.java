package james.springboot.spring_game.Models;

import james.springboot.spring_game.Utilities.Utilities;

public class Score {
    private int[][] score;
    private final int X_IN_A_ROW;
    //{CLOSED, SEMI, OPEN} {{Length=0, Length=1....Length=5},...}
    private final Integer[][] NEXT_PLAYER_WEIGHTS = {{0, 1, 2, 3, 4, 1000}, {0, 3, 5, 8, 500, 1000}, {0, 4, 20, 100, 500,
            1000}};// Indicates the weights that scores are multiplied by.
    // The scores represent how many lines of a certain length and certain openess
    // (whether the two ends are blocked) exist in the board
    private final Integer[][] CURRENT_PLAYER_WEIGHTS = {{0, 1, 1, 2, 3, 1000}, {0, 2, 3, 5, 50, 1000}, {0, 4, 10, 50, 200,
            1000}};

    public Score(int xInARow) {
        this.X_IN_A_ROW = xInARow;
        score = new int[3][xInARow + 1];
    }

    public Score(int[][] score) {
        this.score = score;
        X_IN_A_ROW = score[0].length - 1;
    }

    public void increaseScore(Pair<Integer, Openess> scoreDataToIncreasePositive, Pair<Integer, Openess> scoreDataToIncreaseNegative) {
        Openess prevOpenessPositive = Openess.addInts(1, scoreDataToIncreasePositive.b);
        Openess prevOpenessNegative = Openess.addInts(1, scoreDataToIncreaseNegative.b);
        decrementScore(prevOpenessPositive, scoreDataToIncreasePositive.a);
        decrementScore(prevOpenessNegative, scoreDataToIncreaseNegative.a);
        Openess newOpeness = Openess.addInts(scoreDataToIncreaseNegative.b, scoreDataToIncreasePositive.b);
        Integer newLenth = scoreDataToIncreaseNegative.a + scoreDataToIncreasePositive.a;
        incrementScore(newOpeness, newLenth);
    }

    private void decrementScore(Openess openess, Integer scoreIndex) {
        score[openess.intConversion][scoreIndex] -= 1;
    }

    public void incrementScore(Openess openess, Integer scoreIndex) {
        if (scoreIndex > X_IN_A_ROW) {
            scoreIndex = X_IN_A_ROW;
        }
        score[openess.intConversion][scoreIndex] += 1;
    }

    public Score clone() {
        return new Score(Utilities.deepCopyDoubleIntArray(score, 3, X_IN_A_ROW));
    }

    public boolean winCheck() {
        if (
                score[0][X_IN_A_ROW] > 0 || score[1][X_IN_A_ROW] > 0 || score[2][X_IN_A_ROW] > 0
        ) {
            System.out.println("asd");
        }
        return score[0][X_IN_A_ROW] > 0 || score[1][X_IN_A_ROW] > 0 || score[2][X_IN_A_ROW] > 0;
    }

    public void decreaseOpeness(Pair<Openess, Integer> oldLineData) {
        int oldOpeness = oldLineData.a.intConversion;
        int oldLineLength = oldLineData.b;
        score[oldOpeness][oldLineLength] -= 1;
        score[oldOpeness - 1][oldLineLength] += 1;
    }

    public int calculateScore(boolean current) {
        int sum = 0;
        if (current) {
            for (int openess = 0; openess < score.length; openess++) {
                for (int lineLength = 1; lineLength < X_IN_A_ROW + 1; lineLength++) {
                    sum += score[openess][lineLength] * CURRENT_PLAYER_WEIGHTS[openess][lineLength];
                }
            }
        } else {
            for (int openess = 0; openess < score.length; openess++) {
                for (int lineLength = 1; lineLength < X_IN_A_ROW + 1; lineLength++) {
                    sum += score[openess][lineLength] * NEXT_PLAYER_WEIGHTS[openess][lineLength];
                }
            }
        }
        return sum;
    }
}
