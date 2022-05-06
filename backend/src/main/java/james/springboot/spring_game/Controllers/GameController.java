package james.springboot.spring_game.Controllers;

import james.springboot.spring_game.Exceptions.GameOverException;
import james.springboot.spring_game.Exceptions.InvalidMoveException;
import james.springboot.spring_game.Exceptions.WrongPlayerException;
import james.springboot.spring_game.Models.Board;
import james.springboot.spring_game.Models.Move;
import james.springboot.spring_game.Services.AgentService;
import james.springboot.spring_game.Services.GameService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;

@RestController
@CrossOrigin()
@RequestMapping("/game")
@Log4j2
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
      try {
        gameService.playMove(1, y, x);
      } catch (WrongPlayerException e) {
        log.error("Human played when it was not their go");
      }
      //Below line plays a move. Need to do a copy of the board?
      int[][] boardDeepCopy = ((int[][]) gameService.getBoard(false).get("board"));
      Board boardnew = new Board(boardDeepCopy);
      Move move = agentService.move(boardnew);
      gameService.playMove(2, move.y, move.x);
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
