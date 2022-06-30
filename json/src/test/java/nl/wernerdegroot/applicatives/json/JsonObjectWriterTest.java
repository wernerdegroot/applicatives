package nl.wernerdegroot.applicatives.json;

import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static nl.wernerdegroot.applicatives.json.EnergyType.COLORLESS;
import static nl.wernerdegroot.applicatives.json.EnergyType.GRASS;
import static nl.wernerdegroot.applicatives.json.Json.intWriter;
import static nl.wernerdegroot.applicatives.json.Json.stringWriter;
import static nl.wernerdegroot.applicatives.json.Key.key;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonObjectWriterTest {

    private final JsonWriter<EnergyType> energyTypeWriter = Json.stringFormat.contramap(EnergyType::name);

    private final JsonObjectWriter<Move> moveWriter = Json.instance().writer(
            key("cost").using(energyTypeWriter.list()),
            key("name").using(stringWriter),
            key("damage").using(intWriter)
    );

    private final JsonObjectWriter<PokemonCard> pokemonCardWriter = Json.instance().writer(
            key("name").using(stringWriter),
            key("hp").using(intWriter),
            key("energyType").using(energyTypeWriter),
            key("moves").using(moveWriter.list())
    );

    @Test
    public void givenPokemonCard() {
        PokemonCard bulbasaur = PokemonCard.of(
                "Bulbasaur",
                60,
                GRASS,
                asList(
                        Move.of(
                                asList(GRASS),
                                "Tackle",
                                10
                        ),
                        Move.of(
                                asList(GRASS, COLORLESS, COLORLESS),
                                "Razor Leaf",
                                30
                        )
                )
        );

        String expected = "{\"name\":\"Bulbasaur\",\"hp\":60,\"energyType\":\"GRASS\",\"moves\":[{\"cost\":[\"GRASS\"],\"name\":\"Tackle\",\"damage\":10},{\"cost\":[\"GRASS\",\"COLORLESS\",\"COLORLESS\"],\"name\":\"Razor Leaf\",\"damage\":30}]}";
        String toVerify = pokemonCardWriter.writeString(bulbasaur);

        assertEquals(expected, toVerify);
    }
}
