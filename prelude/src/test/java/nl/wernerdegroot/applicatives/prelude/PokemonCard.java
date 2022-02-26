package nl.wernerdegroot.applicatives.prelude;

import java.util.List;
import java.util.Objects;

public class PokemonCard {

    private final String name;
    private final int hp;
    private final EnergyType energyType;
    private final List<Move> moves;

    public PokemonCard(String name, int hp, EnergyType energyType, List<Move> moves) {
        this.name = name;
        this.hp = hp;
        this.energyType = energyType;
        this.moves = moves;
    }

    public static PokemonCard of(String name, int hp, EnergyType energyType, List<Move> moves) {
        return new PokemonCard(name, hp, energyType, moves);
    }

    public String getName() {
        return name;
    }

    public int getHp() {
        return hp;
    }

    public EnergyType getEnergyType() {
        return energyType;
    }

    public List<Move> getMoves() {
        return moves;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PokemonCard that = (PokemonCard) o;
        return getHp() == that.getHp() && getName().equals(that.getName()) && getEnergyType() == that.getEnergyType() && getMoves().equals(that.getMoves());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getHp(), getEnergyType(), getMoves());
    }

    @Override
    public String toString() {
        return "PokemonCard{" +
                "name='" + name + '\'' +
                ", hp=" + hp +
                ", energyType=" + energyType +
                ", moves=" + moves +
                '}';
    }
}
