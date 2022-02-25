package james.springboot.spring_game.Controllers;

import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import james.springboot.spring_game.Exceptions.GameOverException;
import james.springboot.spring_game.Exceptions.InvalidMoveException;
import james.springboot.spring_game.Exceptions.WrongPlayerException;
import james.springboot.spring_game.Services.GameService;

@RestController
@CrossOrigin("http://localhost:3000")
@RequestMapping("/game")
public class GameController {
    private GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/getState/{gameOver}")
    public ResponseEntity<HashMap<String, Object>> getGameState(@PathVariable("gameOver") Boolean gameOver) {
        try {
            HashMap<String, Object> gameState = gameService.getBoard(gameOver);
            return new ResponseEntity<HashMap<String, Object>>(gameState, HttpStatus.OK);
        } catch (GameOverException e) {
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, e.getMessage());
        }

    }

    @PostMapping("/makeMove")
    public ResponseEntity<String> makeMove(@RequestBody HashMap<String, Integer> body) {
        Integer x = body.get("x");
        Integer y = body.get("y");
        Integer playerId = body.get("player");
        try {

            gameService.playMove(playerId, y, x);
            return new ResponseEntity<String>("Played move", HttpStatus.OK);
        } catch (NullPointerException e) {
            return new ResponseEntity<String>("One or more fields missing, needs x, y and player",
                    HttpStatus.BAD_REQUEST);
        } catch (GameOverException e) {
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, e.getMessage());
        } catch (WrongPlayerException | InvalidMoveException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @PostMapping("/resetBoard")
    public void resetBoard() {
        gameService.resetBoard();
    }
}
