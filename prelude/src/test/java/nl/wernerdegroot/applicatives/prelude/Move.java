package nl.wernerdegroot.applicatives.prelude;

import nl.wernerdegroot.applicatives.runtime.Function25;
import nl.wernerdegroot.applicatives.runtime.Function3;
import nl.wernerdegroot.applicatives.runtime.decompositions.Decomposable3;

import java.util.List;
import java.util.Objects;

public class Move implements Decomposable3<List<EnergyType>, String, Integer> {
    private final List<EnergyType> cost;
    private final String name;
    private final int damage;

    public Move(List<EnergyType> cost, String name, int damage) {
        this.cost = cost;
        this.name = name;
        this.damage = damage;
    }

    public static Move of(List<EnergyType> cost, String name, int damage) {
        return new Move(cost, name, damage);
    }

    @Override
    public <T> T decomposeTo(Function3<? super List<EnergyType>, ? super String, ? super Integer, ? extends T> fn) {
        return fn.apply(cost, name, damage);
    }

    public List<EnergyType> getCost() {
        return cost;
    }

    public String getName() {
        return name;
    }

    public int getDamage() {
        return damage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return damage == move.damage && cost.equals(move.cost) && name.equals(move.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cost, name, damage);
    }

    @Override
    public String toString() {
        return "Move{" +
                "cost=" + cost +
                ", name='" + name + '\'' +
                ", damage=" + damage +
                '}';
    }
}
