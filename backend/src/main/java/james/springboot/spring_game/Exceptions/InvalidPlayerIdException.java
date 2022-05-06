package james.springboot.spring_game.Exceptions;

public class InvalidPlayerIdException extends Exception {
  public InvalidPlayerIdException(String message) {
    super(message);
  }
}
