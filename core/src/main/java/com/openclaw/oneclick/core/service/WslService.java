package com.openclaw.oneclick.core.service;

import com.openclaw.oneclick.core.exec.CommandResult;
import com.openclaw.oneclick.core.exec.CommandRunner;
import com.openclaw.oneclick.core.exec.CommandSpec;

import java.time.Duration;
import java.util.List;

public class WslService {
    private final CommandRunner runner;

    public WslService(CommandRunner runner) {
        this.runner = runner;
    }

    public CommandResult runWsl(String distro, String cmd, Duration timeout, com.openclaw.oneclick.core.exec.CommandListener listener) throws Exception {
        return runner.run(CommandSpec.of(List.of("wsl.exe", "-d", distro, "--", "bash", "-lc", cmd), timeout, cmd), listener);
    }

    public CommandResult runWindows(List<String> cmd, Duration timeout, com.openclaw.oneclick.core.exec.CommandListener listener) throws Exception {
        return runner.run(CommandSpec.of(cmd, timeout, String.join(" ", cmd)), listener);
    }
}
