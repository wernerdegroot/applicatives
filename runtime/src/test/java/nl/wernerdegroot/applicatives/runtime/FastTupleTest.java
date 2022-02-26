package nl.wernerdegroot.applicatives.runtime;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FastTupleTest {

    @Test
    public void shouldConstructFastTupleWithRightMaxSizeAndCorrectElementsInitialized() {
        FastTuple<String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String> original = new FastTuple<>(new Object[]{"alpha", "bravo", "charlie"});
        FastTuple<String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String> copied = original.withThird("-.-.");
        assertNotSame(copied, original);
    }

    @Test
    public void shouldUseMutationWhenPossibleButMakeCopiesWhenNecessary() {
        FastTuple<String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String> original = new FastTuple<>("alpha", "bravo", 26);
        FastTuple<String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String> mutated = original
                .withThird("charlie")
                .withFourth("delta")
                .withFifth("echo")
                .withSixth("foxtrot")
                .withSeventh("golf")
                .withEighth("hotel")
                .withNinth("india")
                .withTenth("juliet")
                .withEleventh("kilo")
                .withTwelfth("lima")
                .withThirteenth("mike")
                .withFourteenth("november")
                .withFifteenth("oscar")
                .withSixteenth("papa")
                .withSeventeenth("quebec")
                .withEighteenth("romeo")
                .withNineteenth("sierra")
                .withTwentieth("tango")
                .withTwentyFirst("uniform")
                .withTwentySecond("victor")
                .withTwentyThird("whiskey")
                .withTwentyFourth("x-ray")
                .withTwentyFifth("yankee")
                .withTwentySixth("zulu");
        ;

        assertSame(original, mutated);
        assertEquals("alpha", mutated.getFirst());
        assertEquals("bravo", mutated.getSecond());
        assertEquals("charlie", mutated.getThird());
        assertEquals("delta", mutated.getFourth());
        assertEquals("echo", mutated.getFifth());
        assertEquals("foxtrot", mutated.getSixth());
        assertEquals("golf", mutated.getSeventh());
        assertEquals("hotel", mutated.getEighth());
        assertEquals("india", mutated.getNinth());
        assertEquals("juliet", mutated.getTenth());
        assertEquals("kilo", mutated.getEleventh());
        assertEquals("lima", mutated.getTwelfth());
        assertEquals("mike", mutated.getThirteenth());
        assertEquals("november", mutated.getFourteenth());
        assertEquals("oscar", mutated.getFifteenth());
        assertEquals("papa", mutated.getSixteenth());
        assertEquals("quebec", mutated.getSeventeenth());
        assertEquals("romeo", mutated.getEighteenth());
        assertEquals("sierra", mutated.getNineteenth());
        assertEquals("tango", mutated.getTwentieth());
        assertEquals("uniform", mutated.getTwentyFirst());
        assertEquals("victor", mutated.getTwentySecond());
        assertEquals("whiskey", mutated.getTwentyThird());
        assertEquals("x-ray", mutated.getTwentyFourth());
        assertEquals("yankee", mutated.getTwentyFifth());
        assertEquals("zulu", mutated.getTwentySixth());

        FastTuple<String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String> copied;

        copied = mutated.withSecond("-...");
        assertNotSame(mutated, copied);
        assertEquals("bravo", mutated.getSecond());
        assertEquals("-...", copied.getSecond());

        copied = mutated.withThird("-.-.");
        assertNotSame(mutated, copied);
        assertEquals("charlie", mutated.getThird());
        assertEquals("-.-.", copied.getThird());

        copied = mutated.withFourth("-..");
        assertNotSame(mutated, copied);
        assertEquals("delta", mutated.getFourth());
        assertEquals("-..", copied.getFourth());

        copied = mutated.withFifth(".");
        assertNotSame(mutated, copied);
        assertEquals("echo", mutated.getFifth());
        assertEquals(".", copied.getFifth());

        copied = mutated.withSixth("..-.");
        assertNotSame(mutated, copied);
        assertEquals("foxtrot", mutated.getSixth());
        assertEquals("..-.", copied.getSixth());

        copied = mutated.withSeventh("--.");
        assertNotSame(mutated, copied);
        assertEquals("golf", mutated.getSeventh());
        assertEquals("--.", copied.getSeventh());

        copied = mutated.withEighth("....");
        assertNotSame(mutated, copied);
        assertEquals("hotel", mutated.getEighth());
        assertEquals("....", copied.getEighth());

        copied = mutated.withNinth("..");
        assertNotSame(mutated, copied);
        assertEquals("india", mutated.getNinth());
        assertEquals("..", copied.getNinth());

        copied = mutated.withTenth(".---");
        assertNotSame(mutated, copied);
        assertEquals("juliet", mutated.getTenth());
        assertEquals(".---", copied.getTenth());

        copied = mutated.withEleventh("-.-");
        assertNotSame(mutated, copied);
        assertEquals("kilo", mutated.getEleventh());
        assertEquals("-.-", copied.getEleventh());

        copied = mutated.withTwelfth(".-..");
        assertNotSame(mutated, copied);
        assertEquals("lima", mutated.getTwelfth());
        assertEquals(".-..", copied.getTwelfth());

        copied = mutated.withThirteenth("--");
        assertNotSame(mutated, copied);
        assertEquals("mike", mutated.getThirteenth());
        assertEquals("--", copied.getThirteenth());

        copied = mutated.withFourteenth("-.");
        assertNotSame(mutated, copied);
        assertEquals("november", mutated.getFourteenth());
        assertEquals("-.", copied.getFourteenth());

        copied = mutated.withFifteenth("---");
        assertNotSame(mutated, copied);
        assertEquals("oscar", mutated.getFifteenth());
        assertEquals("---", copied.getFifteenth());

        copied = mutated.withSixteenth(".--.");
        assertNotSame(mutated, copied);
        assertEquals("papa", mutated.getSixteenth());
        assertEquals(".--.", copied.getSixteenth());

        copied = mutated.withSeventeenth("--.-");
        assertNotSame(mutated, copied);
        assertEquals("quebec", mutated.getSeventeenth());
        assertEquals("--.-", copied.getSeventeenth());

        copied = mutated.withEighteenth(".-.");
        assertNotSame(mutated, copied);
        assertEquals("romeo", mutated.getEighteenth());
        assertEquals(".-.", copied.getEighteenth());

        copied = mutated.withNineteenth("...");
        assertNotSame(mutated, copied);
        assertEquals("sierra", mutated.getNineteenth());
        assertEquals("...", copied.getNineteenth());

        copied = mutated.withTwentieth("-");
        assertNotSame(mutated, copied);
        assertEquals("tango", mutated.getTwentieth());
        assertEquals("-", copied.getTwentieth());

        copied = mutated.withTwentyFirst("..-");
        assertNotSame(mutated, copied);
        assertEquals("uniform", mutated.getTwentyFirst());
        assertEquals("..-", copied.getTwentyFirst());

        copied = mutated.withTwentySecond("...-");
        assertNotSame(mutated, copied);
        assertEquals("victor", mutated.getTwentySecond());
        assertEquals("...-", copied.getTwentySecond());

        copied = mutated.withTwentyThird(".--");
        assertNotSame(mutated, copied);
        assertEquals("whiskey", mutated.getTwentyThird());
        assertEquals(".--", copied.getTwentyThird());

        copied = mutated.withTwentyFourth("-..-");
        assertNotSame(mutated, copied);
        assertEquals("x-ray", mutated.getTwentyFourth());
        assertEquals("-..-", copied.getTwentyFourth());

        copied = mutated.withTwentyFifth("-.--");
        assertNotSame(mutated, copied);
        assertEquals("yankee", mutated.getTwentyFifth());
        assertEquals("-.--", copied.getTwentyFifth());

        copied = mutated.withTwentySixth("--..");
        assertNotSame(mutated, copied);
        assertEquals("zulu", mutated.getTwentySixth());
        assertEquals("--..", copied.getTwentySixth());

    }

    @Test
    public void shouldThrowExceptionWhenAddingThirdElementToATupleThatOnlyHasRoomForTwo() {
        FastTuple<String, Boolean, Integer, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object> pair = new FastTuple<>("alpha", true, 2);
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> pair.withThird(4));
    }
}
