package james.springboot.spring_game.Exceptions;

public class GameOverException extends Exception {
  public GameOverException(Integer winner) {
    super(String.valueOf(winner));

  }

}
