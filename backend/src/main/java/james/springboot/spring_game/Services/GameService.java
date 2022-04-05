package james.springboot.spring_game.Services;

import java.util.HashMap;

import org.springframework.stereotype.Service;

import james.springboot.spring_game.Exceptions.GameOverException;
import james.springboot.spring_game.Exceptions.InvalidMoveException;
import james.springboot.spring_game.Exceptions.WrongPlayerException;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class GameService {
    private final int BOARD_SIZE = 10;
    private int winner = 0;
    private int currentPlayer;

    private int[][] board = new int[BOARD_SIZE][BOARD_SIZE];
    // 0 = empty, 1 = player-1, 2 = player-2

    public GameService() {
        resetBoard();
    }

    public void resetBoard() {
        board = new int[BOARD_SIZE][BOARD_SIZE];
//        for (Integer[] row: board) {
//            Arrays.fill(row, 0);
//        }
        // Initialises board of BOARD_SIZE to be all 0s
        currentPlayer = 1;
        winner = 0;
    }

    public HashMap<String, Object> getBoard(boolean gameOver) throws GameOverException {
        if (winner != 0) {
            if (!gameOver) {
                throw new GameOverException(winner);
            }
        }
        HashMap<String, Object> toReturn = new HashMap<>();
        toReturn.put("board", Utilities.deepCopyBoard(board, BOARD_SIZE));
        toReturn.put("currentPlayer", this.currentPlayer);
        return toReturn;
    }


    public void playMove(@NonNull Integer playerId, @NonNull Integer y, @NonNull Integer x)
            throws InvalidMoveException, GameOverException, WrongPlayerException {
        if (this.currentPlayer != playerId) {
            throw new WrongPlayerException();
        }
        if (winner != 0) {
            throw new GameOverException(winner);
        }
        if (this.board[y][x] != 0) {
            log.error("Invalid move: " + x  +" " + y + "\tPlayer: " + playerId );
            throw new InvalidMoveException();
        }
        this.board[y][x] =  playerId;
        winCheck();
        if (currentPlayer == 1) {
            currentPlayer = 2;
        } else {
            currentPlayer = 1;
        }
    }

    private void winCheck() throws GameOverException {
        try {
            verticalCheck();
            horizontalCheck();
            diagonalCheckRight();
            diagonalCheckLeft();
        } catch (GameOverException e) {
            this.winner = Integer.parseInt(e.getMessage());
            throw e;
        }
        GameService.log.info("No Winner");
    }

    private void verticalCheck() throws GameOverException {
        for (int firstRow = 0; firstRow < BOARD_SIZE - 4; firstRow++) {
            for (int column = 0; column < BOARD_SIZE; column++) {
                int currentCandidateToBeAWinner = board[firstRow][column];
                for (int currentRow = firstRow + 1; currentRow < firstRow + 5; currentRow++) {
                    if (board[currentRow][column] != currentCandidateToBeAWinner) {
                        currentCandidateToBeAWinner = 0;
                        break;
                    }
                }
                if (currentCandidateToBeAWinner != 0) {
                    throw new GameOverException(currentCandidateToBeAWinner);
                }
            }
        }
    }

    private void horizontalCheck() throws GameOverException {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int firstColumn = 0; firstColumn < BOARD_SIZE - 4; firstColumn++) {
                int currentCandidateToBeAWinner = board[row][firstColumn];
                for (int currentColumn = firstColumn + 1; currentColumn < firstColumn + 5; currentColumn++) {
                    if (board[row][currentColumn] != currentCandidateToBeAWinner) {
                        currentCandidateToBeAWinner = 0;
                        break;
                    }
                }
                if (currentCandidateToBeAWinner != 0) {
                    throw new GameOverException(currentCandidateToBeAWinner);
                }
            }
        }
    }

    private void diagonalCheckRight() throws GameOverException {
        for (int row = 0; row < BOARD_SIZE - 4; row++) {
            for (int column = 0; column < BOARD_SIZE - 4; column++) {
                int currentCandidateToBeAWinner = board[row][column];
                for (int currentAddition = 1; currentAddition < 5; currentAddition++) {
                    if (board[row + currentAddition][column + currentAddition] != currentCandidateToBeAWinner) {
                        currentCandidateToBeAWinner = 0;
                        break;
                    }
                }
                if (currentCandidateToBeAWinner != 0) {
                    throw new GameOverException(currentCandidateToBeAWinner);
                }
            }
        }
    }

    private void diagonalCheckLeft() throws GameOverException {
        for (int row = 0; row < BOARD_SIZE - 4; row++) {
            for (int column = 4; column < BOARD_SIZE; column++) {
                int currentCandidateToBeAWinner = board[row][column];
                for (int currentAddition = 1; currentAddition < 5; currentAddition++) {
                    if (board[row + currentAddition][column - currentAddition] != currentCandidateToBeAWinner) {
                        currentCandidateToBeAWinner = 0;
                        break;
                    }
                }
                if (currentCandidateToBeAWinner != 0) {
                    throw new GameOverException(currentCandidateToBeAWinner);
                }
            }
        }
    }
}
