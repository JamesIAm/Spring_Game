package james.springboot.spring_game.Utilities;

import james.springboot.spring_game.Models.Pair;

import java.util.ArrayList;

public class Utilities {
    public static int[][] deepCopyDoubleIntArray(int[][] prevArray, int FIRST_ARRAY_LENGTH, int SECOND_ARRAY_LENGTH) {
        int[][] newBoard = new int[FIRST_ARRAY_LENGTH][SECOND_ARRAY_LENGTH];
        for (int index = 0; index < prevArray.length; index++) {
            newBoard[index] = prevArray[index].clone();
        }
        return newBoard;
    }

    // Creates the order that the board should be searched for existing lines. This
    // was from a previous iteration of the line
    // search that searched the entire board every time
    // It defines an order to search over the data so that each row goes vertically,
    // horizonatally or diagonally
    public static void defineOrders(int BOARD_SIZE,
                                    ArrayList<ArrayList<Pair<Integer, Integer>>> horizontalCoords,
                                    ArrayList<ArrayList<Pair<Integer, Integer>>> verticalCoords,
                                    ArrayList<ArrayList<Pair<Integer, Integer>>> upDiagCoords,
                                    ArrayList<ArrayList<Pair<Integer, Integer>>> downDiagCoords) {
        defineHorizontalCoords(BOARD_SIZE, horizontalCoords);
        defineVerticalCoords(BOARD_SIZE, verticalCoords);
        defineDownDiagCoords(BOARD_SIZE, downDiagCoords);
        defineUpDiagCoords(BOARD_SIZE, upDiagCoords);
    }

    private static void defineHorizontalCoords(int BOARD_SIZE, ArrayList<ArrayList<Pair<Integer, Integer>>> horizontalCoords) {

        for (int y = 0; y < BOARD_SIZE; y++) {
            ArrayList<Pair<Integer, Integer>> lineOfMoves = new ArrayList<>();
            for (int x = 0; x < BOARD_SIZE; x++) {
                lineOfMoves.add(new Pair<>(x, y));

            }
            horizontalCoords.add(lineOfMoves);
        }
    }

    private static void defineVerticalCoords(int BOARD_SIZE, ArrayList<ArrayList<Pair<Integer, Integer>>> verticalCoords) {
        for (int x = 0; x < BOARD_SIZE; x++) {
            ArrayList<Pair<Integer, Integer>> lineOfMoves = new ArrayList<>();
            for (int y = 0; y < BOARD_SIZE; y++) {
                lineOfMoves.add(new Pair<>(x, y));

            }
            verticalCoords.add(lineOfMoves);
        }
    }

    //Down diag
    private static void defineDownDiagCoords(int BOARD_SIZE, ArrayList<ArrayList<Pair<Integer, Integer>>> downDiagCoords) {
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
    }

    private static void defineUpDiagCoords(int BOARD_SIZE, ArrayList<ArrayList<Pair<Integer, Integer>>> upDiagCoords) {
        //Up diag
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
    }
}
