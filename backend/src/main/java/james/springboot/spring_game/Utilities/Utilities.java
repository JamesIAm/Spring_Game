package james.springboot.spring_game.Utilities;

public class Utilities {
    public static int[][] deepCopyDoubleIntArray(int[][] prevArray, int FIRST_ARRAY_LENGTH, int SECOND_ARRAY_LENGTH) {
        int[][] newBoard = new int[FIRST_ARRAY_LENGTH][SECOND_ARRAY_LENGTH];
        for (int index = 0; index < prevArray.length; index++) {
            newBoard[index] = prevArray[index].clone();
        }
        return newBoard;
    }
}
