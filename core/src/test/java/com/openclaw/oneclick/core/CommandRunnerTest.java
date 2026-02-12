package com.openclaw.oneclick.core;

import com.openclaw.oneclick.core.exec.CommandRunner;
import com.openclaw.oneclick.core.exec.CommandSpec;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandRunnerTest {
    @Test
    void shouldTimeoutLongCommand() throws Exception {
        CommandRunner runner = new CommandRunner();
        var result = runner.run(CommandSpec.of(List.of("bash", "-lc", "sleep 2"), Duration.ofMillis(300), "sleep"), (l, e) -> {});
        assertTrue(result.timedOut());
    }

    @Test
    void shouldCancelRunningProcess() throws Exception {
        CommandRunner runner = new CommandRunner();
        Thread t = new Thread(() -> {
            try {
                runner.run(CommandSpec.of(List.of("bash", "-lc", "sleep 5"), Duration.ofSeconds(10), "sleep"), (l, e) -> {});
            } catch (Exception ignored) {
            }
        });
        t.start();
        Thread.sleep(200);
        runner.cancel();
        t.join(2000);
        assertTrue(!t.isAlive());
    }
}
