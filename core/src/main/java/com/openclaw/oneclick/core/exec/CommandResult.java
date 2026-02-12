package com.openclaw.oneclick.core.exec;

public record CommandResult(int exitCode, String stdout, String stderr, boolean timedOut) {
    public boolean success() {
        return exitCode == 0 && !timedOut;
    }
}
