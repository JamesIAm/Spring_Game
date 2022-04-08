package james.springboot.spring_game.Models;

public enum Openess {
    OPEN(2), SEMI(1), CLOSED(0);
    public final int intConversion;

    Openess(int intConversion) {
        this.intConversion = intConversion;
    }

    public static Openess addInts(Openess openess1, Openess openess2) {
        int addition = openess1.intConversion + openess2.intConversion;
        switch (addition) {
            case 0:
                return CLOSED;
            case 1:
                return SEMI;
            default:
                return OPEN;
        }
    }

    public static Openess addInts(int openess1, Openess openess2) {
        int addition = openess1 + openess2.intConversion;
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
