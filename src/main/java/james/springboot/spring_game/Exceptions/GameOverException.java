package james.springboot.spring_game.Exceptions;

public class GameOverException extends Exception {
    public GameOverException(Integer winner) {
        super("Game Over! Player " + winner + " was the winner");

    }

}
