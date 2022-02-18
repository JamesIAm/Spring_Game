package james.springboot.spring_game.Exceptions;

public class InvalidMoveException extends Exception {
    public InvalidMoveException() {
        super("Invalid move");
    }

}
