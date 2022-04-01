package james.springboot.spring_game.Controllers;

import java.util.HashMap;

import james.springboot.spring_game.Move;
import james.springboot.spring_game.Services.AgentService;
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
import james.springboot.spring_game.Services.AgentService;

@RestController
@CrossOrigin("http://localhost:3000")
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;
    private final AgentService agentService;

    public GameController(GameService gameService, AgentService agentService) {
        this.gameService = gameService;
        this.agentService = agentService;
    }

    @GetMapping("/getState/{gameOver}")
    public ResponseEntity<HashMap<String, Object>> getGameState(@PathVariable("gameOver") Boolean gameOver) {
        try {
            HashMap<String, Object> gameState = gameService.getBoard(gameOver);
            return new ResponseEntity<>(gameState, HttpStatus.OK);
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
            int a=0;
            gameService.playMove(1, y, x);
            a=1;
            //Below line plays a move. Need to do a copy of the board?
            Move move = agentService.move((Integer[][]) gameService.getBoard(false).get("board"));
            a=1;
            gameService.playMove(2, move.x, move.y);
            a=1;
            return new ResponseEntity<>("Played move", HttpStatus.OK);
        } catch (NullPointerException e) {
            return new ResponseEntity<>("One or more fields missing, needs x, y and player",
                    HttpStatus.BAD_REQUEST);
        } catch (GameOverException e) {
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, e.getMessage());
        } catch (WrongPlayerException | InvalidMoveException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PostMapping("/resetBoard")
    public void resetBoard() {
        gameService.resetBoard();
    }
}
