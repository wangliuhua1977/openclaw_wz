package com.openclaw.oneclick.core.steps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclaw.oneclick.core.engine.StepContext;
import com.openclaw.oneclick.core.exec.CommandResult;
import com.openclaw.oneclick.core.util.DashboardUrlBuilder;

import java.time.Duration;

public class TokenStep extends BaseStep {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override public String id() { return "read-token"; }
    @Override public String title() { return "Step 6: 读取 Gateway Token"; }

    @Override
    public void run(StepContext context) throws Exception {
        CommandResult result = runWsl(context, "cat ~/.openclaw/openclaw.json", Duration.ofSeconds(30));
        ensureOk(result, "读取 openclaw.json 失败");
        JsonNode token = mapper.readTree(result.stdout()).path("gateway").path("auth").path("token");
        if (!token.isTextual()) {
            throw new IllegalStateException("openclaw.json 中未发现 gateway.auth.token");
        }
        String url = DashboardUrlBuilder.build(context.settings().gatewayPort(), token.asText());
        context.output().append("Dashboard URL: " + url);
    }
}
