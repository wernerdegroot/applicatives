package nl.wernerdegroot.applicatives.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Collections.emptyList;

public class Path {
    private final List<String> components;

    public Path(List<String> components) {
        this.components = components;
    }

    public static Path empty() {
        return new Path(emptyList());
    }

    public Path prepend(String component) {
        List<String> updatedComponents = new ArrayList<>();
        updatedComponents.add(component);
        updatedComponents.addAll(components);
        return new Path(updatedComponents);
    }

    public Path append(String component) {
        List<String> updatedComponents = new ArrayList<>();
        updatedComponents.addAll(components);
        updatedComponents.add(component);
        return new Path(updatedComponents);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Path path = (Path) o;
        return Objects.equals(components, path.components);
    }

    @Override
    public int hashCode() {
        return Objects.hash(components);
    }

    @Override
    public String toString() {
        return "Path{" +
                "components=" + components +
                '}';
    }
}
