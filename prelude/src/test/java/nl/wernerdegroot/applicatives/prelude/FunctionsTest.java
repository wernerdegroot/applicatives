package nl.wernerdegroot.applicatives.prelude;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FunctionsTest {

    @Test
    public void combineAndLift() {
        Function<Random, String> randomName = oneOf("Bulbasaur", "Charmander", "Squirtle");
        Function<Random, Integer> randomHp = oneOf(30, 40, 60);
        Function<Random, EnergyType> randomEnergyType = oneOf(EnergyType.values());
        Function<Random, List<EnergyType>> randomMoveCost = listOf(1, 2, EnergyType.values());
        Function<Random, String> randomMoveName = oneOf("Bubble", "Withdraw", "Ember", "Razor Leaf");
        Function<Random, Integer> randomMoveDamage = oneOf(10, 20, 30);
        Function<Random, Move> randomMove = new Functions<>().combine(randomMoveCost, randomMoveName, randomMoveDamage, Move::new);
        Function<Random, List<Move>> randomMoves = listOf(1, 2, randomMove);
        Function<Random, PokemonCard> randomPokemonCard = new Functions<Random>().lift(PokemonCard::new).apply(randomName, randomHp, randomEnergyType, randomMoves);
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

    private <T> Function<Random, List<T>> listOf(int lowerBound, int upperBound, T... options) {
        return listOf(lowerBound, upperBound, oneOf(options));
    }

    private <T> Function<Random, T> oneOf(T... options) {
        List<T> optionsAsList = asList(options);
        return random -> {
            int randomIndex = random.nextInt(options.length);
            return optionsAsList.get(randomIndex);
        };
    }

    private Function<Random, Integer> randomInt(int lowerBound, int upperBound) {
        return random -> random.nextInt(upperBound - lowerBound + 1) + lowerBound;
    }
}
