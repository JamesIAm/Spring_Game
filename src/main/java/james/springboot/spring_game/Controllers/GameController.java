package james.springboot.spring_game.Controllers;

import java.util.ArrayList;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import james.springboot.spring_game.Services.GameService;

@RestController
@RequestMapping("/game")
public class GameController {
    private GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/getState/{gameId}")
    public ArrayList<ArrayList<Integer>> getGameState(@PathVariable("gameId") int gameId) {
        return gameService.getBoard();
    }
}
