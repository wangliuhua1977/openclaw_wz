package com.openclaw.oneclick.core.steps;

import com.openclaw.oneclick.core.engine.StepContext;
import com.openclaw.oneclick.core.exec.CommandResult;

import java.time.Duration;

public class InstallOpenClawStep extends BaseStep {
    @Override public String id() { return "install-openclaw"; }
    @Override public String title() { return "Step 4: 安装 OpenClaw"; }

    @Override
    public void run(StepContext context) throws Exception {
        String cmd;
        if (context.settings().useInstaller()) {
            cmd = "curl -fsSL https://openclaw.ai/install.sh | bash -s -- --no-onboard";
        } else {
            cmd = "curl -fsSL https://deb.nodesource.com/setup_22.x | sudo -E bash - && sudo apt-get install -y nodejs && npm install -g openclaw@latest";
        }
        CommandResult result = runWsl(context, cmd, Duration.ofMinutes(25));
        ensureOk(result, "OpenClaw 安装失败");
    }
}
