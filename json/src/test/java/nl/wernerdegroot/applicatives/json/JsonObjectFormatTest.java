package nl.wernerdegroot.applicatives.json;

import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static nl.wernerdegroot.applicatives.json.EnergyType.COLORLESS;
import static nl.wernerdegroot.applicatives.json.EnergyType.GRASS;
import static nl.wernerdegroot.applicatives.json.Key.key;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonObjectFormatTest {

    private final JsonFormat<EnergyType> energyTypeFormat = JsonFormat.of(
            Json.STRING.validate((str, ctx) -> {
                try {
                    return EnergyType.valueOf(str);
                } catch (IllegalArgumentException e) {
                    return ctx.notifyFailure("json.parse.error.unknownEnergyType", str);
                }
            }),
            Json.STRING.contramap(EnergyType::name)
    );

    private final JsonFormat<Move> moveFormat = Json.instance().format(
            key("cost").using(energyTypeFormat.list()),
            key("name").asString(),
            key("damage").asInt(),
            Move::new
    );

    private final JsonFormat<PokemonCard> pokemonCardFormat = Json.instance().format(
            key("name").asString(),
            key("hp").asInt(),
            key("energyType").using(energyTypeFormat),
            key("moves").using(moveFormat.list()),
            PokemonCard::new
    );

    @Test
    public void givenValidPokemonCard() {
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

        Json.Result<PokemonCard> expected = Json.success(bulbasaur);
        Json.Result<PokemonCard> toVerify = pokemonCardFormat.readString(pokemonCardFormat.writeString(bulbasaur));

        assertEquals(expected, toVerify);
    }
}
