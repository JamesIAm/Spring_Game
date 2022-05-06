package james.springboot.spring_game.Models;

public class Move {
  public final int x;
  public final int y;
  public int score;

  public Move(int x, int y, int score) {
    this.x = x;
    this.y = y;
    this.score = score;
  }

  public Move(int x, int y) {
    this.x = x;
    this.y = y;
    this.score = 0;
  }

  public Move(int sum) {
    this.x = 0;
    this.y = 0;
    this.score = sum;
  }

  public Move() {
    this.x = 0;
    this.y = 0;
    this.score = 0;
  }
}
