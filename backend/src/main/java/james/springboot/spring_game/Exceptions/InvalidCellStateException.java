package james.springboot.spring_game.Exceptions;

public class InvalidCellStateException extends Exception {
  public InvalidCellStateException(String message) {
    super(message);
  }
}
