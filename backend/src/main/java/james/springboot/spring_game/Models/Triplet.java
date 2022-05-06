package james.springboot.spring_game.Models;

public class Triplet<Type1, Type2, Type3> {
  public Type1 a;
  public Type2 b;
  public Type3 c;

  public Triplet(Type1 a, Type2 b, Type3 c) {
    this.a = a;
    this.b = b;
    this.c = c;
  }

}