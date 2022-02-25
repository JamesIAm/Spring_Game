package james.springboot.spring_game.Controllers;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import james.springboot.spring_game.Exceptions.GameOverException;
import james.springboot.spring_game.Exceptions.InvalidMoveException;
import james.springboot.spring_game.Services.GameService;
import james.springboot.spring_game.Services.WrongPlayerException;

@RestController
@CrossOrigin("http://localhost:3000")
@RequestMapping("/game")
public class GameController {
    private GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/getState")
    public ArrayList<ArrayList<Integer>> getGameState(/* @PathVariable("gameId") int gameId */)
            throws GameOverException {
        return gameService.getBoard(false);
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
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.EXPECTATION_FAILED);
        } catch (WrongPlayerException | InvalidMoveException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }
}
