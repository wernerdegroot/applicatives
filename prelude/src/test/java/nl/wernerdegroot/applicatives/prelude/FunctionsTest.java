package nl.wernerdegroot.applicatives.prelude;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.mapping;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FunctionsTest {

    @Test
    public void combineAndLiftResults() {
        Function<Random, PokemonCard> randomPokemonCard = Functions.<Random>resultsInstance().lift(PokemonCard::new).apply(
                oneOf("Bulbasaur", "Charmander", "Squirtle"),
                oneOf(30, 40, 60),
                oneOf(EnergyType.values()),
                listOf(
                        1,
                        2,
                        Functions.<Random>resultsInstance().combine(
                                listOf(1, 2, EnergyType.values()),
                                oneOf("Bubble", "Withdraw", "Ember", "Razor Leaf"),
                                oneOf(10, 20, 30),
                                Move::new
                        )
                )
        );
        PokemonCard toVerify = randomPokemonCard.apply(new Random(43));
        PokemonCard expected = PokemonCard.of(
                "Bulbasaur",
                60,
                EnergyType.PSYCHIC,
                asList(
                        Move.of(asList(EnergyType.LIGHTNING, EnergyType.COLORLESS), "Bubble", 20),
                        Move.of(asList(EnergyType.GRASS), "Withdraw", 30)
                )
        );
        assertEquals(expected, toVerify);
    }

    @Test
    public void combineAndLiftParameters() {
        Functions.Parameters<String> printerInstance = Functions.parametersInstance((left, right) -> left + ", " + right);

        Function<PokemonCard, String> pokemonCardPrinter = printerInstance.combine(
                describe("Name"),
                describe("HP"),
                describe("Energy type"),
                Functions.Parameters.many(
                        printerInstance.lift(Move::decompose).apply(
                                Functions.Parameters.many(EnergyType::toString, joining(" + ", "Cost: ", "")),
                                describe("Name"),
                                describe("Damage")
                        ),
                        mapping(s -> String.format("Move { %s }", s), joining(", ", "Moves: [ ", " ]"))
                )
        );

        PokemonCard toPrint = PokemonCard.of(
                "Charmander",
                20,
                EnergyType.FIRE,
                asList(
                        Move.of(asList(EnergyType.COLORLESS), "Scratch", 10),
                        Move.of(asList(EnergyType.FIRE, EnergyType.COLORLESS), "Ember", 30)
                )
        );

        String expected = "Name: Charmander, HP: 20, Energy type: FIRE, Moves: [ Move { Cost: COLORLESS, Name: Scratch, Damage: 10 }, Move { Cost: FIRE + COLORLESS, Name: Ember, Damage: 30 } ]";
        String toVerify = pokemonCardPrinter.apply(toPrint);

        assertEquals(expected, toVerify);
    }

    private <T> Function<Random, List<T>> listOf(int lowerBound, int upperBound, Function<Random, T> generator) {
        return random -> {
            int n = randomInt(lowerBound, upperBound).apply(random);
            List<T> result = new ArrayList<>(n);
            for (int i = 0; i < n; ++i) {
                result.add(generator.apply(random));
            }
            return result;
        };
    }

    @SafeVarargs
    private final <T> Function<Random, List<T>> listOf(int lowerBound, int upperBound, T... options) {
        return listOf(lowerBound, upperBound, oneOf(options));
    }

    @SafeVarargs
    private final <T> Function<Random, T> oneOf(T... options) {
        List<T> optionsAsList = asList(options);
        return random -> {
            int randomIndex = random.nextInt(options.length);
            return optionsAsList.get(randomIndex);
        };
    }

    private Function<Random, Integer> randomInt(int lowerBound, int upperBound) {
        return random -> random.nextInt(upperBound - lowerBound + 1) + lowerBound;
    }

    private <T> Function<T, String> describe(String description) {
        return p -> description + ": " + p;
    }
}
