package james.springboot.spring_game.Models;

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
}
