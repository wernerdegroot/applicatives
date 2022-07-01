package nl.wernerdegroot.applicatives.json;

import java.util.*;

import static nl.wernerdegroot.applicatives.json.ReadResult.FAILED;
import static nl.wernerdegroot.applicatives.json.ReadResult.SUCCESS;

/**
 * To hold the status of reading JSON (did we encounter validation
 * errors or not?), track the path at which we are currently parsing,
 * and to signal failures.
 */
public class ValidationContext {

    private final Deque<String> path = new ArrayDeque<>();
    private final Deque<ReadResult> readResults = new ArrayDeque<>();
    private final List<Failure> failures = new ArrayList<>();
    private ReadResult readResult = SUCCESS;

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
        // Assume success:
        readResults.push(SUCCESS);
    }

    public <T> T notifyFailure(String errorMessageKey, Object... arguments) {
        // Update status:
        readResult = FAILED;

        // Clear the results. They are all `FAILED` now:
        readResults.clear();

        // Add failure to the list:
        failures.add(Failure.of(resolvePath(), errorMessageKey, arguments));

        return null;
    }

    // Finishes the current read operation and returns whether it was successful or not.
    public ReadResult finishReading() {
        if (readResults.isEmpty()) {
            return readResult;
        } else {
            return readResults.pop();
        }
    }
}
