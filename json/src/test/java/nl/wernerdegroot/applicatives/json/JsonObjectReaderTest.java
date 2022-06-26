package nl.wernerdegroot.applicatives.json;

import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static nl.wernerdegroot.applicatives.json.EnergyType.COLORLESS;
import static nl.wernerdegroot.applicatives.json.EnergyType.GRASS;
import static nl.wernerdegroot.applicatives.json.Key.key;
import static nl.wernerdegroot.applicatives.json.Validated.invalid;
import static nl.wernerdegroot.applicatives.json.Validated.valid;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonObjectReaderTest {

    private final JsonReader<EnergyType> energyTypeReader = Json.STRING.validate(s -> {
        try {
            return valid(EnergyType.valueOf(s));
        } catch (IllegalArgumentException e) {
            return invalid( "json.parse.error.unknownEnergyType", s);
        }
    });

    private final JsonObjectReader<Move> moveReader = Json.instance().readers(
            key("cost").readUsing(energyTypeReader.list()),
            key("name").readString(),
            key("damage").readInt(),
            Move::new
    );

    private final JsonObjectReader<PokemonCard> pokemonCardReader = Json.instance().readers(
            key("name").readString(),
            key("hp").readInt(),
            key("energyType").readUsing(energyTypeReader),
            key("moves").readUsing(moveReader.list()),
            PokemonCard::new
    );

    @Test
    public void givenValidPokemonCard() {
        String toRead = "{\"name\":\"Bulbasaur\",\"hp\":60,\"energyType\":\"GRASS\",\"moves\":[{\"cost\":[\"GRASS\"],\"name\":\"Tackle\",\"damage\":10},{\"cost\":[\"GRASS\",\"COLORLESS\",\"COLORLESS\"],\"name\":\"Razor Leaf\",\"damage\":30}]}";

        PokemonCard expected = PokemonCard.of(
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

        Json.Result<PokemonCard> toVerify = pokemonCardReader.readString(toRead);

        assertTrue(toVerify.isSuccess());
        assertEquals(expected, toVerify.get());
    }

    @Test
    public void givenInvalidPokemonCard() {
        String toRead = "{\"name\":\"Bulbasaur\",\"hp\":60,\"energyType\":\"MAGIC\",\"moves\":[{\"cost\":[\"GRASS\"],\"name\":\"Tackle\",\"damage\":true},{\"cost\":[\"GRASS\",\"COLORLESS\",\"COLORLESS\"],\"name\":\"Razor Leaf\",\"damage\":30}]}";

        PokemonCard expected = PokemonCard.of(
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

        Json.Result<PokemonCard> toVerify = pokemonCardReader.readString(toRead);

        assertTrue(toVerify.isSuccess());
        assertEquals(expected, toVerify.get());
    }
}
