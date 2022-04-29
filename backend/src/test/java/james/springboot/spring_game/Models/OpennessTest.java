package james.springboot.spring_game.Models;

import james.springboot.spring_game.Exceptions.InvalidOpenessStateException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OpennessTest {

  @Test
  public void addIntsWith2Enums() {
    Assertions.assertAll(
        () -> Assertions.assertEquals(Openness.CLOSED, Openness.addInts(Openness.CLOSED, Openness.CLOSED)),
        () -> Assertions.assertEquals(Openness.SEMI, Openness.addInts(Openness.CLOSED, Openness.SEMI)),
        () -> Assertions.assertEquals(Openness.OPEN, Openness.addInts(Openness.CLOSED, Openness.OPEN)),
        () -> Assertions.assertEquals(Openness.SEMI, Openness.addInts(Openness.SEMI, Openness.CLOSED)),
        () -> Assertions.assertEquals(Openness.OPEN, Openness.addInts(Openness.SEMI, Openness.SEMI)),
        () -> Assertions.assertEquals(Openness.OPEN, Openness.addInts(Openness.SEMI, Openness.OPEN)),
        () -> Assertions.assertEquals(Openness.OPEN, Openness.addInts(Openness.OPEN, Openness.CLOSED)),
        () -> Assertions.assertEquals(Openness.OPEN, Openness.addInts(Openness.OPEN, Openness.SEMI)),
        () -> Assertions.assertEquals(Openness.OPEN, Openness.addInts(Openness.OPEN, Openness.OPEN))
    );
  }

  @Test
  public void addIntsWithEnumAndInt() {
    Assertions.assertAll(
        () -> Assertions.assertEquals(Openness.CLOSED, Openness.addInts(0, Openness.CLOSED)),
        () -> Assertions.assertEquals(Openness.SEMI, Openness.addInts(0, Openness.SEMI)),
        () -> Assertions.assertEquals(Openness.OPEN, Openness.addInts(0, Openness.OPEN)),
        () -> Assertions.assertEquals(Openness.SEMI, Openness.addInts(1, Openness.CLOSED)),
        () -> Assertions.assertEquals(Openness.OPEN, Openness.addInts(1, Openness.SEMI)),
        () -> Assertions.assertEquals(Openness.OPEN, Openness.addInts(1, Openness.OPEN)),
        () -> Assertions.assertEquals(Openness.OPEN, Openness.addInts(2, Openness.CLOSED)),
        () -> Assertions.assertEquals(Openness.OPEN, Openness.addInts(2, Openness.SEMI)),
        () -> Assertions.assertEquals(Openness.OPEN, Openness.addInts(2, Openness.OPEN))
    );
  }

  @Test
  public void combineLines() {
    Assertions.assertAll(
        () -> Assertions.assertEquals(Openness.CLOSED, Openness.combineLines(Openness.SEMI, Openness.SEMI)),
        () -> Assertions.assertEquals(Openness.SEMI, Openness.combineLines(Openness.SEMI, Openness.OPEN)),
        () -> Assertions.assertEquals(Openness.SEMI, Openness.combineLines(Openness.OPEN, Openness.SEMI)),
        () -> Assertions.assertEquals(Openness.OPEN, Openness.combineLines(Openness.OPEN, Openness.OPEN)),
        () -> Assertions.assertThrows(InvalidOpenessStateException.class,
            () -> Openness.combineLines(Openness.CLOSED, Openness.OPEN)),
        () -> Assertions.assertThrows(InvalidOpenessStateException.class,
            () -> Openness.combineLines(Openness.CLOSED, Openness.CLOSED)),
        () -> Assertions.assertThrows(InvalidOpenessStateException.class,
            () -> Openness.combineLines(Openness.SEMI, Openness.CLOSED))
    );
  }


}