package nl.wernerdegroot.applicatives.records;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Record3Test {

    public record Color(int red, int green, int blue) implements Record3<Color, Integer, Integer, Integer> {
    }

    public record Town(int population, int altitude, int yearEstablished) {
    }

    @Test
    public void givenPerson() {
        // See https://benjiweber.co.uk/blog/2020/09/19/fun-with-java-records/
        Color color = new Color(1, 2, 3);
        Town expected = new Town(1, 2, 3);
        Town toVerify = color.decomposeTo(Town::new);
        assertEquals(expected, toVerify);
    }
}
