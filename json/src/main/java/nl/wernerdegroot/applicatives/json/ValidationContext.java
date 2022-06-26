package nl.wernerdegroot.applicatives.json;

import java.util.*;

public class ValidationContext {

    private final Deque<String> path = new ArrayDeque<>();
    private final Deque<Boolean> marks = new ArrayDeque<>();
    private final List<Failure> failures = new ArrayList<>();
    private boolean isValid = true;

    public List<Failure> getFailures() {
        return failures;
    }

    public void pushKey(String key) {
        path.push(key);
    }

    public void popKey() {
        path.pop();
    }

    public String resolvePath() {
        StringJoiner joiner = new StringJoiner(".");
        Iterator<String> keyIterator = path.descendingIterator();
        while (keyIterator.hasNext()) {
            joiner.add(keyIterator.next());
        }
        return joiner.toString();
    }

    public void startReading() {
        marks.push(true);
    }

    public <T> T notifyFailure(String errorMessageKey, Object... arguments) {
        // Update marks:
        marks.clear();

        // Update status:
        isValid = false;

        // Add failure to the list:
        failures.add(Failure.of(resolvePath(), errorMessageKey, arguments));

        return null;
    }

    // Finishes the current read operation and returns whether it was successful or not.
    public boolean finishReading() {
        if (marks.isEmpty()) {
            return isValid;
        } else {
            return marks.pop();
        }
    }
}
