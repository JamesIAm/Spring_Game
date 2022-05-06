package james.springboot.spring_game.Models;

public class Pair<Type1, Type2> {
  public Type1 a;
  public Type2 b;

  public Pair(Type1 a, Type2 b) {
    this.a = a;
    this.b = b;
  }

  public Pair() {
    this.a = null;
    this.b = null;
  }
}
