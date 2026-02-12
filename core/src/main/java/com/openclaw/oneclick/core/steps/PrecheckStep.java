package com.openclaw.oneclick.core.steps;

import com.openclaw.oneclick.core.engine.StepContext;
import com.openclaw.oneclick.core.exec.CommandResult;

import java.time.Duration;
import java.util.List;

public class PrecheckStep extends BaseStep {
    @Override public String id() { return "precheck"; }
    @Override public String title() { return "Step 1: Windows 环境预检"; }
    @Override public boolean needsAdmin() { return true; }

    @Override
    public void run(StepContext context) throws Exception {
        CommandResult wsl = context.wslService().runWindows(List.of("wsl.exe", "--status"), Duration.ofSeconds(30),
                (line, err) -> context.output().append(line));
        context.output().append("手工排障：wsl --status / wsl --version / wsl --update");
        if (!wsl.success()) {
            context.output().append("WSL 当前不可用，后续步骤将尝试安装。请确认已启用虚拟化和 Windows 功能。 ");
        }
    }
}
