package james.springboot.spring_game.Models;

import james.springboot.spring_game.Exceptions.InvalidOpenessStateException;

public enum Openness {
  OPEN(2), SEMI(1), CLOSED(0);
  public final int intConversion;

  Openness(int intConversion) {
    this.intConversion = intConversion;
  }

  public static Openness addInts(Openness openness1, Openness openness2) {
    int addition = openness1.intConversion + openness2.intConversion;
    switch (addition) {
      case 0:
        return CLOSED;
      case 1:
        return SEMI;
      default:
        return OPEN;
    }
  }

  public static Openness addInts(int openess1, Openness openness2) {
    int addition = openess1 + openness2.intConversion;
    switch (addition) {
      case 0:
        return CLOSED;
      case 1:
        return SEMI;
      default:
        return OPEN;
    }
  }

  public static Openness combineLines(Openness line1, Openness line2) throws InvalidOpenessStateException {
    if (line1 == CLOSED || line2 == CLOSED) {
      throw new InvalidOpenessStateException("Can't have a closed line, as placing a piece next to it");
    }
    int newIntConversion = line1.intConversion + line2.intConversion - 2;
    switch (newIntConversion) {
      case 0:
        return CLOSED;
      case 1:
        return SEMI;
      case 2:
        return OPEN;
      default:
        throw new InvalidOpenessStateException("Not sure how you got here, but you shouldn't be able to combine lines like this");
    }
  }
}
