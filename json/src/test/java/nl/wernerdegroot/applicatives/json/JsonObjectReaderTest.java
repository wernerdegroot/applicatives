package nl.wernerdegroot.applicatives.json;

import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static javax.json.JsonValue.ValueType.TRUE;
import static nl.wernerdegroot.applicatives.json.EnergyType.COLORLESS;
import static nl.wernerdegroot.applicatives.json.EnergyType.GRASS;
import static nl.wernerdegroot.applicatives.json.Key.key;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonObjectReaderTest {

    private final JsonReader<EnergyType> energyTypeReader = Json.STRING.validate((str, ctx) -> {
        try {
            return EnergyType.valueOf(str);
        } catch (IllegalArgumentException e) {
            return ctx.notifyFailure("json.parse.error.unknownEnergyType", str);
        }
    });

    private final JsonReader<Move> moveReader = Json.instance().reader(
            key("cost").using(energyTypeReader.list()),
            key("name").asString(),
            key("damage").asInt(),
            Move::new
    );

    private final JsonReader<PokemonCard> pokemonCardReader = Json.instance().reader(
            key("name").asString(),
            key("hp").asInt(),
            key("energyType").using(energyTypeReader),
            key("moves").using(moveReader.list()),
            PokemonCard::new
    );

    @Test
    public void givenValidPokemonCard() {
        String toRead = "{\"name\":\"Bulbasaur\",\"hp\":60,\"energyType\":\"GRASS\",\"moves\":[{\"cost\":[\"GRASS\"],\"name\":\"Tackle\",\"damage\":10},{\"cost\":[\"GRASS\",\"COLORLESS\",\"COLORLESS\"],\"name\":\"Razor Leaf\",\"damage\":30}]}";

        Json.Result<PokemonCard> expected = Json.success(
                PokemonCard.of(
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
                )
        );

        Json.Result<PokemonCard> toVerify = pokemonCardReader.readString(toRead);

        assertEquals(expected, toVerify);
    }

    @Test
    public void givenInvalidPokemonCard() {
        String toRead = "{\"name\":\"Bulbasaur\",\"hp\":60,\"energyType\":\"MAGIC\",\"moves\":[{\"cost\":[\"GRASS\"],\"name\":\"Tackle\",\"damage\":true},{\"cost\":[\"GRASS\",\"SAND\",\"COLORLESS\"],\"name\":\"Razor Leaf\",\"damage\":30}]}";

        Json.Result<PokemonCard> expected = Json.failed(
                asList(
                        Failure.of("energyType", "json.parse.error.unknownEnergyType", "MAGIC"),
                        Failure.of("moves.0.damage", "json.parse.error.notANumber", TRUE),
                        Failure.of("moves.1.cost.1", "json.parse.error.unknownEnergyType", "SAND")
                )
        );
        Json.Result<PokemonCard> toVerify = pokemonCardReader.readString(toRead);

        assertEquals(expected, toVerify);
    }
}
