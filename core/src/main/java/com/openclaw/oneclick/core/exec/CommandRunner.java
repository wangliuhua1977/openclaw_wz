package com.openclaw.oneclick.core.exec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class CommandRunner {
    private static final Logger log = LoggerFactory.getLogger(CommandRunner.class);
    private final ExecutorService ioPool = Executors.newCachedThreadPool();
    private final AtomicReference<Process> runningProcess = new AtomicReference<>();

    public CommandResult run(CommandSpec spec, CommandListener listener) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(spec.command());
        pb.environment().putAll(spec.environment());
        Process process = pb.start();
        runningProcess.set(process);

        StringBuilder out = new StringBuilder();
        StringBuilder err = new StringBuilder();

        Future<?> outTask = ioPool.submit(() -> stream(process.inputReader(StandardCharsets.UTF_8), false, listener, out));
        Future<?> errTask = ioPool.submit(() -> stream(process.errorReader(StandardCharsets.UTF_8), true, listener, err));

        boolean timedOut = false;
        Duration timeout = spec.timeout() == null ? Duration.ofMinutes(15) : spec.timeout();
        if (!process.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS)) {
            timedOut = true;
            listener.onLine("[TIMEOUT] 命令超时: " + spec.description(), true);
            process.destroyForcibly();
        }
        int exit = process.isAlive() ? -1 : process.exitValue();

        waitQuietly(outTask);
        waitQuietly(errTask);

        if (timedOut) {
            exit = -1;
        }
        runningProcess.set(null);
        return new CommandResult(exit, out.toString(), err.toString(), timedOut);
    }

    public void cancel() {
        Process p = runningProcess.get();
        if (p != null && p.isAlive()) {
            log.warn("Cancelling process");
            p.destroyForcibly();
        }
    }

    private static void stream(BufferedReader reader, boolean err, CommandListener listener, StringBuilder collector) {
        reader.lines().forEach(line -> {
            collector.append(line).append(System.lineSeparator());
            listener.onLine(line, err);
        });
    }

    private static void waitQuietly(Future<?> task) {
        try {
            task.get(30, TimeUnit.SECONDS);
        } catch (Exception ignored) {
        }
    }
}
