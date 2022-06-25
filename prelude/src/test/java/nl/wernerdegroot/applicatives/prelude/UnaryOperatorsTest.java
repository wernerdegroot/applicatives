package nl.wernerdegroot.applicatives.prelude;

import org.junit.jupiter.api.Test;

import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static nl.wernerdegroot.applicatives.prelude.EnergyType.COLORLESS;
import static nl.wernerdegroot.applicatives.prelude.EnergyType.GRASS;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UnaryOperatorsTest {

    @Test
    public void combineAndLift() {
        UnaryOperator<Move> transformMove = UnaryOperators.instance().lift(Move::new).apply(
                cost -> cost.stream().flatMap(energyType -> Stream.of(energyType, energyType)).collect(toList()),
                name -> name + "!",
                damage -> damage * 2
        );

        UnaryOperator<PokemonCard> transformPokemonCard = UnaryOperators.instance().combine(
                name -> name.toUpperCase(),
                level -> level + 1,
                ignored -> COLORLESS,
                moves -> moves.stream().map(transformMove).collect(toList()),
                PokemonCard::new
        );

        PokemonCard toTransform = PokemonCard.of(
                "Bulbasaur",
                70,
                GRASS,
                asList(
                        Move.of(
                                asList(COLORLESS, GRASS),
                                "Razor Leaf",
                                30
                        )
                )
        );

        PokemonCard expected = PokemonCard.of(
                "BULBASAUR",
                71,
                COLORLESS,
                asList(
                        Move.of(
                                asList(COLORLESS, COLORLESS, GRASS, GRASS),
                                "Razor Leaf!",
                                60
                        )
                )
        );

        PokemonCard toVerify = transformPokemonCard.apply(toTransform);

        assertEquals(expected, toVerify);
    }
}
