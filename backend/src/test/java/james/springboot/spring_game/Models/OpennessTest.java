package james.springboot.spring_game.Models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OpennessTest {

    @Test
    void addIntsWith2Enums() {
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
    void addIntsWithEnumAndInt() {
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


}