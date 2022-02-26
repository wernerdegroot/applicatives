package nl.wernerdegroot.applicatives.prelude;

import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ZipListsTest {

    @Test
    public void combine() {
        List<String> name = asList("Bulbasaur", "Charmander", "Squirtle");
        List<Integer> hp = asList(10, 20, 30, 40, 50, 60);
        List<EnergyType> energyType = asList(EnergyType.GRASS, EnergyType.FIRE, EnergyType.WATER);
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
                        "Charmander",
                        20,
                        EnergyType.FIRE,
                        asList(
                                Move.of(asList(EnergyType.COLORLESS), "Scratch", 10),
                                Move.of(asList(EnergyType.FIRE, EnergyType.COLORLESS), "Ember", 30)
                        )
                )
        );
        List<PokemonCard> toVerify = new ZipLists().combine(name, hp, energyType, moves, PokemonCard::new);
        assertEquals(expected, toVerify);
    }
}
