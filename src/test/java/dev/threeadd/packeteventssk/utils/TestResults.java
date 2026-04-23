package dev.threeadd.packeteventssk.test.utils;

import java.util.Map;
import java.util.Set;

public class TestResults {
    private final Set<String> succeeded;
    private final Map<String, String> failed;
    private final boolean docsFailed;

    public TestResults(Set<String> succeeded, Map<String, String> failed, boolean docs_failed) {
        this.docsFailed = docs_failed;
        this.succeeded = succeeded;
        this.failed = failed;
    }

    public Set<String> getSucceeded() { return succeeded; }
    public Map<String, String> getFailed() { return failed; }
    public boolean docsFailed() { return docsFailed; }
}