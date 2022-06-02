package nl.wernerdegroot.applicatives.prelude;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

public class CompletableFuturesTest {

    @Test
    public void combine() {
        CompletableFuture<String> nameFuture = new CompletableFuture<>();
        CompletableFuture<Integer> hpFuture = new CompletableFuture<>();
        CompletableFuture<EnergyType> energyTypeFuture = new CompletableFuture<>();
        CompletableFuture<List<Move>> movesFuture = new CompletableFuture<>();

        CompletableFuture<PokemonCard> pokemonCardFuture = CompletableFutures.instance().combine(nameFuture, hpFuture, energyTypeFuture, movesFuture, PokemonCard::new);

        assertFalse(pokemonCardFuture.isDone());
        nameFuture.complete("Bulbasaur");
        assertFalse(pokemonCardFuture.isDone());
        hpFuture.complete(10);
        assertFalse(pokemonCardFuture.isDone());
        energyTypeFuture.complete(EnergyType.GRASS);
        assertFalse(pokemonCardFuture.isDone());
        movesFuture.complete(asList(Move.of(asList(EnergyType.GRASS, EnergyType.COLORLESS), "Razor Leaf", 30)));

        // Finally done.
        assertTrue(pokemonCardFuture.isDone());

        // Does it contain a `PokemonCard`?
        PokemonCard expected = PokemonCard.of(
                "Bulbasaur",
                10,
                EnergyType.GRASS,
                asList(Move.of(asList(EnergyType.GRASS, EnergyType.COLORLESS), "Razor Leaf", 30))
        );
        PokemonCard toVerify = pokemonCardFuture.join();
        assertEquals(expected, toVerify);
    }
}
