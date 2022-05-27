package nl.wernerdegroot.applicatives.prelude;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ListsTest {

    @Test
    public void combine() {
        List<String> name = asList("Bulbasaur", "Charmander", "Squirtle");
        List<Integer> hp = asList(10);
        List<EnergyType> energyType = asList(EnergyType.GRASS, EnergyType.FIRE);
        List<List<Move>> moves = asList(
                asList(
                        Move.of(asList(EnergyType.GRASS, EnergyType.COLORLESS), "Razor Leaf", 30)
                ),
                asList(
                        Move.of(asList(EnergyType.COLORLESS), "Scratch", 10),
                        Move.of(asList(EnergyType.FIRE, EnergyType.COLORLESS), "Ember", 30)
                )
        );
        List<PokemonCard> expected = asList(
                PokemonCard.of(
                        "Bulbasaur",
                        10,
                        EnergyType.GRASS,
                        asList(
                                Move.of(asList(EnergyType.GRASS, EnergyType.COLORLESS), "Razor Leaf", 30)
                        )
                ),
                PokemonCard.of(
                        "Bulbasaur",
                        10,
                        EnergyType.GRASS,
                        asList(
                                Move.of(asList(EnergyType.COLORLESS), "Scratch", 10),
                                Move.of(asList(EnergyType.FIRE, EnergyType.COLORLESS), "Ember", 30)
                        )
                ),
                PokemonCard.of(
                        "Bulbasaur",
                        10,
                        EnergyType.FIRE,
                        asList(
                                Move.of(asList(EnergyType.GRASS, EnergyType.COLORLESS), "Razor Leaf", 30)
                        )
                ),
                PokemonCard.of(
                        "Bulbasaur",
                        10,
                        EnergyType.FIRE,
                        asList(
                                Move.of(asList(EnergyType.COLORLESS), "Scratch", 10),
                                Move.of(asList(EnergyType.FIRE, EnergyType.COLORLESS), "Ember", 30)
                        )
                ),
                PokemonCard.of(
                        "Charmander",
                        10,
                        EnergyType.GRASS,
                        asList(
                                Move.of(asList(EnergyType.GRASS, EnergyType.COLORLESS), "Razor Leaf", 30)
                        )
                ),
                PokemonCard.of(
                        "Charmander",
                        10,
                        EnergyType.GRASS,
                        asList(
                                Move.of(asList(EnergyType.COLORLESS), "Scratch", 10),
                                Move.of(asList(EnergyType.FIRE, EnergyType.COLORLESS), "Ember", 30)
                        )
                ),
                PokemonCard.of(
                        "Charmander",
                        10,
                        EnergyType.FIRE,
                        asList(
                                Move.of(asList(EnergyType.GRASS, EnergyType.COLORLESS), "Razor Leaf", 30)
                        )
                ),
                PokemonCard.of(
                        "Charmander",
                        10,
                        EnergyType.FIRE,
                        asList(
                                Move.of(asList(EnergyType.COLORLESS), "Scratch", 10),
                                Move.of(asList(EnergyType.FIRE, EnergyType.COLORLESS), "Ember", 30)
                        )
                ),
                PokemonCard.of(
                        "Squirtle",
                        10,
                        EnergyType.GRASS,
                        asList(
                                Move.of(asList(EnergyType.GRASS, EnergyType.COLORLESS), "Razor Leaf", 30)
                        )
                ),
                PokemonCard.of(
                        "Squirtle",
                        10,
                        EnergyType.GRASS,
                        asList(
                                Move.of(asList(EnergyType.COLORLESS), "Scratch", 10),
                                Move.of(asList(EnergyType.FIRE, EnergyType.COLORLESS), "Ember", 30)
                        )
                ),
                PokemonCard.of(
                        "Squirtle",
                        10,
                        EnergyType.FIRE,
                        asList(
                                Move.of(asList(EnergyType.GRASS, EnergyType.COLORLESS), "Razor Leaf", 30)
                        )
                ),
                PokemonCard.of(
                        "Squirtle",
                        10,
                        EnergyType.FIRE,
                        asList(
                                Move.of(asList(EnergyType.COLORLESS), "Scratch", 10),
                                Move.of(asList(EnergyType.FIRE, EnergyType.COLORLESS), "Ember", 30)
                        )
                )
        );
        List<PokemonCard> toVerify = Lists.instance().combine(name, hp, energyType, moves, PokemonCard::new);
        assertEquals(expected, toVerify);
    }
}
