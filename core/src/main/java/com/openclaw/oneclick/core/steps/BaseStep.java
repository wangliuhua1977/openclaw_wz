package com.openclaw.oneclick.core.steps;

import com.openclaw.oneclick.core.engine.Step;
import com.openclaw.oneclick.core.engine.StepContext;
import com.openclaw.oneclick.core.exec.CommandResult;

import java.time.Duration;

public abstract class BaseStep implements Step {
    protected CommandResult runWsl(StepContext ctx, String cmd, Duration timeout) throws Exception {
        return ctx.wslService().runWsl(ctx.settings().distro(), cmd, timeout, (line, err) -> ctx.output().append((err ? "[ERR] " : "") + line));
    }

    protected void ensureOk(CommandResult result, String message) {
        if (!result.success()) {
            throw new IllegalStateException(message + "\n可手工执行命令：" + result.stderr());
        }
    }
}
