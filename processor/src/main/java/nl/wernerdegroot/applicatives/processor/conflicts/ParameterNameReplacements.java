package nl.wernerdegroot.applicatives.processor.conflicts;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Contains a mapping between the parameter names the programmer supplied
 * and their conflict-free alternatives. If no potential conflicts are found
 * this mapping will map each of the programmer's parameter names to itself.
 */
class ParameterNameReplacements {

    private final Map<String, String> parameterNameReplacements;

    public ParameterNameReplacements(Map<String, String> parameterNameReplacements) {
        this.parameterNameReplacements = parameterNameReplacements;
    }

    public static ParameterNameReplacements of(Map<String, String> parameterNameReplacements) {
        return new ParameterNameReplacements(parameterNameReplacements);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Map<String, String> getParameterNameReplacements() {
        return parameterNameReplacements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParameterNameReplacements that = (ParameterNameReplacements) o;
        return getParameterNameReplacements().equals(that.getParameterNameReplacements());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getParameterNameReplacements());
    }

    @Override
    public String toString() {
        return "ParameterNameReplacements{" +
                "parameterNameReplacements=" + parameterNameReplacements +
                '}';
    }

    public static class Builder {

        private final Map<String, String> parameterNameReplacements = new HashMap<>();

        public Builder replaceSecondaryParameterName(String toReplace, String replacement) {
            parameterNameReplacements.put(toReplace, replacement);
            return this;
        }

        public ParameterNameReplacements build() {
            return ParameterNameReplacements.of(parameterNameReplacements);
        }
    }
}
