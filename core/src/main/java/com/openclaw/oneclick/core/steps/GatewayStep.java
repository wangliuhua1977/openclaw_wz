package com.openclaw.oneclick.core.steps;

import com.openclaw.oneclick.core.engine.StepContext;
import com.openclaw.oneclick.core.exec.CommandResult;

import java.time.Duration;

public class GatewayStep extends BaseStep {
    @Override public String id() { return "gateway"; }
    @Override public String title() { return "Step 7: 启动并验证 Gateway"; }

    @Override
    public void run(StepContext context) throws Exception {
        if (context.settings().installDaemon()) {
            runWsl(context, "openclaw gateway status || openclaw gateway start", Duration.ofMinutes(2));
        } else {
            runWsl(context, "nohup openclaw gateway start > ~/.openclaw/gateway.log 2>&1 &", Duration.ofSeconds(30));
        }
        CommandResult health = runWsl(context, "openclaw health || true", Duration.ofSeconds(30));
        context.output().append("Gateway 健康检查输出: " + health.stdout());
    }
}
