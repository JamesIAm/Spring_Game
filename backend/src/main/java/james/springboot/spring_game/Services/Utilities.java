package james.springboot.spring_game.Services;

public class Utilities {
    public static int[][] deepCopyBoard(int[][] board, int BOARD_SIZE) {
        int[][] newBoard = new int[BOARD_SIZE][BOARD_SIZE];
        for (int index = 0;index < board.length; index++) {
            newBoard[index] = board[index].clone();
        }
        return newBoard;
    }
}
