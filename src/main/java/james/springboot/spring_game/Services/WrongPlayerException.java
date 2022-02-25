package james.springboot.spring_game.Services;

public class WrongPlayerException extends Exception {
    public WrongPlayerException() {
        super("It is not your turn");
    }
}
