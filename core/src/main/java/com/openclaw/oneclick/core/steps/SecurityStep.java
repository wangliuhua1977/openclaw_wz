package com.openclaw.oneclick.core.steps;

import com.openclaw.oneclick.core.engine.StepContext;

import java.time.Duration;

public class SecurityStep extends BaseStep {
    @Override public String id() { return "security"; }
    @Override public String title() { return "Step 8: 安全加固"; }

    @Override
    public void run(StepContext context) throws Exception {
        runWsl(context, "openclaw security audit --fix || true", Duration.ofMinutes(3));
        runWsl(context, "openclaw config get gateway.bind && openclaw config get gateway.auth.mode", Duration.ofSeconds(30));
        context.output().append("默认安全策略：loopback + token，skills 默认关闭。");
    }
}
