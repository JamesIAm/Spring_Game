package james.springboot.spring_game.Services;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

import james.springboot.spring_game.Exceptions.GameOverException;
import james.springboot.spring_game.Exceptions.InvalidMoveException;

@Service
public class GameService {
    private final int BOARD_SIZE = 10;
    private int winner = 0;
    private int currentPlayer;

    private ArrayList<ArrayList<Integer>> board = new ArrayList<>();
    // 0 = empty, 1 = player-1, 2 = player-2

    public GameService() {
        resetBoard();
    }

    public void resetBoard() {
        // Initialises board of BOARD_SIZE to be all 0s
        for (int y = 0; y < BOARD_SIZE; y++) {
            ArrayList<Integer> row = new ArrayList<Integer>();
            for (int x = 0; x < BOARD_SIZE; x++) {
                row.add(0);
            }
            board.add(row);
        }
        currentPlayer = 1;
        winner = 0;
    }

    public ArrayList<ArrayList<Integer>> getBoard(boolean gameOver) throws GameOverException {
        if (winner != 0) {
            throw new GameOverException(winner);
        }
        return this.board;
    }

    public void playMove(Integer player, Integer y, Integer x) throws InvalidMoveException, GameOverException {
        if (winner != 0) {
            throw new GameOverException(winner);
        }
        if (this.board.get(y).get(x) != 0) {
            throw new InvalidMoveException();
        }
        this.board.get(y).set(x, player);
        winCheck();
        if (currentPlayer == 1) {
            currentPlayer = 2;
        } else {
            currentPlayer = 1;
        }
    }

    private void winCheck() {
        Integer winner = verticalCheck();
        if (winner != 0) {
            this.winner = winner;
            return;
        }
        winner = horizontalCheck();
        if (winner != 0) {
            this.winner = winner;
            return;
        }
        winner = diagonalCheckRight();
        if (winner != 0) {
            this.winner = winner;
            return;
        }
        winner = diagonalCheckLeft();
        if (winner != 0) {
            this.winner = winner;
            return;
        }
    }

    private Integer verticalCheck() {
        Integer newWinner = 0;
        for (int firstRow = 0; firstRow < BOARD_SIZE - 4; firstRow++) {
            if (newWinner != 0) {
                break;
            }
            for (int column = 0; column < BOARD_SIZE; column++) {
                int currentCandidateToBeAWinner = board.get(firstRow).get(column);
                for (int currentRow = firstRow + 1; currentRow < firstRow + 5; currentRow++) {
                    if (board.get(currentRow).get(column) != currentCandidateToBeAWinner) {
                        currentCandidateToBeAWinner = 0;
                        break;
                    }
                }
                if (currentCandidateToBeAWinner != 0) {
                    newWinner = currentCandidateToBeAWinner;
                    break;
                }
            }
        }
        return winner;
    }

    private Integer horizontalCheck() {
        Integer newWinner = 0;
        for (int row = 0; row < BOARD_SIZE; row++) {
            if (newWinner != 0) {
                break;
            }
            for (int firstColumn = 0; firstColumn < BOARD_SIZE - 4; firstColumn++) {
                int currentCandidateToBeAWinner = board.get(row).get(firstColumn);
                for (int currentColumn = firstColumn + 1; currentColumn < firstColumn + 5; currentColumn++) {
                    if (board.get(row).get(currentColumn) != currentCandidateToBeAWinner) {
                        currentCandidateToBeAWinner = 0;
                        break;
                    }
                }
                if (currentCandidateToBeAWinner != 0) {
                    newWinner = currentCandidateToBeAWinner;
                    break;
                }
            }
        }
        return winner;
    }

    private Integer diagonalCheckRight() {
        Integer newWinner = 0;
        for (int row = 0; row < BOARD_SIZE - 4; row++) {
            if (newWinner != 0) {
                break;
            }
            for (int column = 0; column < BOARD_SIZE - 4; column++) {
                int currentCandidateToBeAWinner = board.get(row).get(column);
                for (int currentAddition = 1; currentAddition < 5; currentAddition++) {
                    if (board.get(row + currentAddition).get(column + currentAddition) != currentCandidateToBeAWinner) {
                        currentCandidateToBeAWinner = 0;
                        break;
                    }
                }
                if (currentCandidateToBeAWinner != 0) {
                    newWinner = currentCandidateToBeAWinner;
                    break;
                }
            }
        }
        return winner;
    }

    private Integer diagonalCheckLeft() {
        Integer newWinner = 0;
        for (int row = 0; row < BOARD_SIZE - 4; row++) {
            if (newWinner != 0) {
                break;
            }
            for (int column = 4; column < BOARD_SIZE; column++) {
                int currentCandidateToBeAWinner = board.get(row).get(column);
                for (int currentAddition = 1; currentAddition < 5; currentAddition++) {
                    if (board.get(row + currentAddition).get(column - currentAddition) != currentCandidateToBeAWinner) {
                        currentCandidateToBeAWinner = 0;
                        break;
                    }
                }
                if (currentCandidateToBeAWinner != 0) {
                    newWinner = currentCandidateToBeAWinner;
                    break;
                }
            }
        }
        return winner;
    }
}
