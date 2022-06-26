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

    private final JsonObjectFormat<Move> moveFormat = Json.instance().formats(
            key("cost").formatUsing(energyTypeFormat.list()),
            key("name").formatString(),
            key("damage").formatInt(),
            Move::new
    );

    private final JsonObjectFormat<PokemonCard> pokemonCardFormat = Json.instance().formats(
            key("name").formatString(),
            key("hp").formatInt(),
            key("energyType").formatUsing(energyTypeFormat),
            key("moves").formatUsing(moveFormat.list()),
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
