package com.openclaw.oneclick.core.exec;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public record CommandSpec(List<String> command, Duration timeout, Map<String, String> environment, String description) {
    public static CommandSpec of(List<String> command, Duration timeout, String description) {
        return new CommandSpec(command, timeout, Map.of(), description);
    }
}
